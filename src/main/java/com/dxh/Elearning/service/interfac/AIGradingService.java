package com.dxh.Elearning.service.interfac;

import com.dxh.Elearning.dto.request.SpeakingSubmissionRequest;
import com.dxh.Elearning.dto.request.WritingSubmissionRequest;
import com.dxh.Elearning.dto.response.AIGradingResponse;

public interface AIGradingService {
    
    /**
     * Chấm điểm bài Writing
     */
    AIGradingResponse gradeWriting(WritingSubmissionRequest request);
    
    /**
     * Chấm điểm bài Speaking
     */
    AIGradingResponse gradeSpeaking(SpeakingSubmissionRequest request);
    
    /**
     * Chấm lại bài đã submit (re-grade)
     */
    AIGradingResponse regradeAnswer(Long userAnswerId);
}
