package com.dxh.BookingBe.entity;

import com.dxh.BookingBe.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "questions")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Column(columnDefinition = "TEXT")
    private String correctAnswer;

    private Float score;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private ExamSections section;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Option> options;

    @OneToMany(mappedBy = "question")
    private List<Answer> answers;
}
