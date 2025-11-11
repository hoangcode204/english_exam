package com.dxh.Elearning.dto.request;


import com.dxh.Elearning.enums.QuestionType;
import com.dxh.Elearning.enums.SkillType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionRequest {


    @NotNull(message = "INVALID_NULL")
    Long examPartId;

    @NotNull(message = "INVALID_NULL")
    SkillType skillType;

    @NotNull(message = "INVALID_NULL")
    QuestionType type;

    @NotBlank(message = "INVALID_BLANK")
    String content;

    String audioUrl; // optional, cho Listening

    @NotNull(message = "INVALID_NULL")
    Double maxScore;

    @NotNull(message = "INVALID_NULL")
    List<OptionRequest> options;

    @NotBlank(message = "INVALID_BLANK")
    String correctTempId; // tempId của đáp án đúng
}
