package com.dxh.Elearning.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListQuestionRequest {

    List<QuestionRequest> questions;

}
