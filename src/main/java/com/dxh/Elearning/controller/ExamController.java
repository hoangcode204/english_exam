package com.dxh.Elearning.controller;

import com.dxh.Elearning.dto.request.ExamRequest;
import com.dxh.Elearning.dto.response.ApiResponse;
import com.dxh.Elearning.dto.response.ExamResponse;
import com.dxh.Elearning.dto.response.PageResponse;
import com.dxh.Elearning.dto.response.UserResponse;
import com.dxh.Elearning.service.interfac.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ExamController {
    ExamService examService;

    @Operation(method = "POST", summary = "Add new exam", description = "Send a request via this API to create new exam")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<ExamResponse> createExam(@RequestBody ExamRequest req) {
        return ApiResponse.<ExamResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Successfully created exam")
                .result(examService.create(req)).build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{examId}")
    public ApiResponse<?> delete(@PathVariable Long examId) {
        examService.delete(examId);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("delete exam successful")
                .build();
    }

    @Operation(summary = "Get list of exam per pageNo and sort by one column", description = "Send a request via this API to get exam list by pageNo and pageSize")
    @GetMapping("/list")
    public ApiResponse<PageResponse<List<ExamResponse>>> getAllExamSortBy(@RequestParam(defaultValue = "1", required = false) Integer pageNo,
                                                                           @Min(value = 1,message = "pageSize must be greater than 1") @RequestParam(defaultValue = "20", required = false) Integer pageSize,
                                                                           @RequestParam(required = false) String sortBy) {
        log.info("get all exams");
        return ApiResponse.<PageResponse<List<ExamResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully get exam list")
                .result(examService.getAllExamsSortBy(pageNo,pageSize,sortBy))
                .build();
    }
}
