package com.dxh.Elearning.controller;

import com.dxh.Elearning.dto.request.SubmitRLPartRequest;
import com.dxh.Elearning.dto.request.UserExamRequest;
import com.dxh.Elearning.dto.response.ApiResponse;
import com.dxh.Elearning.dto.response.SubmitRLPartResponse;
import com.dxh.Elearning.dto.response.UserExamResponse;
import com.dxh.Elearning.service.interfac.UserExamPartService;
import com.dxh.Elearning.service.interfac.UserExamService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userexamparts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserExamPartController {
    UserExamPartService userExamPartService;

    @Operation(method = "POST", summary = "Submit answer reading/listening", description = "Send a request via this API to submit answer reading/listening")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ApiResponse<?> submitRLPart(@RequestBody SubmitRLPartRequest req) {
        return ApiResponse.<SubmitRLPartResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Part submitted successfully")
                .result(userExamPartService.submitRLPart(req)).build();
    }

}
