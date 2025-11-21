package com.dxh.Elearning.controller;

import com.dxh.Elearning.dto.request.*;
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

    //update question
    @Operation(method = "PUT", summary = "Update reading question", description = "Update a reading question by its ID")
    @PutMapping("/reading/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<QuestionResponse> updateReadingQuestion(
            @PathVariable Long id,
            @RequestBody UpdateQuestionReadingRequest req) {
        QuestionResponse updated = questionService.updateQuestionReading(id, req);
        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Reading question updated successfully")
                .result(updated)
                .build();
    }

    //tạo theo list reading
    @PostMapping("/reading/bulk")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<List<QuestionResponse>> createMultipleReadingQuestions(
            @RequestBody ListQuestionRequest req) {

        return ApiResponse.<List<QuestionResponse>>builder()
                .code(HttpStatus.CREATED.value())
                .message("Successfully created questions")
                .result(questionService.createMultipleReadingQuestions(req))
                .build();
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

    //update question listening
    @Operation(method = "PUT", summary = "Update listening question", description = "Update a listening question by its ID")
    @PutMapping("/listening/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<QuestionResponse> updateQuestionListening(
            @PathVariable Long id,
            @RequestPart("question") UpdateQuestionListeningRequest req,
            @RequestPart(value = "audio", required = false) MultipartFile audioFile) {

        QuestionResponse updated = questionService.updateQuestionListening(id, req, audioFile);

        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Listening question updated successfully")
                .result(updated)
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

    @Operation(method = "PUT", summary = "Update speaking question", description = "Update a speaking question by its ID")
    @PutMapping("/speaking/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<QuestionResponse> updateSpeakingQuestion(
            @PathVariable Long id,
            @RequestBody UpdateQuestionSpeakingRequest req) {

        QuestionResponse updated = questionService.updateQuestionSpeaking(id, req);

        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Speaking question updated successfully")
                .result(updated)
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

    @Operation(method = "PUT", summary = "Update writing question", description = "Update a writing question by its ID")
    @PutMapping("/writing/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<QuestionResponse> updateWritingQuestion(
            @PathVariable Long id,
            @RequestBody UpdateQuestionWritingRequest req) {

        QuestionResponse updated = questionService.updateQuestionWriting(id, req);

        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Writing question updated successfully")
                .result(updated)
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
