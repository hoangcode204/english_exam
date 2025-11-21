package com.dxh.Elearning.controller;

import com.dxh.Elearning.dto.response.ApiResponse;
import com.dxh.Elearning.dto.response.SpeakingAnalysisResponse;
import com.dxh.Elearning.service.interfac.SpeakingAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for speaking analysis with Whisper
 */
@RestController
@RequestMapping("/speaking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Speaking Analysis", description = "Complete speaking analysis: Accuracy + Fluency + Completeness")
public class SpeakingAnalysisController {

    SpeakingAnalysisService speakingAnalysisService;

    @Operation(
            summary = "Analyze speaking with complete criteria",
            description = "Upload audio and get analysis with Accuracy, Fluency, and Completeness scores"
    )
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SpeakingAnalysisResponse> analyzeSpeaking(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "referenceText", required = false) String referenceText,
            @RequestParam(value = "language", defaultValue = "en") String language) {
        
        log.info("Received speaking analysis request for file: {}", file.getOriginalFilename());
        
        SpeakingAnalysisResponse analysis = speakingAnalysisService.analyzeSpeaking(
            file, 
            referenceText, 
            language
        );
        
        return ApiResponse.<SpeakingAnalysisResponse>builder()
                .result(analysis)
                .build();
    }

    @Operation(
            summary = "Analyze for exam submission",
            description = "Complete analysis for speaking exam based on question ID from database"
    )
    @PostMapping(value = "/analyze-exam", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SpeakingAnalysisResponse> analyzeExam(
            @RequestParam("file") MultipartFile file,
            @RequestParam("questionId") Long questionId) {
        
        log.info("Received exam speaking analysis for questionId: {}", questionId);
        
        SpeakingAnalysisResponse analysis = speakingAnalysisService.analyzeSpeakingByQuestion(
            file, 
            questionId
        );
        
        return ApiResponse.<SpeakingAnalysisResponse>builder()
                .result(analysis)
                .build();
    }
}
