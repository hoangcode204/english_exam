package com.dxh.Elearning.repo;

import com.dxh.Elearning.entity.ExamPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPartRepository extends JpaRepository<ExamPart, Long> {

    List<ExamPart> findAllByExam_Id(Long examId);
}

