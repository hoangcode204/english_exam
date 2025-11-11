package com.dxh.BookingBe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "writing_submissions")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WritingSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Float aiScore;

    @Column(columnDefinition = "TEXT")
    private String aiFeedback;

    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private ExamSections section;
}
