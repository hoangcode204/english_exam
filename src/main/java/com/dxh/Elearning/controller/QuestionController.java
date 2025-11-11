package com.dxh.Elearning.controller;

import com.dxh.Elearning.dto.request.ExamPartRequest;
import com.dxh.Elearning.dto.request.QuestionRequest;
import com.dxh.Elearning.dto.response.ApiResponse;
import com.dxh.Elearning.dto.response.ExamPartResponse;
import com.dxh.Elearning.dto.response.PageResponse;
import com.dxh.Elearning.dto.response.QuestionResponse;
import com.dxh.Elearning.enums.SkillType;
import com.dxh.Elearning.service.interfac.ExamPartService;
import com.dxh.Elearning.service.interfac.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuestionController {
    QuestionService questionService;

    @Operation(method = "POST", summary = "Add new question reading", description = "Send a request via this API to create new question reading")
    @PostMapping("/reading")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<QuestionResponse> createExamPart(@RequestBody QuestionRequest req) {
        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Successfully created question")
                .result(questionService.createQuestionReading(req)).build();
    }

    @Operation(
            method = "POST",
            summary = "Add new question listening",
            description = "Send a request via this API to create new question listening"
    )
    @PostMapping("/listening")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<QuestionResponse> createQuestionListening(
            @RequestPart("question") QuestionRequest req,
            @RequestPart("audio") MultipartFile audioFile) {

        QuestionResponse response = questionService.createQuestionListening(req, audioFile);

        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Successfully created listening question")
                .result(response)
                .build();
    }

    @Operation(
            method = "POST",
            summary = "Add new question speaking",
            description = "Send a request via this API to create new question speaking"
    )
    @PostMapping("/speaking")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<QuestionResponse> createSpeakingQuestion(@RequestBody QuestionRequest req) {
        QuestionResponse response = questionService.createQuestionSpeaking(req);
        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Successfully created speaking question")
                .result(response)
                .build();
    }

    @Operation(
            method = "POST",
            summary = "Add new question writing",
            description = "Send a request via this API to create new question writing"
    )
    @PostMapping("/writing")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<QuestionResponse> createWritingQuestion(@RequestBody QuestionRequest req) {
        QuestionResponse response = questionService.createQuestionWriting(req);
        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Successfully created writing question")
                .result(response)
                .build();
    }

    @Operation(
            method = "GET",
            summary = "Get list of questions by examPart",
            description = "Retrieve paginated list of questions filtered by examPartId and skillType"
    )
    @GetMapping("/list")
    public ApiResponse<PageResponse<List<QuestionResponse>>> getQuestionsByExamPart(
            @RequestParam Long examPartId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {

        return ApiResponse.<PageResponse<List<QuestionResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully fetched questions")
                .result(questionService.getQuestionsByExamPart(examPartId, pageNo, pageSize, sortBy))
                .build();
    }

}
