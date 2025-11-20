# Whisper Transcription Service

FastAPI service for audio-to-text transcription using OpenAI Whisper.

## Features

- 🎤 Audio transcription using Whisper model
- 🚀 Fast and efficient processing
- 🐳 Docker support
- 📊 REST API with OpenAPI documentation
- 🌐 CORS enabled for Spring Boot integration
- 🔄 Batch processing support

## Quick Start

### Option 1: Run with Python (Recommended for Development)

1. **Install Python 3.11+**

2. **Create virtual environment:**
```bash
python -m venv venv
venv\Scripts\activate  # Windows
```

3. **Install dependencies:**
```bash
pip install -r requirements.txt
```

4. **Run the service:**
```bash
python main.py
```

The service will start on `http://localhost:8000`

### Option 2: Run with Docker

1. **Build the image:**
```bash
docker-compose build
```

2. **Start the service:**
```bash
docker-compose up -d
```

## API Endpoints

### 1. Health Check
```http
GET http://localhost:8000/health
```

### 2. Transcribe Audio
```http
POST http://localhost:8000/transcribe
Content-Type: multipart/form-data

file: [audio file]
language: en (optional)
```

**Supported formats:** mp3, wav, m4a, ogg, flac, webm

**Example Response:**
```json
{
  "text": "Hello, this is a test transcription.",
  "language": "en",
  "duration": null,
  "confidence": null
}
```

### 3. Batch Transcription
```http
POST http://localhost:8000/transcribe-batch
Content-Type: multipart/form-data

files: [multiple audio files]
```

## Configuration

Edit `.env` file to configure:

```env
HOST=0.0.0.0
PORT=8000
WHISPER_MODEL_SIZE=base  # Options: tiny, base, small, medium, large
```

### Model Selection Guide:

| Model  | Speed | Accuracy | RAM Usage | Best For |
|--------|-------|----------|-----------|----------|
| tiny   | ⚡⚡⚡⚡⚡ | ⭐⭐ | ~1 GB | Quick testing |
| base   | ⚡⚡⚡⚡ | ⭐⭐⭐ | ~1 GB | **Recommended** |
| small  | ⚡⚡⚡ | ⭐⭐⭐⭐ | ~2 GB | Better accuracy |
| medium | ⚡⚡ | ⭐⭐⭐⭐⭐ | ~5 GB | High accuracy |
| large  | ⚡ | ⭐⭐⭐⭐⭐ | ~10 GB | Best accuracy |

## Testing

### Using curl:
```bash
curl -X POST "http://localhost:8000/transcribe" \
  -H "accept: application/json" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@path/to/audio.mp3"
```

### Using Python:
```python
import requests

url = "http://localhost:8000/transcribe"
files = {"file": open("audio.mp3", "rb")}
response = requests.post(url, files=files)
print(response.json())
```

## API Documentation

Once running, visit:
- Swagger UI: `http://localhost:8000/docs`
- ReDoc: `http://localhost:8000/redoc`

## Performance Tips

1. **Use smaller models** for faster processing
2. **Enable GPU** if available (requires CUDA)
3. **Cache models** - first run downloads model (~150MB for base)
4. **Process in background** for long audio files

## Troubleshooting

### Issue: Model download fails
**Solution:** Check internet connection or manually download model

### Issue: Out of memory
**Solution:** Use a smaller model (tiny or base)

### Issue: Slow transcription
**Solution:** Use GPU or switch to smaller model

## Integration with Spring Boot

See the main project README for Spring Boot integration details.
