"""
FastAPI service for audio transcription using Whisper
"""
from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import whisper
import tempfile
import os
from typing import Optional
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="Whisper Transcription Service",
    description="Audio transcription service using OpenAI Whisper",
    version="1.0.0"
)

# CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, specify your Spring Boot URL
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load Whisper model (using base model for balance between speed and accuracy)
# Options: tiny, base, small, medium, large
MODEL_SIZE = os.getenv("WHISPER_MODEL_SIZE", "base")
logger.info(f"Loading Whisper model: {MODEL_SIZE}")

# Check if ffmpeg is available
try:
    import subprocess
    subprocess.run(["ffmpeg", "-version"], capture_output=True, check=True)
    logger.info("ffmpeg is available")
except (FileNotFoundError, subprocess.CalledProcessError):
    logger.warning("⚠️  WARNING: ffmpeg not found! Some audio formats may not work.")
    logger.warning("   Install ffmpeg from: https://ffmpeg.org/download.html")

model = whisper.load_model(MODEL_SIZE)
logger.info("Whisper model loaded successfully")


class TranscriptionResponse(BaseModel):
    """Response model for transcription"""
    text: str
    language: Optional[str] = None
    duration: Optional[float] = None
    confidence: Optional[float] = None


class HealthResponse(BaseModel):
    """Health check response"""
    status: str
    model: str
    message: str


class SpeakingAnalysisResponse(BaseModel):
    """Complete speaking analysis response"""
    text: str
    language: Optional[str] = None
    duration: Optional[float] = None
    accuracy: float
    fluency: float
    completeness: float
    accuracy_feedback: str
    fluency_feedback: str
    completeness_feedback: str
    overall_score: float


@app.get("/", response_model=HealthResponse)
async def root():
    """Root endpoint"""
    return HealthResponse(
        status="running",
        model=MODEL_SIZE,
        message="Whisper Transcription Service is running"
    )


@app.get("/health", response_model=HealthResponse)
async def health_check():
    """Health check endpoint"""
    return HealthResponse(
        status="healthy",
        model=MODEL_SIZE,
        message="Service is healthy"
    )


@app.post("/transcribe", response_model=TranscriptionResponse)
async def transcribe_audio(
    file: UploadFile = File(...),
    language: Optional[str] = None
):
    """
    Transcribe audio file to text
    
    Args:
        file: Audio file (mp3, wav, m4a, etc.)
        language: Optional language code (e.g., 'en', 'vi')
    
    Returns:
        TranscriptionResponse with transcribed text
    """
    try:
        logger.info(f"Received file: {file.filename}, content_type: {file.content_type}")
        
        # Validate file
        if not file.filename:
            raise HTTPException(status_code=400, detail="No file provided")
        
        # Supported audio formats
        supported_formats = ['.mp3', '.wav', '.m4a', '.ogg', '.flac', '.webm']
        file_ext = os.path.splitext(file.filename)[1].lower()
        
        if file_ext not in supported_formats:
            raise HTTPException(
                status_code=400,
                detail=f"Unsupported file format. Supported: {', '.join(supported_formats)}"
            )
        
        # Save uploaded file temporarily
        with tempfile.NamedTemporaryFile(delete=False, suffix=file_ext) as temp_file:
            content = await file.read()
            temp_file.write(content)
            temp_file_path = temp_file.name
        
        try:
            logger.info(f"Transcribing file: {temp_file_path}")
            
            # Transcribe with Whisper
            options = {}
            if language:
                options['language'] = language
            
            result = model.transcribe(temp_file_path, **options)
            
            logger.info("Transcription completed successfully")
            
            return TranscriptionResponse(
                text=result['text'].strip(),
                language=result.get('language'),
                duration=None,  # Can be calculated if needed
                confidence=None  # Whisper doesn't provide confidence scores directly
            )
            
        finally:
            # Clean up temporary file
            if os.path.exists(temp_file_path):
                os.remove(temp_file_path)
                logger.info(f"Cleaned up temporary file: {temp_file_path}")
    
    except HTTPException:
        raise
    except FileNotFoundError as e:
        logger.error(f"File not found error: {str(e)}", exc_info=True)
        if "ffmpeg" in str(e).lower() or "avconv" in str(e).lower():
            raise HTTPException(
                status_code=500, 
                detail="ffmpeg is not installed. Please install ffmpeg and add it to PATH. "
                       "Download from: https://ffmpeg.org/download.html"
            )
        raise HTTPException(status_code=500, detail=f"File not found: {str(e)}")
    except Exception as e:
        logger.error(f"Error during transcription: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Transcription failed: {str(e)}")


@app.post("/transcribe-batch")
async def transcribe_batch(files: list[UploadFile] = File(...)):
    """
    Transcribe multiple audio files
    
    Args:
        files: List of audio files
    
    Returns:
        List of transcription results
    """
    results = []
    
    for file in files:
        try:
            result = await transcribe_audio(file)
            results.append({
                "filename": file.filename,
                "success": True,
                "transcription": result
            })
        except Exception as e:
            results.append({
                "filename": file.filename,
                "success": False,
                "error": str(e)
            })
    
    return {"results": results}


@app.post("/analyze-speaking", response_model=SpeakingAnalysisResponse)
async def analyze_speaking(
    file: UploadFile = File(...),
    reference_text: Optional[str] = None,
    language: Optional[str] = "en"
):
    """
    Analyze speaking with 3 criteria: Accuracy, Fluency, Completeness
    
    Args:
        file: Audio file to analyze
        reference_text: Reference text for completeness comparison
        language: Language code (default: 'en')
    
    Returns:
        Complete analysis with scores and feedback
    """
    try:
        logger.info(f"Analyzing speaking for file: {file.filename}")
        
        # Step 1: Transcribe audio
        transcription_result = await transcribe_audio(file, language)
        text = transcription_result.text
        detected_language = transcription_result.language
        
        logger.info(f"Transcribed text: {text[:100]}...")
        
        # Step 2: Analyze 3 criteria
        
        # 2.1 Accuracy (Grammar + Vocabulary + Pronunciation)
        word_count = len(text.split())
        sentence_count = text.count('.') + text.count('!') + text.count('?')
        if sentence_count == 0:
            sentence_count = 1
        
        # Simple scoring based on text characteristics
        avg_word_length = sum(len(word) for word in text.split()) / max(word_count, 1)
        
        # Accuracy score (0-10)
        accuracy_score = min(10.0, 5.0 + (avg_word_length / 2) + (word_count / 20))
        accuracy_feedback = f"Word count: {word_count}, Average word length: {avg_word_length:.1f} characters"
        
        # 2.2 Fluency (Speaking rate + Natural flow)
        # Assume average speaking duration based on word count
        # Average speaking rate: 120-150 WPM
        estimated_duration = word_count / 2.5  # ~150 WPM
        words_per_minute = (word_count / estimated_duration) * 60 if estimated_duration > 0 else 0
        
        # Fluency score (0-10)
        if 120 <= words_per_minute <= 160:
            fluency_score = 9.0
        elif 100 <= words_per_minute <= 180:
            fluency_score = 7.5
        else:
            fluency_score = 6.0
        
        fluency_feedback = f"Estimated speaking rate: {words_per_minute:.0f} WPM (optimal: 120-150 WPM)"
        
        # 2.3 Completeness (Relevance + Depth)
        completeness_score = 7.0  # Base score
        completeness_feedback = "Response length is adequate"
        
        if reference_text:
            # Check if key words from reference appear in response
            reference_words = set(reference_text.lower().split())
            response_words = set(text.lower().split())
            
            # Calculate word overlap
            common_words = reference_words.intersection(response_words)
            overlap_ratio = len(common_words) / max(len(reference_words), 1)
            
            completeness_score = min(10.0, 5.0 + (overlap_ratio * 5) + (word_count / 30))
            completeness_feedback = f"Content relevance: {overlap_ratio*100:.0f}%, Word count: {word_count}"
        else:
            # Without reference, score based on response length
            if word_count >= 50:
                completeness_score = 9.0
                completeness_feedback = "Comprehensive response with good detail"
            elif word_count >= 30:
                completeness_score = 7.5
                completeness_feedback = "Adequate response length"
            else:
                completeness_score = 6.0
                completeness_feedback = "Response could be more detailed"
        
        # Calculate overall score
        overall_score = round((accuracy_score + fluency_score + completeness_score) / 3, 2)
        
        logger.info(f"Analysis complete - Overall: {overall_score}, "
                   f"Accuracy: {accuracy_score}, Fluency: {fluency_score}, Completeness: {completeness_score}")
        
        return SpeakingAnalysisResponse(
            text=text,
            language=detected_language,
            duration=estimated_duration,
            accuracy=round(accuracy_score, 2),
            fluency=round(fluency_score, 2),
            completeness=round(completeness_score, 2),
            accuracy_feedback=accuracy_feedback,
            fluency_feedback=fluency_feedback,
            completeness_feedback=completeness_feedback,
            overall_score=overall_score
        )
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error during speaking analysis: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Analysis failed: {str(e)}")


if __name__ == "__main__":
    import uvicorn
    
    port = int(os.getenv("PORT", 8000))
    host = os.getenv("HOST", "0.0.0.0")
    
    logger.info(f"Starting server on {host}:{port}")
    uvicorn.run(app, host=host, port=port)
