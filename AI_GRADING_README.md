# AI Grading System - Hệ thống chấm điểm AI cho Writing & Speaking

## Tổng quan

Hệ thống này sử dụng **Spring AI** với **Google Gemini AI** để tự động chấm điểm bài thi kỹ năng Writing và Speaking trong ứng dụng học tiếng Anh.

## Kiến trúc

```
┌─────────────────┐
│   Controller    │  ← REST API endpoints
└────────┬────────┘
         │
┌────────▼────────┐
│   AI Grading    │  ← Business logic
│     Service     │
└────────┬────────┘
         │
┌────────▼────────┐
│  Spring AI +    │  ← AI Integration
│  Gemini 2.0     │
└─────────────────┘
```

## Các thành phần chính

### 1. Entities
- **UserAnswer**: Lưu trữ câu trả lời của học viên
  - `answerText`: Nội dung bài viết (Writing) hoặc transcript (Speaking)
  - `audioUrl`: Link file ghi âm (Speaking)
  - `aiScore`: Điểm AI chấm
  - `aiFeedback`: Nhận xét từ AI

### 2. DTOs

#### WritingSubmissionRequest
```json
{
  "questionId": 1,
  "userExamPartId": 1,
  "answerText": "Nội dung bài viết của học viên..."
}
```

#### SpeakingSubmissionRequest
```json
{
  "questionId": 1,
  "userExamPartId": 1,
  "audioUrl": "https://s3.amazonaws.com/audio/recording.mp3",
  "transcript": "Nội dung phiên âm (optional)"
}
```

#### AIGradingResponse
```json
{
  "userAnswerId": 1,
  "aiScore": 8.5,
  "aiFeedback": "Bài làm tốt...",
  "detailedAnalysis": "Phân tích chi tiết...",
  "grammarAnalysis": {
    "score": 22,
    "feedback": "Ngữ pháp chính xác...",
    "errorCount": 2
  },
  "vocabularyAnalysis": {
    "score": 23,
    "feedback": "Từ vựng phong phú...",
    "level": "B2"
  },
  "coherenceAnalysis": {
    "score": 21,
    "feedback": "Mạch lạc..."
  }
}
```

### 3. API Endpoints

#### Chấm bài Writing
```http
POST /api/ai-grading/writing
Content-Type: application/json

{
  "questionId": 1,
  "userExamPartId": 1,
  "answerText": "Climate change is one of the most pressing issues..."
}
```

#### Chấm bài Speaking
```http
POST /api/ai-grading/speaking
Content-Type: application/json

{
  "questionId": 1,
  "userExamPartId": 1,
  "audioUrl": "https://bucket.s3.amazonaws.com/audio.mp3",
  "transcript": "Hello, today I want to talk about..."
}
```

#### Chấm lại bài đã nộp
```http
POST /api/ai-grading/regrade/{userAnswerId}
```

## Tiêu chí chấm điểm

### Writing (IELTS/TOEFL Style)
1. **Grammar (25%)**: Độ chính xác ngữ pháp
2. **Vocabulary (25%)**: Vốn từ vựng và cách sử dụng
3. **Coherence (25%)**: Tính mạch lạc và liên kết
4. **Task Achievement (25%)**: Hoàn thành yêu cầu đề bài

### Speaking (IELTS/TOEFL Style)
1. **Pronunciation (25%)**: Phát âm rõ ràng
2. **Fluency (25%)**: Độ trôi chảy
3. **Vocabulary (25%)**: Vốn từ
4. **Grammar (25%)**: Ngữ pháp

## Cấu hình

### application.yaml
```yaml
spring:
  ai:
    openai:
      api-key: YOUR_GEMINI_API_KEY
      chat:
        base-url: https://generativelanguage.googleapis.com
        completions-path: /v1beta/openai/chat/completions
        options:
          model: gemini-2.0-flash
```

## Luồng hoạt động

### Writing Flow
```
1. Học viên submit bài viết
   ↓
2. System validate question & user exam part
   ↓
3. Gửi prompt + bài viết tới Gemini AI
   ↓
4. AI phân tích và trả về JSON kết quả
   ↓
5. Parse JSON → AIGradingResponse
   ↓
6. Lưu điểm và feedback vào UserAnswer
   ↓
7. Trả về kết quả cho học viên
```

### Speaking Flow
```
1. Học viên upload audio + (optional) transcript
   ↓
2. Nếu không có transcript → gọi Transcription Service
   ↓
3. Validate question & user exam part
   ↓
4. Gửi prompt + transcript tới Gemini AI
   ↓
5. AI phân tích và trả về JSON kết quả
   ↓
6. Parse JSON → AIGradingResponse
   ↓
7. Lưu điểm, feedback, audioUrl vào UserAnswer
   ↓
8. Trả về kết quả cho học viên
```

## Prompts AI

### Writing Prompt
- Đánh giá như IELTS/TOEFL examiner
- Chấm điểm theo 4 tiêu chí
- Feedback bằng tiếng Việt
- Đưa ra lời khuyên cải thiện cụ thể

### Speaking Prompt
- Đánh giá dựa trên transcript
- Chấm điểm pronunciation, fluency, vocabulary, grammar
- Ước tính WPM (Words Per Minute)
- Feedback chi tiết bằng tiếng Việt

## Tính năng nâng cao (Roadmap)

### 1. Audio Transcription
- Tích hợp Whisper API (OpenAI)
- Google Cloud Speech-to-Text
- Azure Speech Service

### 2. Plagiarism Detection
- Kiểm tra đạo văn
- So sánh với database bài mẫu

### 3. Progress Tracking
- Theo dõi tiến bộ học viên
- Phân tích điểm yếu
- Đề xuất lộ trình học

### 4. Batch Grading
- Chấm hàng loạt bài cùng lúc
- Queue system với Redis

### 5. Custom Rubrics
- Cho phép giáo viên tùy chỉnh tiêu chí chấm
- Template chấm điểm linh hoạt

## Testing

### Test với Swagger UI
1. Truy cập: `http://localhost:8080/api/swagger-ui.html`
2. Tìm "AI Grading" section
3. Test các endpoints

### Test bằng cURL

**Writing:**
```bash
curl -X POST http://localhost:8080/api/ai-grading/writing \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "questionId": 1,
    "userExamPartId": 1,
    "answerText": "Climate change is becoming increasingly severe..."
  }'
```

**Speaking:**
```bash
curl -X POST http://localhost:8080/api/ai-grading/speaking \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "questionId": 1,
    "userExamPartId": 1,
    "audioUrl": "https://example.com/audio.mp3",
    "transcript": "Hello, I would like to talk about..."
  }'
```

## Xử lý lỗi

- `QUESTION_NOT_FOUND`: Question ID không tồn tại
- `USER_EXAM_PART_NOT_FOUND`: User exam part không tồn tại
- `INVALID_QUESTION_TYPE`: Loại câu hỏi không phù hợp
- `INVALID_SKILL_TYPE`: Skill type không phù hợp
- `TRANSCRIPT_REQUIRED`: Cần transcript cho speaking
- `AI_RESPONSE_PARSE_ERROR`: Lỗi parse response từ AI

## Performance Tips

1. **Caching**: Cache AI responses cho câu trả lời tương tự
2. **Async Processing**: Xử lý chấm điểm bất đồng bộ
3. **Rate Limiting**: Giới hạn số request/phút tới AI
4. **Batch Processing**: Gom nhóm requests nếu có nhiều bài cùng lúc

## Security

- Validate input để tránh prompt injection
- Rate limit để tránh abuse
- Log tất cả AI requests để audit
- Encrypt sensitive data

## Chi phí

- Gemini 2.0 Flash: Free tier available
- Ước tính: ~0.1 request/second cho 100 users
- Monitor usage qua Google Cloud Console

## Liên hệ & Support

- Developer: Your Name
- Email: your.email@example.com
- Documentation: [Link to docs]
