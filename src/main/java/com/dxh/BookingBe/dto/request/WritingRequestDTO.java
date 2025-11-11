package com.dxh.BookingBe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WritingRequestDTO {
    private Long examId;
    private Long userId;
    private String content; // bài viết của thí sinh
}
