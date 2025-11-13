package com.dxh.Elearning.repo;

import com.dxh.Elearning.entity.UserExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExamRepository extends JpaRepository<UserExam, Long> {

}
