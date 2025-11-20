package com.dxh.Elearning.service.impl;

import com.dxh.Elearning.dto.request.WritingSubmissionRequest;
import com.dxh.Elearning.dto.response.AIGradingResponse;
import com.dxh.Elearning.entity.Question;
import com.dxh.Elearning.entity.UserExamPart;
import com.dxh.Elearning.enums.QuestionType;
import com.dxh.Elearning.enums.SkillType;
import com.dxh.Elearning.repo.QuestionRepository;
import com.dxh.Elearning.repo.UserExamPartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test cho AI Grading Service
 * Chỉ chạy test này khi đã có dữ liệu trong database
 */
@SpringBootTest
class AIGradingServiceImplTest {

    @Autowired
    private AIGradingServiceImpl aiGradingService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserExamPartRepository userExamPartRepository;

    /**
     * Test chấm bài Writing
     * Cần có question WRITING và userExamPart WRITING trong DB
     */
    @Test
    void testGradeWriting_Success() {
        // Arrange - Tạo dữ liệu test
        String sampleEssay = """
                Climate change is one of the most pressing issues facing humanity today. 
                The increasing concentration of greenhouse gases in the atmosphere has led to 
                rising global temperatures, melting ice caps, and more frequent extreme weather events.
                
                To address this challenge, governments and individuals must take immediate action. 
                Firstly, we need to transition to renewable energy sources such as solar and wind power. 
                Secondly, reducing deforestation and promoting reforestation efforts are crucial. 
                Finally, individuals can contribute by adopting sustainable lifestyles.
                
                In conclusion, while climate change poses significant threats, collective action 
                can help mitigate its worst effects and create a more sustainable future for all.
                """;

        // Tìm một question Writing từ DB (hoặc tạo mới)
        Question question = questionRepository.findAll().stream()
                .filter(q -> q.getType() == QuestionType.WRITING)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No WRITING question found in DB"));

        // Tìm một UserExamPart Writing từ DB (hoặc tạo mới)
        UserExamPart userExamPart = userExamPartRepository.findAll().stream()
                .filter(uep -> uep.getSkillType() == SkillType.WRITING)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No WRITING UserExamPart found in DB"));

        WritingSubmissionRequest request = WritingSubmissionRequest.builder()
                .questionId(question.getId())
                .userExamPartId(userExamPart.getId())
                .answerText(sampleEssay)
                .build();

        // Act
        AIGradingResponse response = aiGradingService.gradeWriting(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getUserAnswerId());
        assertNotNull(response.getAiScore());
        assertNotNull(response.getAiFeedback());
        assertTrue(response.getAiScore() >= 0 && response.getAiScore() <= question.getMaxScore());
        
        assertNotNull(response.getGrammarAnalysis());
        assertNotNull(response.getVocabularyAnalysis());
        assertNotNull(response.getCoherenceAnalysis());

        System.out.println("=== AI Grading Result ===");
        System.out.println("Score: " + response.getAiScore());
        System.out.println("Feedback: " + response.getAiFeedback());
        System.out.println("Grammar Score: " + response.getGrammarAnalysis().getScore());
        System.out.println("Vocabulary Level: " + response.getVocabularyAnalysis().getLevel());
    }

    /**
     * Test chấm bài Speaking với transcript có sẵn
     */
    @Test
    void testGradeSpeaking_WithTranscript() {
        // Test này cần có:
        // 1. Question SPEAKING trong DB
        // 2. UserExamPart SPEAKING trong DB
        // 3. Transcript text
        
        // TODO: Implement when you have speaking questions and user exam parts
        assertTrue(true, "Speaking test not yet implemented");
    }
}
