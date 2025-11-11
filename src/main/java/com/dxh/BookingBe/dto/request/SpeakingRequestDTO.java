package com.dxh.BookingBe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeakingRequestDTO {
    private Long examId;
    private Long userId;
    private String audioUrl; // file upload hoặc link audio
}