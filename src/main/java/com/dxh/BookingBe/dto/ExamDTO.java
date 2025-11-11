package com.dxh.BookingBe.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamDTO {
    private Long id;
    private String title;
    private String description;
    private Integer duration; // in minutes
    private LocalDateTime createdDate;
    private List<QuestionDTO> questions; // dành cho Listening/Reading
}
