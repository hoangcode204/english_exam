package com.dxh.Elearning.repo;

import com.dxh.Elearning.entity.UserExamPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExamPartRepository extends JpaRepository<UserExamPart, Long> {
    @Query("SELECT COALESCE(SUM(p.score), 0) FROM UserExamPart p WHERE p.userExam.id = :examId")
    double sumScoreByUserExamId(@Param("examId") Long examId);

}
