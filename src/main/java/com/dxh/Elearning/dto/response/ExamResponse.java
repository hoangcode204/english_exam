package com.dxh.Elearning.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamResponse {
    Long id;           // id của Exam sinh ra
    String title;
    String description;
    Integer totalDuration;
}
