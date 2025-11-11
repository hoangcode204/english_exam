package com.dxh.BookingBe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeakingResponseDTO {
    private Long submissionId;
    private String transcript; // text sau khi chuyển từ giọng nói
    private Double score;
    private String feedback;
    private LocalDateTime gradedAt;
}