package com.dxh.Elearning.mapper;

import com.dxh.Elearning.dto.request.ExamRequest;
import com.dxh.Elearning.dto.response.AddressResponse;
import com.dxh.Elearning.dto.response.ExamResponse;
import com.dxh.Elearning.entity.Address;
import com.dxh.Elearning.entity.Exam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExamMapper {
    Exam toExam(ExamRequest req);
    ExamResponse toExamResponse(Exam exam);
}
