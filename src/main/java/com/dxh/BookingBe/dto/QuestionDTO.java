package com.dxh.BookingBe.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {
    private Long id;
    private String content;      // Câu hỏi
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer; // Chỉ dùng khi giáo viên xem, ẩn khi học sinh làm bài
    private String skillType;     // "LISTENING" hoặc "READING"
}
