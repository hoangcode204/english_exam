package com.dxh.Elearning.service.interfac;

import com.dxh.Elearning.dto.request.ExamPartRequest;
import com.dxh.Elearning.dto.request.ExamRequest;
import com.dxh.Elearning.dto.response.ExamPartResponse;
import com.dxh.Elearning.dto.response.ExamResponse;
import com.dxh.Elearning.dto.response.PageResponse;

import java.util.List;

public interface ExamPartService {
    ExamPartResponse create(ExamPartRequest req);

    List<ExamPartResponse> findAllByExamId(Long examId);

    void delete(Long examPartId);
}
