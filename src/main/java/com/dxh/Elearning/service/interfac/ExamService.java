package com.dxh.Elearning.service.interfac;

import com.dxh.Elearning.dto.request.ExamRequest;
import com.dxh.Elearning.dto.response.ExamResponse;
import com.dxh.Elearning.dto.response.PageResponse;
import com.dxh.Elearning.dto.response.UserResponse;

import java.util.List;

public interface ExamService {
    ExamResponse create(ExamRequest req);

    void delete(Long examId);

    PageResponse<List<ExamResponse>> getAllExamsSortBy(int pageNo, int pageSize, String sortBy);

}
