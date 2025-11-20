# Whisper Audio Transcription Integration

Tích hợp hệ thống chuyển đổi audio sang text sử dụng OpenAI Whisper cho phần chấm speaking.

## 📋 Tổng quan

Hệ thống bao gồm 2 phần:

1. **Whisper Service (FastAPI)**: Dịch vụ Python độc lập xử lý transcription
2. **Spring Boot Integration**: API endpoints để sử dụng trong ứng dụng

## 🚀 Hướng dẫn Setup

### Bước 1: Cài đặt Whisper Service

#### Yêu cầu hệ thống:
- Python 3.11 trở lên
- 4GB RAM (đối với model base)
- ffmpeg (để xử lý audio)

#### Cài đặt trên Windows:

1. **Cài đặt ffmpeg:**
```bash
# Download từ https://ffmpeg.org/download.html
# Hoặc sử dụng chocolatey:
choco install ffmpeg
```

2. **Chuyển đến thư mục whisper-service:**
```bash
cd whisper-service
```

3. **Tạo virtual environment:**
```bash
python -m venv venv
venv\Scripts\activate
```

4. **Cài đặt dependencies:**
```bash
pip install -r requirements.txt
```

5. **Chạy service:**
```bash
python main.py
```

Whisper service sẽ chạy tại: `http://localhost:8000`

### Bước 2: Cấu hình Spring Boot

Cấu hình đã được thêm vào `application.yaml`:

```yaml
whisper:
  service:
    url: http://localhost:8000
    timeout: 30000
```

### Bước 3: Khởi động Spring Boot

```bash
mvnw spring-boot:run
```

## 📡 API Endpoints

### 1. Transcribe Audio (General)

```http
POST /api/audio/transcribe
Content-Type: multipart/form-data

Parameters:
- file: audio file (mp3, wav, m4a, etc.)
- language: (optional) language code (en, vi, etc.)
```

**Ví dụ với curl:**
```bash
curl -X POST "http://localhost:8080/api/audio/transcribe" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@recording.mp3" \
  -F "language=en"
```

**Response:**
```json
{
  "code": 1000,
  "result": {
    "text": "Hello, this is a test recording.",
    "language": "en"
  }
}
```

### 2. Transcribe Speaking Exam

```http
POST /api/audio/transcribe-speaking
Content-Type: multipart/form-data

Parameters:
- file: audio file
```

**Ví dụ:**
```bash
curl -X POST "http://localhost:8080/api/audio/transcribe-speaking" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@speaking_answer.mp3"
```

### 3. Health Check

```http
GET /api/audio/health
```

## 🎯 Tích hợp với AI Grading

Để sử dụng transcription cho chấm speaking:

```java
@Autowired
private WhisperService whisperService;

@Autowired
private AIGradingService aiGradingService;

public void gradeSpeaking(MultipartFile audioFile, String questionText) {
    // 1. Transcribe audio to text
    String transcript = whisperService.transcribeAudio(audioFile, "en");
    
    // 2. Grade using AI
    var gradingResult = aiGradingService.gradeSpeaking(questionText, transcript);
    
    // 3. Save result...
}
```

## 🐳 Docker Deployment

### Chạy Whisper service với Docker:

```bash
cd whisper-service
docker-compose up -d
```

### Docker Compose tổng hợp (tùy chọn):

Tạo `docker-compose.yml` ở thư mục root:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: 123456789
      MYSQL_DATABASE: elearning
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  whisper-service:
    build: ./whisper-service
    ports:
      - "8000:8000"
    environment:
      - WHISPER_MODEL_SIZE=base
    volumes:
      - ./whisper-service/models:/root/.cache/whisper

  spring-boot-app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - whisper-service
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/elearning
      - WHISPER_SERVICE_URL=http://whisper-service:8000

volumes:
  mysql_data:
```

## 🔧 Troubleshooting

### Lỗi: "Whisper service is unavailable"

**Giải pháp:**
1. Kiểm tra Whisper service đang chạy: `curl http://localhost:8000/health`
2. Kiểm tra port có bị chiếm: `netstat -ano | findstr :8000`
3. Xem logs của Whisper service

### Lỗi: "Out of memory"

**Giải pháp:**
- Sử dụng model nhỏ hơn: Sửa `.env` → `WHISPER_MODEL_SIZE=tiny`
- Tăng RAM cho máy

### Lỗi: "ffmpeg not found"

**Giải pháp:**
```bash
# Windows (với chocolatey)
choco install ffmpeg

# Hoặc download từ: https://ffmpeg.org/download.html
```

### Transcription chậm

**Giải pháp:**
1. Sử dụng model nhỏ hơn (tiny hoặc base)
2. Nếu có GPU NVIDIA, cài đặt CUDA để tăng tốc
3. Giảm kích thước file audio trước khi upload

## 📊 Chọn Model phù hợp

| Model  | Tốc độ | Độ chính xác | RAM  | Khi nào dùng |
|--------|--------|--------------|------|--------------|
| tiny   | ⚡⚡⚡⚡⚡ | ⭐⭐ | 1 GB | Test nhanh |
| **base**   | ⚡⚡⚡⚡ | ⭐⭐⭐ | 1 GB | **Khuyến nghị** |
| small  | ⚡⚡⚡ | ⭐⭐⭐⭐ | 2 GB | Cần chính xác cao |
| medium | ⚡⚡ | ⭐⭐⭐⭐⭐ | 5 GB | Production |
| large  | ⚡ | ⭐⭐⭐⭐⭐ | 10 GB | Tốt nhất |

## 🧪 Testing

### Test Whisper Service:

```bash
cd whisper-service

# Test với file audio mẫu
curl -X POST "http://localhost:8000/transcribe" \
  -F "file=@test_audio.mp3"
```

### Test Spring Boot Integration:

Sử dụng Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`

Hoặc Postman collection đã có sẵn trong project.

## 📝 Lưu ý Production

1. **Security**: Thêm authentication cho Whisper service
2. **Rate Limiting**: Giới hạn số request để tránh overload
3. **Async Processing**: Xử lý async cho audio files lớn
4. **Caching**: Cache kết quả transcription
5. **Monitoring**: Theo dõi performance và errors

## 🔗 Tài liệu tham khảo

- [OpenAI Whisper Documentation](https://github.com/openai/whisper)
- [FastAPI Documentation](https://fastapi.tiangolo.com/)
- [Spring Boot RestTemplate](https://spring.io/guides/gs/consuming-rest/)

## 🆘 Support

Nếu gặp vấn đề, kiểm tra:
1. Logs của Whisper service
2. Logs của Spring Boot application
3. Network connectivity giữa 2 services
