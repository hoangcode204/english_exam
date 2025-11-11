package com.dxh.BookingBe.entity;

import com.dxh.BookingBe.enums.ExamLevel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "exams")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Exams extends AbstractEntity<Long> {
    @Column(nullable = false, length = 255)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ExamLevel level;   // ENUM('A1','A2','B1','B2','C1','C2')

    @Column(nullable = false)
    Integer duration;  // thời gian làm (phút)

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    private List<ExamSections> sections;

    @OneToMany(mappedBy = "exam")
    private List<ExamResult> results;
}
