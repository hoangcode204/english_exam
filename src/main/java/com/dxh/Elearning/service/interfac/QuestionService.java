package com.dxh.Elearning.service.interfac;


import com.dxh.Elearning.dto.request.QuestionRequest;
import com.dxh.Elearning.dto.response.ExamResponse;
import com.dxh.Elearning.dto.response.PageResponse;
import com.dxh.Elearning.dto.response.QuestionResponse;
import com.dxh.Elearning.enums.SkillType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {

    QuestionResponse createQuestionReading(QuestionRequest req);
    QuestionResponse createQuestionListening(QuestionRequest req, MultipartFile audioFile);
    QuestionResponse createQuestionWriting(QuestionRequest req);
    QuestionResponse createQuestionSpeaking(QuestionRequest req);

    PageResponse<List<QuestionResponse>> getQuestionsByExamPart(Long examPartId, int pageNo, int pageSize, String sortBy);
}
