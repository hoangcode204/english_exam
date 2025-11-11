package com.dxh.Elearning.entity;

import com.dxh.Elearning.enums.QuestionType;
import com.dxh.Elearning.enums.SkillType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "questions")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question extends AbstractEntity<Long> {

    @Enumerated(EnumType.STRING)
    SkillType skillType;

    @Enumerated(EnumType.STRING)
    QuestionType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    String content;

    @Column(name = "audio_url")
    String audioUrl; // dùng cho Listening hoặc Speaking mẫu

    @OneToOne
    @JoinColumn(name = "correct_option_id")
    Option correctOption;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Option> options = new ArrayList<>();

    @Column(name = "max_score")
    Double maxScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_part_id")
    ExamPart examPart;
}
