package com.dxh.BookingBe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exam_results")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float listeningScore;
    private Float readingScore;
    private Float writingScore;
    private Float speakingScore;
    private Float totalScore;

    private LocalDateTime gradedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exams exam;

}
