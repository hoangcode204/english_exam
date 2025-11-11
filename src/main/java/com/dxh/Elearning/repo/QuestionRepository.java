package com.dxh.Elearning.repo;


import com.dxh.Elearning.entity.Question;
import com.dxh.Elearning.enums.SkillType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByExamPartId(Long examPartId, Pageable pageable);

    List<Question> findBySkillType(SkillType skillType);
}
