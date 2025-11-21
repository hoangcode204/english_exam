package com.dxh.Elearning.service.impl;

import com.dxh.Elearning.dto.request.UserExamRequest;
import com.dxh.Elearning.dto.response.UserExamResponse;
import com.dxh.Elearning.entity.*;
import com.dxh.Elearning.exception.AppException;
import com.dxh.Elearning.exception.ErrorCode;
import com.dxh.Elearning.mapper.UserExamMapper;
import com.dxh.Elearning.repo.*;
import com.dxh.Elearning.service.interfac.ExamService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserExamServiceImpl implements UserExamService {
    UserExamRepository userExamRepository;
    UserExamMapper userExamMapper;
    UserRepository userRepository;
    ExamRepository examRepository;
    ExamPartRepository examPartRepository;
    UserExamPartRepository userExamPartRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserExamResponse create(UserExamRequest req) {
        User user = checkUser();
        Exam exam = examRepository.findById(req.getExamId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_EXISTED));

        // 1️⃣ Tạo user_exam
        UserExam userExam = UserExam.builder()
                .user(user)
                .exam(exam)
                .submitted(false)
                .totalScore(0.0)
                .startedAt(LocalDateTime.now())
                .build();
        userExam = userExamRepository.save(userExam);

        // 2️⃣ Lấy danh sách ExamPart của đề này
        List<ExamPart> examParts = examPartRepository.findAllByExam_Id(req.getExamId());

        Set<UserExamPart> userExamParts = new HashSet<>();
        for (ExamPart part : examParts) {
            UserExamPart userExamPart = UserExamPart.builder()
                    .userExam(userExam)
                    .skillType(part.getSkillType())
                    .score(0.0)
                    .submitted(false)
                    .build();
            userExamPartRepository.save(userExamPart);
            userExamParts.add(userExamPart);
        }

        userExam.setParts(userExamParts);
        UserExam save = userExamRepository.save(userExam);

        return userExamMapper.toUserExamResponse(save);
    }

    @Transactional
    @Override
    public List<UserExamResponse> getUserExams() {
        User user = checkUser();
        List<UserExam> exams = userExamRepository.findAllByUser_Id(user.getId());
        return exams.stream()
                .map(userExamMapper::toUserExamResponse)
                .collect(Collectors.toList());
    }


    private User checkUser(){
        return userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
