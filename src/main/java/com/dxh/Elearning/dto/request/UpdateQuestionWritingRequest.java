package com.dxh.Elearning.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateQuestionWritingRequest {
    @NotBlank(message = "INVALID_BLANK")
    String content;

    @NotNull(message = "INVALID_NULL")
    Double maxScore;
}
