package com.dxh.BookingBe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "options")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String optionText;

    private Boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}
