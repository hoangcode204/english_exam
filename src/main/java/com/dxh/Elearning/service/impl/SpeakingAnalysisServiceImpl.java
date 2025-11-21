package com.dxh.Elearning.service.impl;

import com.dxh.Elearning.dto.response.SpeakingAnalysisResponse;
import com.dxh.Elearning.entity.Question;
import com.dxh.Elearning.exception.AppException;
import com.dxh.Elearning.exception.ErrorCode;
import com.dxh.Elearning.repo.QuestionRepository;
import com.dxh.Elearning.service.interfac.SpeakingAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of speaking analysis service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SpeakingAnalysisServiceImpl implements SpeakingAnalysisService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final QuestionRepository questionRepository;
    
    @Value("${whisper.service.url:http://localhost:8000}")
    private String whisperServiceUrl;

    @Override
    public SpeakingAnalysisResponse analyzeSpeaking(
            MultipartFile audioFile, 
            String referenceText, 
            String language) {
        
        try {
            log.info("Starting speaking analysis for file: {}", audioFile.getOriginalFilename());
            
            // Prepare multipart request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            
            // Add audio file
            ByteArrayResource fileResource = new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            };
            body.add("file", fileResource);
            
            // Add optional parameters
            if (referenceText != null && !referenceText.isEmpty()) {
                body.add("reference_text", referenceText);
            }
            if (language != null && !language.isEmpty()) {
                body.add("language", language);
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = 
                new HttpEntity<>(body, headers);

            // Call speaking analysis endpoint
            String url = whisperServiceUrl + "/analyze-speaking";
            log.info("Calling speaking analysis service at: {}", url);
            
            ResponseEntity<SpeakingAnalysisResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    SpeakingAnalysisResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                SpeakingAnalysisResponse result = response.getBody();
                log.info("Speaking analysis completed successfully. Overall score: {}", 
                    result.getOverallScore());
                return result;
            }

            throw new AppException(ErrorCode.TRANSCRIPTION_FAILED);

        } catch (Exception e) {
            log.error("Error during speaking analysis", e);
            throw new AppException(ErrorCode.TRANSCRIPTION_FAILED);
        }
    }

    @Override
    public SpeakingAnalysisResponse analyzeSpeaking(MultipartFile audioFile, String referenceText) {
        return analyzeSpeaking(audioFile, referenceText, "en");
    }
    
    @Override
    public SpeakingAnalysisResponse analyzeSpeakingByQuestion(MultipartFile audioFile, Long questionId) {
        log.info("Analyzing speaking for questionId: {}", questionId);
        
        // Get question from database
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
        
        // Use question content as reference text
        String referenceText = question.getContent();
        log.info("Using question content as reference: {}", referenceText);
        
        return analyzeSpeaking(audioFile, referenceText, "en");
    }
}
