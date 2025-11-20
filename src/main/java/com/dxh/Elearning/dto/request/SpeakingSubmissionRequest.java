package com.dxh.Elearning.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingSubmissionRequest {
    
    @NotNull(message = "Question ID is required")
    Long questionId;
    
    @NotNull(message = "User exam part ID is required")
    Long userExamPartId;
    
    @NotBlank(message = "Audio URL is required")
    String audioUrl;
    
    String transcript; // Có thể có hoặc không, nếu không có AI sẽ transcribe
}
