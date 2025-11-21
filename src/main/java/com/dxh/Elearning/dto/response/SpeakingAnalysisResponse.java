package com.dxh.Elearning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Complete speaking analysis response from Whisper service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpeakingAnalysisResponse {
    
    /**
     * Transcribed text
     */
    private String text;
    
    /**
     * Detected language
     */
    private String language;
    
    /**
     * Audio duration in seconds
     */
    private Double duration;
    
    /**
     * Accuracy score (Grammar, Vocabulary, Pronunciation) - 0 to 10
     */
    private Double accuracy;
    
    /**
     * Fluency score (Speaking rate, Natural flow) - 0 to 10
     */
    private Double fluency;
    
    /**
     * Completeness score (Relevance, Depth) - 0 to 10
     */
    private Double completeness;
    
    /**
     * Detailed feedback for accuracy
     */
    @JsonProperty("accuracy_feedback")
    private String accuracyFeedback;
    
    /**
     * Detailed feedback for fluency
     */
    @JsonProperty("fluency_feedback")
    private String fluencyFeedback;
    
    /**
     * Detailed feedback for completeness
     */
    @JsonProperty("completeness_feedback")
    private String completenessFeedback;
    
    /**
     * Overall average score
     */
    @JsonProperty("overall_score")
    private Double overallScore;
}
