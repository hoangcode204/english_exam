package com.dxh.BookingBe.repo;

import com.dxh.BookingBe.entity.ExamSections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSectionRepository extends JpaRepository<ExamSections, Long> {

    List<ExamSections> findByExamId(Long examId);

    List<ExamSections> findBySkill(String skill);
}