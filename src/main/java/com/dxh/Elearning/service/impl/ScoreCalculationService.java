package com.dxh.Elearning.service.impl;

import com.dxh.Elearning.entity.UserAnswer;
import com.dxh.Elearning.entity.UserExamPart;
import com.dxh.Elearning.enums.QuestionType;
import com.dxh.Elearning.repo.UserExamPartRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service để tính toán điểm tổng cho UserExamPart
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ScoreCalculationService {

    UserExamPartRepository userExamPartRepository;

    /**
     * Tính và cập nhật điểm tổng cho một UserExamPart
     * Điểm được tính dựa trên:
     * - Multiple Choice questions: score (auto-graded)
     * - Writing/Speaking questions: aiScore (AI-graded)
     */
    @Transactional
    public void calculateAndUpdateScore(Long userExamPartId) {
        UserExamPart userExamPart = userExamPartRepository.findById(userExamPartId)
                .orElseThrow(() -> new IllegalArgumentException("UserExamPart not found"));

        List<UserAnswer> answers = userExamPart.getAnswers();
        
        if (answers == null || answers.isEmpty()) {
            log.warn("No answers found for UserExamPart: {}", userExamPartId);
            userExamPart.setScore(0.0);
            userExamPartRepository.save(userExamPart);
            return;
        }

        double totalScore = 0.0;
        int answeredQuestions = 0;

        for (UserAnswer answer : answers) {
            QuestionType questionType = answer.getQuestion().getType();
            
            if (questionType == QuestionType.MULTIPLE_CHOICE) {
                // Listening/Reading: sử dụng score (auto-graded)
                if (answer.getScore() != null) {
                    totalScore += answer.getScore();
                    answeredQuestions++;
                }
            } else if (questionType == QuestionType.WRITING || questionType == QuestionType.SPEAKING) {
                // Writing/Speaking: sử dụng aiScore (AI-graded)
                if (answer.getAiScore() != null) {
                    totalScore += answer.getAiScore();
                    answeredQuestions++;
                }
            }
        }

        // Cập nhật điểm trung bình
        double averageScore = answeredQuestions > 0 ? totalScore / answeredQuestions : 0.0;
        userExamPart.setScore(averageScore);
        
        userExamPartRepository.save(userExamPart);
        
        log.info("Updated score for UserExamPart {}: {} (from {} answers)", 
                 userExamPartId, averageScore, answeredQuestions);
    }

    /**
     * Kiểm tra xem UserExamPart đã hoàn thành chưa
     * (tất cả câu hỏi đã được chấm điểm)
     */
    public boolean isCompleted(Long userExamPartId) {
        UserExamPart userExamPart = userExamPartRepository.findById(userExamPartId)
                .orElseThrow(() -> new IllegalArgumentException("UserExamPart not found"));

        List<UserAnswer> answers = userExamPart.getAnswers();
        
        if (answers == null || answers.isEmpty()) {
            return false;
        }

        for (UserAnswer answer : answers) {
            QuestionType questionType = answer.getQuestion().getType();
            
            if (questionType == QuestionType.MULTIPLE_CHOICE) {
                if (answer.getScore() == null) {
                    return false;
                }
            } else if (questionType == QuestionType.WRITING || questionType == QuestionType.SPEAKING) {
                if (answer.getAiScore() == null) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Lấy tiến độ hoàn thành (%)
     */
    public double getCompletionPercentage(Long userExamPartId) {
        UserExamPart userExamPart = userExamPartRepository.findById(userExamPartId)
                .orElseThrow(() -> new IllegalArgumentException("UserExamPart not found"));

        List<UserAnswer> answers = userExamPart.getAnswers();
        
        if (answers == null || answers.isEmpty()) {
            return 0.0;
        }

        int totalQuestions = answers.size();
        int gradedQuestions = 0;

        for (UserAnswer answer : answers) {
            QuestionType questionType = answer.getQuestion().getType();
            
            if (questionType == QuestionType.MULTIPLE_CHOICE && answer.getScore() != null) {
                gradedQuestions++;
            } else if ((questionType == QuestionType.WRITING || questionType == QuestionType.SPEAKING) 
                       && answer.getAiScore() != null) {
                gradedQuestions++;
            }
        }

        return (gradedQuestions * 100.0) / totalQuestions;
    }
}
