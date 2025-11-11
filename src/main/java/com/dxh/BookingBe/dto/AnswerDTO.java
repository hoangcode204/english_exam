package com.dxh.BookingBe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDTO {
    private Long questionId;
    private String selectedAnswer;  // A/B/C/D
    private Boolean isCorrect;
    private Double score;
}
