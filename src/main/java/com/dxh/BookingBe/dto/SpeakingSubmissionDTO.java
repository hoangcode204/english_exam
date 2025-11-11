package com.dxh.BookingBe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeakingSubmissionDTO {
    private Long id;
    private Long userId;
    private Long examId;
    private String audioUrl;  // đường dẫn file âm thanh lưu trên server
    private String transcript; // đoạn text sau khi chuyển đổi từ giọng nói
    private Double score;      // điểm AI chấm
    private String feedback;   // phản hồi chi tiết
}
