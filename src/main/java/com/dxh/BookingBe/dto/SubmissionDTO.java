package com.dxh.BookingBe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionDTO {
    private Long id;
    private Long examId;
    private Long userId;
    private LocalDateTime submittedAt;
    private Double totalScore;
    private List<AnswerDTO> answers; // Dùng cho Listening/Reading
    private WritingSubmissionDTO writing;
    private SpeakingSubmissionDTO speaking;
}
