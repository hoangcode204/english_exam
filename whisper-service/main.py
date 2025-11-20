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


if __name__ == "__main__":
    import uvicorn
    
    port = int(os.getenv("PORT", 8000))
    host = os.getenv("HOST", "0.0.0.0")
    
    logger.info(f"Starting server on {host}:{port}")
    uvicorn.run(app, host=host, port=port)
