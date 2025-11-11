package com.dxh.BookingBe.entity;

import com.dxh.BookingBe.enums.Skill;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "exam_sections")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamSections{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Skill skill;
    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exams exam;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private List<Question> questions;

    @OneToMany(mappedBy = "section")
    private List<WritingSubmission> writingSubmissions;

    @OneToMany(mappedBy = "section")
    private List<SpeakingSubmission> speakingSubmissions;

}
