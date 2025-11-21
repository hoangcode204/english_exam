package com.dxh.Elearning.controller;

import com.dxh.Elearning.dto.request.SpeakingSubmissionRequest;
import com.dxh.Elearning.dto.request.WritingSubmissionRequest;
import com.dxh.Elearning.dto.response.AIGradingResponse;
import com.dxh.Elearning.dto.response.ApiResponse;
import com.dxh.Elearning.service.interfac.AIGradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai-grading")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "AI Grading", description = "AI-powered grading for Writing and Speaking")
public class AIGradingController {

    AIGradingService aiGradingService;

    @PostMapping("/writing")
    @Operation(summary = "Submit and grade writing answer")
    public ApiResponse<AIGradingResponse> gradeWriting(
            @Valid @RequestBody WritingSubmissionRequest request) {
        log.info("Received writing submission for question: {}", request.getQuestionId());
        AIGradingResponse response = aiGradingService.gradeWriting(request);
        return ApiResponse.<AIGradingResponse>builder()
                .code(200)
                .message("Writing graded successfully")
                .result(response)
                .build();
    }

    @PostMapping("/speaking")
    @Operation(summary = "Submit and grade speaking answer")
    public ApiResponse<AIGradingResponse> gradeSpeaking(
            @Valid @RequestBody SpeakingSubmissionRequest request) {
        log.info("Received speaking submission for question: {}", request.getQuestionId());
        AIGradingResponse response = aiGradingService.gradeSpeaking(request);
        return ApiResponse.<AIGradingResponse>builder()
                .code(200)
                .message("Speaking graded successfully")
                .result(response)
                .build();
    }

    @PostMapping("/regrade/{userAnswerId}")
    @Operation(summary = "Re-grade an existing answer")
    public ApiResponse<AIGradingResponse> regradeAnswer(
            @PathVariable Long userAnswerId) {
        log.info("Re-grading answer: {}", userAnswerId);
        AIGradingResponse response = aiGradingService.regradeAnswer(userAnswerId);
        return ApiResponse.<AIGradingResponse>builder()
                .code(200)
                .message("Answer re-graded successfully")
                .result(response)
                .build();
    }
}
