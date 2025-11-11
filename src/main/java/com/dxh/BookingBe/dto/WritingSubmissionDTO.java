package com.dxh.BookingBe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WritingSubmissionDTO {
    private Long id;
    private Long userId;
    private Long examId;
    private String content;  // nội dung bài viết
    private Double score;    // điểm do AI chấm
    private String feedback; // phản hồi chi tiết
}
