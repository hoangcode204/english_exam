package com.dxh.Elearning.service.impl;

import com.dxh.Elearning.dto.request.ExamPartRequest;
import com.dxh.Elearning.dto.request.ExamRequest;
import com.dxh.Elearning.dto.response.ExamPartResponse;
import com.dxh.Elearning.dto.response.ExamResponse;
import com.dxh.Elearning.dto.response.PageResponse;
import com.dxh.Elearning.entity.Exam;
import com.dxh.Elearning.entity.ExamPart;
import com.dxh.Elearning.exception.AppException;
import com.dxh.Elearning.exception.ErrorCode;
import com.dxh.Elearning.mapper.ExamMapper;
import com.dxh.Elearning.mapper.ExamPartMapper;
import com.dxh.Elearning.repo.ExamPartRepository;
import com.dxh.Elearning.repo.ExamRepository;
import com.dxh.Elearning.service.interfac.ExamPartService;
import com.dxh.Elearning.service.interfac.ExamService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dxh.Elearning.utils.AppConstant.SORT_BY;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ExamPartServiceImpl implements ExamPartService {

    ExamRepository examRepository;
    ExamPartRepository examPartRepository;
    ExamPartMapper examPartMapper;

    @Override
    public ExamPartResponse create(ExamPartRequest req) {

        Exam exam = examRepository.findById(req.getExamId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_EXISTED));

        ExamPart examPart = ExamPart.builder()
                .exam(exam)
                .skillType(req.getSkillType())
                .duration(req.getDuration())
                .build();

        examPartRepository.save(examPart);
        return examPartMapper.toExamPartResponse(examPart);
    }

    @Override
    public List<ExamPartResponse> findAllByExamId(Long examId) {
        List<ExamPart> list = examPartRepository.findAllByExam_Id(examId);
        return list.stream().map(examPartMapper::toExamPartResponse).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long examPartId) {
        examPartRepository.deleteById(examPartId);
    }


}
