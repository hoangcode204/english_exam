package com.dxh.Elearning.dto.response;

import com.dxh.Elearning.enums.QuestionType;
import com.dxh.Elearning.enums.SkillType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse {
    Long id;
    Long examPartId;
    SkillType skillType;
    QuestionType type;
    String content;
    String audioUrl;
    Double maxScore;
    Long correctOptionId;
    List<OptionResponse> optionRes;
}
