package com.dxh.Elearning.service.interfac;

import com.dxh.Elearning.dto.response.SpeakingAnalysisResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for complete speaking analysis using Whisper
 */
public interface SpeakingAnalysisService {
    
    /**
     * Analyze speaking audio file with all criteria
     * @param audioFile Audio file to analyze
     * @param referenceText Reference text for completeness comparison
     * @param language Language code (e.g., 'en', 'vi')
     * @return Complete analysis with 3 scores
     */
    SpeakingAnalysisResponse analyzeSpeaking(
        MultipartFile audioFile, 
        String referenceText, 
        String language
    );
    
    /**
     * Analyze speaking with default English language
     */
    SpeakingAnalysisResponse analyzeSpeaking(MultipartFile audioFile, String referenceText);
    
    /**
     * Analyze speaking based on question from database
     * @param audioFile Audio file to analyze
     * @param questionId Question ID from database
     * @return Complete analysis with 3 scores
     */
    SpeakingAnalysisResponse analyzeSpeakingByQuestion(MultipartFile audioFile, Long questionId);
}
