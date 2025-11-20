# Quick Start Guide - AI Grading System

## 🚀 Bắt đầu nhanh

### 1. Cấu hình (đã hoàn tất)
File `application.yaml` đã được cấu hình sẵn với Gemini AI.

### 2. Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Test API qua Swagger
Truy cập: http://localhost:8080/api/swagger-ui.html

Tìm section **"AI Grading"** và test các endpoint:
- POST `/api/ai-grading/writing` - Chấm bài Writing
- POST `/api/ai-grading/speaking` - Chấm bài Speaking  
- POST `/api/ai-grading/regrade/{id}` - Chấm lại

## 📝 Ví dụ sử dụng

### Chấm bài Writing

**Request:**
```json
POST /api/ai-grading/writing
{
  "questionId": 1,
  "userExamPartId": 1,
  "answerText": "Climate change is one of the most pressing issues..."
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Writing graded successfully",
  "result": {
    "userAnswerId": 123,
    "aiScore": 8.5,
    "aiFeedback": "Bài viết của bạn thể hiện khả năng sử dụng ngôn ngữ tốt...",
    "grammarAnalysis": {
      "score": 22,
      "feedback": "Ngữ pháp chính xác, chỉ có 2 lỗi nhỏ...",
      "errorCount": 2
    },
    "vocabularyAnalysis": {
      "score": 23,
      "feedback": "Từ vựng phong phú, sử dụng đa dạng...",
      "level": "B2"
    },
    "coherenceAnalysis": {
      "score": 21,
      "feedback": "Bài viết mạch lạc, có cấu trúc rõ ràng..."
    }
  }
}
```

### Chấm bài Speaking

**Request:**
```json
POST /api/ai-grading/speaking
{
  "questionId": 10,
  "userExamPartId": 5,
  "audioUrl": "https://s3.amazonaws.com/audio.mp3",
  "transcript": "Hello, today I want to talk about..."
}
```

**Response:** (Tương tự Writing nhưng có thêm pronunciationAnalysis và fluencyAnalysis)

## 🔧 Các tính năng đã triển khai

✅ Chấm bài Writing tự động với AI
✅ Chấm bài Speaking với transcript
✅ Phân tích chi tiết (Grammar, Vocabulary, Coherence, etc.)
✅ Feedback bằng tiếng Việt
✅ Tự động tính điểm tổng cho UserExamPart
✅ Chức năng chấm lại (regrade)
✅ Integration với Spring AI + Gemini
✅ API documentation với Swagger
✅ Error handling đầy đủ

## 📋 Yêu cầu trước khi test

1. **Database phải có:**
   - Question với type = WRITING hoặc SPEAKING
   - UserExamPart với skillType = WRITING hoặc SPEAKING
   - Question phải có `maxScore` được set

2. **Gemini API Key:**
   - Đã được config trong `application.yaml`
   - Key hiện tại: `AIzaSyBMT6EwdTobiK7DRf2RHNxpHoEVbIZl_VA`

3. **Authentication:**
   - Cần JWT token để gọi API
   - Login qua `/api/auth/login` để lấy token

## 🎯 Luồng hoạt động

```
1. Học viên làm bài Writing/Speaking
2. Frontend gửi request tới /ai-grading/writing hoặc /speaking
3. Backend validate dữ liệu
4. Gọi Gemini AI để chấm điểm
5. Parse kết quả từ AI
6. Lưu điểm vào UserAnswer
7. Tự động tính điểm tổng cho UserExamPart
8. Trả response về frontend
9. Frontend hiển thị điểm + feedback cho học viên
```

## 📊 Cấu trúc đánh giá

### Writing (100 điểm)
- Grammar: 25 điểm
- Vocabulary: 25 điểm  
- Coherence: 25 điểm
- Task Achievement: 25 điểm

### Speaking (100 điểm)
- Pronunciation: 25 điểm
- Fluency: 25 điểm
- Vocabulary: 25 điểm
- Grammar: 25 điểm

## 🔮 Tính năng sắp tới (TODO)

- [ ] Audio Transcription tự động (Whisper API)
- [ ] Batch grading (chấm nhiều bài cùng lúc)
- [ ] Cache AI responses
- [ ] Export báo cáo chi tiết PDF
- [ ] Plagiarism detection
- [ ] Progress tracking dashboard
- [ ] Custom grading rubrics

## 🐛 Troubleshooting

### Lỗi: "QUESTION_NOT_FOUND"
→ Đảm bảo questionId tồn tại trong DB

### Lỗi: "INVALID_QUESTION_TYPE"  
→ Question phải có type = WRITING hoặc SPEAKING

### Lỗi: "AI_RESPONSE_PARSE_ERROR"
→ Kiểm tra API key Gemini còn valid không
→ Kiểm tra logs để xem response từ AI

### Lỗi: "TRANSCRIPT_REQUIRED"
→ Speaking submission phải có transcript (audio transcription chưa implement)

## 📞 Support

- Developer: Dương Xuân Hưng
- Email: mkvht8@gmail.com
- GitHub: hoangcode204/english_exam

---

**Happy Coding! 🎉**
