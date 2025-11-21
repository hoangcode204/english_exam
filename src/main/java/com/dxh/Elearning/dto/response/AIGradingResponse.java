package com.dxh.Elearning.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AIGradingResponse {
    
    Long userAnswerId;
    Double aiScore;
    String aiFeedback;
    String detailedAnalysis;
    
    // Phân tích chi tiết cho Writing
    GrammarAnalysis grammarAnalysis;
    VocabularyAnalysis vocabularyAnalysis;
    CoherenceAnalysis coherenceAnalysis;
    
    // Phân tích chi tiết cho Speaking
    PronunciationAnalysis pronunciationAnalysis;
    FluencyAnalysis fluencyAnalysis;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarAnalysis {
        Double score;
        String feedback;
        Integer errorCount;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VocabularyAnalysis {
        Double score;
        String feedback;
        String level; // A1, A2, B1, B2, C1, C2
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoherenceAnalysis {
        Double score;
        String feedback;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PronunciationAnalysis {
        Double score;
        String feedback;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FluencyAnalysis {
        Double score;
        String feedback;
        Integer wordsPerMinute;
    }
}
