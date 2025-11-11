package com.dxh.BookingBe.repo;

import com.dxh.BookingBe.entity.Exams;
import com.dxh.BookingBe.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exams, Long> {

    List<Exams> findByLevel(String level);

    List<Exams> findByCreatedBy(Long userId);

}
