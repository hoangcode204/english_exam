package com.dxh.Elearning.service.impl;

import com.dxh.Elearning.dto.request.AnswerRLPartRequest;
import com.dxh.Elearning.dto.request.SubmitRLPartRequest;
import com.dxh.Elearning.dto.request.UserExamRequest;
import com.dxh.Elearning.dto.response.SubmitRLPartResponse;
import com.dxh.Elearning.dto.response.UserExamResponse;
import com.dxh.Elearning.entity.*;
import com.dxh.Elearning.exception.AppException;
import com.dxh.Elearning.exception.ErrorCode;
import com.dxh.Elearning.mapper.UserExamMapper;
import com.dxh.Elearning.mapper.UserExamPartMapper;
import com.dxh.Elearning.repo.*;
import com.dxh.Elearning.service.interfac.UserExamPartService;
import com.dxh.Elearning.service.interfac.UserExamService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserExamPartServiceImpl implements UserExamPartService {
    UserExamRepository userExamRepository;
    UserRepository userRepository;
    QuestionRepository questionRepository;
    UserExamPartRepository userExamPartRepository;
    UserAnswerRepository userAnswerRepository;
    UserExamPartMapper userExamPartMapper;

//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public SubmitRLPartResponse submitRLPart(SubmitRLPartRequest req) {
//        UserExamPart userExamPart = userExamPartRepository.findById(req.getUserExamPartId())
//                .orElseThrow(() -> new AppException(ErrorCode.USER_EXAM_PART_NOT_EXISTED));
//
//        // 1️⃣ Lưu từng câu trả lời
//        for (AnswerRLPartRequest a : req.getAnswers()) {
//            Question question = questionRepository.findById(a.getQuestionId())
//                    .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
//
//            // Xác định đúng sai
//            boolean isCorrect = question.getCorrectOption() != null &&
//                    question.getCorrectOption().getId().equals(a.getSelectedOptionId());
//
//            double score = isCorrect ? 1.0 : 0.0; // ví dụ mỗi câu đúng = 1 điểm
//
//            UserAnswer ua = UserAnswer.builder()
//                    .userExamPart(userExamPart)
//                    .question(question)
//                    .selectedOptionId(a.getSelectedOptionId())
//                    .score(score)
//                    .build();
//            userAnswerRepository.save(ua);
//        }
//
//        // 2️⃣ Tính tổng điểm phần thi này
//        double totalScore = userAnswerRepository.sumScoreByUserExamPartId(userExamPart.getId());
//        userExamPart.setScore(totalScore);
//        userExamPart.setSubmitted(true);
//
//        UserExamPart save = userExamPartRepository.save(userExamPart);
//
//        // 4️⃣ Cập nhật tổng điểm UserExam
//        double examTotalScore = userExamPartRepository.sumScoreByUserExamId(userExamPart.getUserExam().getId());
//        UserExam userExam = userExamPart.getUserExam();
//        userExam.setTotalScore(examTotalScore);
//        userExamRepository.save(userExam);
//
//        // 3️⃣ Trả về response
//        return userExamPartMapper.toSubmitRLPartResponse(save);
//    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public SubmitRLPartResponse submitRLPart(SubmitRLPartRequest req) {
        UserExamPart userExamPart = userExamPartRepository.findById(req.getUserExamPartId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXAM_PART_NOT_EXISTED));

        //fix: Xóa answers cũ trước khi thêm mới
        userAnswerRepository.deleteByUserExamPartId(userExamPart.getId());
        userExamPartRepository.flush(); // Đồng bộ với DB ngay lập tức

        // 1️⃣ Lưu từng câu trả lời
        for (AnswerRLPartRequest a : req.getAnswers()) {
            Question question = questionRepository.findById(a.getQuestionId())
                    .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

            // Xác định đúng sai
            boolean isCorrect = question.getCorrectOption() != null &&
                    question.getCorrectOption().getId().equals(a.getSelectedOptionId());

            double score = isCorrect ? 1.0 : 0.0; // ví dụ mỗi câu đúng = 1 điểm

            UserAnswer ua = UserAnswer.builder()
                    .userExamPart(userExamPart)
                    .question(question)
                    .selectedOptionId(a.getSelectedOptionId())
                    .score(score)
                    .build();
            userAnswerRepository.save(ua);
        }

        // 2️⃣ Tính tổng điểm phần thi này
        double totalScore = userAnswerRepository.sumScoreByUserExamPartId(userExamPart.getId());
        userExamPart.setScore(totalScore);
        userExamPart.setSubmitted(true);

        UserExamPart save = userExamPartRepository.save(userExamPart);

        // 4️⃣ Cập nhật tổng điểm UserExam
        double examTotalScore = userExamPartRepository.sumScoreByUserExamId(userExamPart.getUserExam().getId());
        UserExam userExam = userExamPart.getUserExam();
        userExam.setTotalScore(examTotalScore);
        userExamRepository.save(userExam);

        // 3️⃣ Trả về response
        return userExamPartMapper.toSubmitRLPartResponse(save);
    }


    private User checkUser(){
        return userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }


}
