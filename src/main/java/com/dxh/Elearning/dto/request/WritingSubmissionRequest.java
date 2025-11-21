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
public class WritingSubmissionRequest {
    
    @NotNull(message = "Question ID is required")
    Long questionId;
    
    @NotNull(message = "User exam part ID is required")
    Long userExamPartId;
    
    @NotBlank(message = "Answer text is required")
    String answerText;
}
