package com.dxh.Elearning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerResponse {
    Long id;                // ID của user answer
    QuestionResponse question;  // Embed QuestionResponse
    Long selectedOptionId;  // đáp án trắc nghiệm
    String answerText;      // text viết/ transcript Speaking
    String audioUrl;        // link ghi âm Speaking
    Double score;           // điểm auto (Listening/Reading)
    Double aiScore;         // điểm AI (Writing/Speaking)
    String aiFeedback;      // nhận xét từ AI
}
