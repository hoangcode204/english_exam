package com.dxh.Elearning.controller;

import com.dxh.Elearning.dto.request.ExamRequest;
import com.dxh.Elearning.dto.request.UserExamRequest;
import com.dxh.Elearning.dto.response.ApiResponse;
import com.dxh.Elearning.dto.response.ExamResponse;
import com.dxh.Elearning.dto.response.PageResponse;
import com.dxh.Elearning.dto.response.UserExamResponse;
import com.dxh.Elearning.service.interfac.ExamService;
import com.dxh.Elearning.service.interfac.UserExamService;
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
@RequestMapping("/userexams")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserExamController {
    UserExamService userExamService;

    @Operation(method = "POST", summary = "Add new user exam", description = "Send a request via this API to create new user exam")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ApiResponse<UserExamResponse> createExam(@RequestBody UserExamRequest req) {
        return ApiResponse.<UserExamResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Successfully created user exam")
                .result(userExamService.create(req)).build();
    }

}
