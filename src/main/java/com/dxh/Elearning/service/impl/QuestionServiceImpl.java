package com.dxh.Elearning.service.impl;

import com.dxh.Elearning.dto.request.*;
import com.dxh.Elearning.dto.response.ExamResponse;
import com.dxh.Elearning.dto.response.PageResponse;
import com.dxh.Elearning.dto.response.QuestionResponse;
import com.dxh.Elearning.entity.Exam;
import com.dxh.Elearning.entity.ExamPart;
import com.dxh.Elearning.entity.Option;
import com.dxh.Elearning.entity.Question;
import com.dxh.Elearning.enums.SkillType;
import com.dxh.Elearning.exception.AppException;
import com.dxh.Elearning.exception.ErrorCode;
import com.dxh.Elearning.mapper.OptionMapper;
import com.dxh.Elearning.mapper.QuestionMapper;
import com.dxh.Elearning.repo.ExamPartRepository;
import com.dxh.Elearning.repo.OptionRepository;
import com.dxh.Elearning.repo.QuestionRepository;
import com.dxh.Elearning.service.AwsS3Service;
import com.dxh.Elearning.service.interfac.QuestionService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dxh.Elearning.utils.AppConstant.SORT_BY;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    OptionRepository optionRepository;
    QuestionRepository questionRepository;
    ExamPartRepository examPartRepository;
    QuestionMapper questionMapper;
    AwsS3Service awsS3Service;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse createQuestionReading(QuestionRequest req) {
        // 1️ Lấy ExamPart
        ExamPart examPart = examPartRepository.findById(req.getExamPartId()).orElseThrow(() -> new AppException(ErrorCode.EXAM_PART_NOT_EXISTED));

        // 2️ Tạo Question
        Question question = Question.builder().examPart(examPart).skillType(req.getSkillType()).type(req.getType()).content(req.getContent()).audioUrl(req.getAudioUrl()).maxScore(req.getMaxScore()).build();

        question = questionRepository.save(question);

        // 3️ Tạo Option
        Option correctOption = null;
        List<Option> optionList = new ArrayList<>();
        for (OptionRequest o : req.getOptions()) {
            Option option = Option.builder().content(o.getContent()).question(question).build();
            option = optionRepository.save(option);
            optionList.add(option);

            if (o.getTempId().equals(req.getCorrectTempId())) {
                correctOption = option;
            }
        }
        if (!optionList.isEmpty()) {
            question.setOptions(optionList);
        }

        // 4️ Gán đáp án đúng
        question.setCorrectOption(correctOption);
        questionRepository.save(question);
        return questionMapper.toQuestionResponse(question);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse createQuestionListening(QuestionRequest req, MultipartFile audioFile) {
        // 1️ Lấy ExamPart
        ExamPart examPart = examPartRepository.findById(req.getExamPartId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_PART_NOT_EXISTED));

        // 2️ Upload audio lên S3
        String audioUrl = awsS3Service.saveAudioToS3(audioFile);

        // 3️ Tạo Question
        Question question = Question.builder()
                .examPart(examPart)
                .skillType(req.getSkillType())
                .type(req.getType())
                .content(req.getContent())
                .audioUrl(audioUrl)  // link audio mẫu
                .maxScore(req.getMaxScore())
                .build();

        question = questionRepository.save(question);

        // 4️ Tạo Option
        Option correctOption = null;
        List<Option> optionList = new ArrayList<>();
        for (OptionRequest o : req.getOptions()) {
            Option option = Option.builder()
                    .content(o.getContent())
                    .question(question)
                    .build();
            option = optionRepository.save(option);
            optionList.add(option);

            if (o.getTempId().equals(req.getCorrectTempId())) {
                correctOption = option;
            }
        }
        if (!optionList.isEmpty()) {
            question.setOptions(optionList);
        }

        // 5️ Gán đáp án đúng
        question.setCorrectOption(correctOption);
        questionRepository.save(question);

        return questionMapper.toQuestionResponse(question);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse createQuestionWriting(QuestionRequest req) {
        ExamPart examPart = examPartRepository.findById(req.getExamPartId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_PART_NOT_EXISTED));

        Question question = Question.builder()
                .examPart(examPart)
                .skillType(req.getSkillType())
                .type(req.getType())
                .content(req.getContent())
                .maxScore(req.getMaxScore())
                .build();

        question = questionRepository.save(question);
        return questionMapper.toQuestionResponse(question);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse createQuestionSpeaking(QuestionRequest req) {
        ExamPart examPart = examPartRepository.findById(req.getExamPartId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_PART_NOT_EXISTED));

        Question question = Question.builder()
                .examPart(examPart)
                .skillType(req.getSkillType())
                .type(req.getType())
                .content(req.getContent())
                .maxScore(req.getMaxScore())
                .build();

        question = questionRepository.save(question);
        return questionMapper.toQuestionResponse(question);
    }

    @Override
    public PageResponse<List<QuestionResponse>> getQuestionsByExamPart(Long examPartId, int pageNo, int pageSize, String sortBy) {
        int page = pageNo>0?(pageNo-1):0;
        List<Sort.Order> sorts = new ArrayList<>();


        if (StringUtils.hasLength(sortBy)) {
            // name:asc|desc
            Pattern pattern = Pattern.compile(SORT_BY); // AppConstant.SORT_BY = "(\\w+?)(:)(.*)"
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String field = matcher.group(1);
                String direction = matcher.group(3);
                if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
                    throw new IllegalArgumentException("Sort direction must be 'asc' or 'desc'");
                }
                if (direction.equalsIgnoreCase("asc")) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, field));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, field));
                }
            }
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));

        Page<Question> questions = questionRepository.findByExamPartId(examPartId,pageable);
        List<QuestionResponse> list = questions.stream().map(questionMapper::toQuestionResponse).toList();
        return PageResponse.<List<QuestionResponse>>builder()
                .pageNo(page+1)
                .pageSize(pageSize)
                .totalPage(questions.getTotalPages())
                .items(list)
                .totalElements(questions.getTotalElements())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<QuestionResponse> createMultipleReadingQuestions(ListQuestionRequest req) {

        List<QuestionResponse> responses = new ArrayList<>();

        for (QuestionRequest q : req.getQuestions()) {
            QuestionResponse res = createQuestionReading(q);
            responses.add(res);
        }

        return responses;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse updateQuestionReading(Long questionId, UpdateQuestionReadingRequest req) {
        // 1️⃣ Lấy Question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        // 2️⃣ Update nội dung
        question.setContent(req.getContent());
        question.setAudioUrl(req.getAudioUrl());
        question.setMaxScore(req.getMaxScore());

        // 3️⃣ Xử lý Option
        // Xóa option cũ
        optionRepository.deleteAll(question.getOptions());
        question.getOptions().clear();

        // Tạo option mới
        Option correctOption = null;
        List<Option> newOptions = new ArrayList<>();
        for (OptionRequest o : req.getOptions()) {
            Option option = Option.builder()
                    .content(o.getContent())
                    .question(question)
                    .build();
            option = optionRepository.save(option);
            newOptions.add(option);

            if (o.getTempId().equals(req.getCorrectTempId())) {
                correctOption = option;
            }
        }

        if (!newOptions.isEmpty()) {
            question.setOptions(newOptions);
        }

        // 4️⃣ Gán đáp án đúng
        question.setCorrectOption(correctOption);
        question = questionRepository.save(question);

        // 5️⃣ Map sang Response
        return questionMapper.toQuestionResponse(question);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse updateQuestionListening(Long questionId, UpdateQuestionListeningRequest req, MultipartFile audioFile) {
        // 1️⃣ Lấy question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        // 2️⃣ Update nội dung
        question.setContent(req.getContent());
        question.setMaxScore(req.getMaxScore());

        // 3️⃣ Upload audio mới nếu có
        if (audioFile != null && !audioFile.isEmpty()) {
            String audioUrl = awsS3Service.saveAudioToS3(audioFile);
            question.setAudioUrl(audioUrl);
        }

        // 4️⃣ Xử lý option
        optionRepository.deleteAll(question.getOptions());
        question.getOptions().clear();

        Option correctOption = null;
        List<Option> newOptions = new ArrayList<>();
        for (OptionRequest o : req.getOptions()) {
            Option option = Option.builder()
                    .content(o.getContent())
                    .question(question)
                    .build();
            option = optionRepository.save(option);
            newOptions.add(option);

            if (o.getTempId().equals(req.getCorrectTempId())) {
                correctOption = option;
            }
        }

        if (!newOptions.isEmpty()) {
            question.setOptions(newOptions);
        }

        // 5️⃣ Gán đáp án đúng
        question.setCorrectOption(correctOption);
        question = questionRepository.save(question);

        return questionMapper.toQuestionResponse(question);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse updateQuestionSpeaking(Long questionId, UpdateQuestionSpeakingRequest req) {
        // 1️⃣ Lấy question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        // 2️⃣ Update nội dung
        question.setContent(req.getContent());
        question.setMaxScore(req.getMaxScore());

        // 3️⃣ Lưu lại DB
        question = questionRepository.save(question);

        // 4️⃣ Map sang response
        return questionMapper.toQuestionResponse(question);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse updateQuestionWriting(Long questionId, UpdateQuestionWritingRequest req) {
        // 1️⃣ Lấy question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        // 2️⃣ Update nội dung và điểm tối đa
        question.setContent(req.getContent());
        question.setMaxScore(req.getMaxScore());

        // 3️⃣ Lưu lại DB
        question = questionRepository.save(question);

        // 4️⃣ Map sang response
        return questionMapper.toQuestionResponse(question);
    }


}
