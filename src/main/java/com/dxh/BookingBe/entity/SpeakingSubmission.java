package com.dxh.BookingBe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "speaking_submissions")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpeakingSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String audioUrl;

    @Column(columnDefinition = "TEXT")
    private String transcript;

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
