package com.dxh.Elearning.service.impl;

import com.dxh.Elearning.dto.request.SpeakingSubmissionRequest;
import com.dxh.Elearning.dto.request.WritingSubmissionRequest;
import com.dxh.Elearning.dto.response.AIGradingResponse;
import com.dxh.Elearning.entity.Question;
import com.dxh.Elearning.entity.UserAnswer;
import com.dxh.Elearning.entity.UserExamPart;
import com.dxh.Elearning.enums.QuestionType;
import com.dxh.Elearning.enums.SkillType;
import com.dxh.Elearning.exception.AppException;
import com.dxh.Elearning.exception.ErrorCode;
import com.dxh.Elearning.repo.QuestionRepository;
import com.dxh.Elearning.repo.UserAnswerRepository;
import com.dxh.Elearning.repo.UserExamPartRepository;
import com.dxh.Elearning.service.interfac.AIGradingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AIGradingServiceImpl implements AIGradingService {

    ChatModel chatModel;
    QuestionRepository questionRepository;
    UserExamPartRepository userExamPartRepository;
    UserAnswerRepository userAnswerRepository;
    ObjectMapper objectMapper;
    ScoreCalculationService scoreCalculationService;

    @Override
    @Transactional
    public AIGradingResponse gradeWriting(WritingSubmissionRequest request) {
        log.info("Grading writing submission for question: {}", request.getQuestionId());

        // Validate question
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        if (question.getType() != QuestionType.WRITING) {
            throw new AppException(ErrorCode.INVALID_QUESTION_TYPE);
        }

        // Validate user exam part
        UserExamPart userExamPart = userExamPartRepository.findById(request.getUserExamPartId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXAM_PART_NOT_FOUND));

        if (userExamPart.getSkillType() != SkillType.WRITING) {
            throw new AppException(ErrorCode.INVALID_SKILL_TYPE);
        }

        // Create or update user answer
        UserAnswer userAnswer = UserAnswer.builder()
                .userExamPart(userExamPart)
                .question(question)
                .answerText(request.getAnswerText())
                .build();

        // Call AI to grade
        String aiResponse = callAIForWritingGrading(question, request.getAnswerText());
        
        // Parse AI response
        AIGradingResponse gradingResponse = parseWritingGradingResponse(aiResponse);

        // Update user answer with AI scores
        userAnswer.setAiScore(gradingResponse.getAiScore());
        userAnswer.setAiFeedback(gradingResponse.getAiFeedback());

        userAnswer = userAnswerRepository.save(userAnswer);
        gradingResponse.setUserAnswerId(userAnswer.getId());

        // Tính lại điểm tổng cho UserExamPart
        scoreCalculationService.calculateAndUpdateScore(userExamPart.getId());

        log.info("Writing graded successfully. Score: {}", gradingResponse.getAiScore());
        return gradingResponse;
    }

    @Override
    @Transactional
    public AIGradingResponse gradeSpeaking(SpeakingSubmissionRequest request) {
        log.info("Grading speaking submission for question: {}", request.getQuestionId());

        // Validate question
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        if (question.getType() != QuestionType.SPEAKING) {
            throw new AppException(ErrorCode.INVALID_QUESTION_TYPE);
        }

        // Validate user exam part
        UserExamPart userExamPart = userExamPartRepository.findById(request.getUserExamPartId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXAM_PART_NOT_FOUND));

        if (userExamPart.getSkillType() != SkillType.SPEAKING) {
            throw new AppException(ErrorCode.INVALID_SKILL_TYPE);
        }

        // Get transcript (from request or need to transcribe audio)
        String transcript = request.getTranscript();
        if (transcript == null || transcript.isBlank()) {
            // TODO: Implement audio transcription using Whisper API or similar
            throw new AppException(ErrorCode.TRANSCRIPT_REQUIRED);
        }

        // Create or update user answer
        UserAnswer userAnswer = UserAnswer.builder()
                .userExamPart(userExamPart)
                .question(question)
                .audioUrl(request.getAudioUrl())
                .answerText(transcript)
                .build();

        // Call AI to grade
        String aiResponse = callAIForSpeakingGrading(question, transcript);
        
        // Parse AI response
        AIGradingResponse gradingResponse = parseSpeakingGradingResponse(aiResponse);

        // Update user answer with AI scores
        userAnswer.setAiScore(gradingResponse.getAiScore());
        userAnswer.setAiFeedback(gradingResponse.getAiFeedback());

        userAnswer = userAnswerRepository.save(userAnswer);
        gradingResponse.setUserAnswerId(userAnswer.getId());

        // Tính lại điểm tổng cho UserExamPart
        scoreCalculationService.calculateAndUpdateScore(userExamPart.getId());

        log.info("Speaking graded successfully. Score: {}", gradingResponse.getAiScore());
        return gradingResponse;
    }

    @Override
    @Transactional
    public AIGradingResponse regradeAnswer(Long userAnswerId) {
        log.info("Re-grading user answer: {}", userAnswerId);

        UserAnswer userAnswer = userAnswerRepository.findById(userAnswerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_ANSWER_NOT_FOUND));

        Question question = userAnswer.getQuestion();

        if (question.getType() == QuestionType.WRITING) {
            String aiResponse = callAIForWritingGrading(question, userAnswer.getAnswerText());
            AIGradingResponse gradingResponse = parseWritingGradingResponse(aiResponse);
            
            userAnswer.setAiScore(gradingResponse.getAiScore());
            userAnswer.setAiFeedback(gradingResponse.getAiFeedback());
            userAnswerRepository.save(userAnswer);
            
            gradingResponse.setUserAnswerId(userAnswerId);
            return gradingResponse;
            
        } else if (question.getType() == QuestionType.SPEAKING) {
            String aiResponse = callAIForSpeakingGrading(question, userAnswer.getAnswerText());
            AIGradingResponse gradingResponse = parseSpeakingGradingResponse(aiResponse);
            
            userAnswer.setAiScore(gradingResponse.getAiScore());
            userAnswer.setAiFeedback(gradingResponse.getAiFeedback());
            userAnswerRepository.save(userAnswer);
            
            gradingResponse.setUserAnswerId(userAnswerId);
            return gradingResponse;
        }

        throw new AppException(ErrorCode.INVALID_QUESTION_TYPE);
    }

    private String callAIForWritingGrading(Question question, String answerText) {
        String promptTemplate = """
                You are an experienced IELTS/TOEFL examiner. Grade the following writing response.
                
                Question: {questionContent}
                Maximum Score: {maxScore}
                
                Student's Answer:
                {answerText}
                
                IMPORTANT: You must respond ONLY with valid JSON. Do not include any explanatory text before or after the JSON.
                
                Provide your evaluation in this EXACT JSON format:
                {{
                  "totalScore": 8.5,
                  "feedback": "Overall feedback in Vietnamese",
                  "detailedAnalysis": "Detailed analysis in Vietnamese",
                  "grammar": {{
                    "score": 22,
                    "feedback": "Grammar feedback in Vietnamese",
                    "errorCount": 2
                  }},
                  "vocabulary": {{
                    "score": 23,
                    "feedback": "Vocabulary feedback in Vietnamese",
                    "level": "B2"
                  }},
                  "coherence": {{
                    "score": 21,
                    "feedback": "Coherence feedback in Vietnamese"
                  }}
                }}
                
                Evaluation criteria:
                1. Grammar: Accuracy and range of grammatical structures (max 25 points)
                2. Vocabulary: Range, accuracy, and appropriateness (max 25 points)
                3. Coherence: Logical flow and organization (max 25 points)
                4. Task Achievement: Addressing requirements (max 25 points)
                
                totalScore should be out of {maxScore}.
                Provide constructive feedback in Vietnamese.
                Return ONLY the JSON object, nothing else.
                """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("questionContent", question.getContent());
        variables.put("maxScore", question.getMaxScore());
        variables.put("answerText", answerText);

        PromptTemplate template = new PromptTemplate(promptTemplate);
        Prompt prompt = template.create(variables);

        String response = chatModel.call(prompt).getResult().getOutput().getContent();
        log.info("AI Writing Grading Response received. Length: {} chars", response.length());
        return response;
    }

    private String callAIForSpeakingGrading(Question question, String transcript) {
        String promptTemplate = """
                You are an experienced IELTS/TOEFL speaking examiner. Grade the following speaking response.
                
                Question: {questionContent}
                Maximum Score: {maxScore}
                
                Transcript of Student's Answer:
                {transcript}
                
                IMPORTANT: You must respond ONLY with valid JSON. Do not include any explanatory text before or after the JSON.
                
                Provide your evaluation in this EXACT JSON format:
                {{
                  "totalScore": 8.5,
                  "feedback": "Overall feedback in Vietnamese",
                  "detailedAnalysis": "Detailed analysis in Vietnamese",
                  "pronunciation": {{
                    "score": 22,
                    "feedback": "Pronunciation feedback in Vietnamese"
                  }},
                  "fluency": {{
                    "score": 23,
                    "feedback": "Fluency feedback in Vietnamese",
                    "estimatedWPM": 150
                  }},
                  "vocabulary": {{
                    "score": 21,
                    "feedback": "Vocabulary feedback in Vietnamese",
                    "level": "B2"
                  }},
                  "grammar": {{
                    "score": 20,
                    "feedback": "Grammar feedback in Vietnamese",
                    "errorCount": 3
                  }}
                }}
                
                Evaluation criteria:
                1. Pronunciation: Clarity and intelligibility (max 25 points)
                2. Fluency: Speaking pace and natural flow (max 25 points)
                3. Vocabulary: Range and appropriateness (max 25 points)
                4. Grammar: Accuracy and complexity (max 25 points)
                
                totalScore should be out of {maxScore}.
                Estimate words per minute based on transcript length.
                Provide constructive feedback in Vietnamese.
                Return ONLY the JSON object, nothing else.
                """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("questionContent", question.getContent());
        variables.put("maxScore", question.getMaxScore());
        variables.put("transcript", transcript);

        PromptTemplate template = new PromptTemplate(promptTemplate);
        Prompt prompt = template.create(variables);

        String response = chatModel.call(prompt).getResult().getOutput().getContent();
        log.info("AI Speaking Grading Response received. Length: {} chars", response.length());
        return response;
    }

    private AIGradingResponse parseWritingGradingResponse(String aiResponse) {
        try {
            log.debug("Raw AI Response: {}", aiResponse);
            
            // Remove markdown code block if present
            String jsonResponse = aiResponse.trim();
            if (jsonResponse.startsWith("```json")) {
                jsonResponse = jsonResponse.substring(7);
            }
            if (jsonResponse.startsWith("```")) {
                jsonResponse = jsonResponse.substring(3);
            }
            if (jsonResponse.endsWith("```")) {
                jsonResponse = jsonResponse.substring(0, jsonResponse.length() - 3);
            }
            jsonResponse = jsonResponse.trim();
            
            // Find JSON object boundaries
            int jsonStart = jsonResponse.indexOf('{');
            int jsonEnd = jsonResponse.lastIndexOf('}');
            
            if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                jsonResponse = jsonResponse.substring(jsonStart, jsonEnd + 1);
            }
            
            log.debug("Cleaned JSON: {}", jsonResponse);

            JsonNode root = objectMapper.readTree(jsonResponse);

            return AIGradingResponse.builder()
                    .aiScore(root.path("totalScore").asDouble(0.0))
                    .aiFeedback(root.path("feedback").asText("No feedback provided"))
                    .detailedAnalysis(root.path("detailedAnalysis").asText("No detailed analysis"))
                    .grammarAnalysis(AIGradingResponse.GrammarAnalysis.builder()
                            .score(root.path("grammar").path("score").asDouble(0.0))
                            .feedback(root.path("grammar").path("feedback").asText(""))
                            .errorCount(root.path("grammar").path("errorCount").asInt(0))
                            .build())
                    .vocabularyAnalysis(AIGradingResponse.VocabularyAnalysis.builder()
                            .score(root.path("vocabulary").path("score").asDouble(0.0))
                            .feedback(root.path("vocabulary").path("feedback").asText(""))
                            .level(root.path("vocabulary").path("level").asText(""))
                            .build())
                    .coherenceAnalysis(AIGradingResponse.CoherenceAnalysis.builder()
                            .score(root.path("coherence").path("score").asDouble(0.0))
                            .feedback(root.path("coherence").path("feedback").asText(""))
                            .build())
                    .build();
        } catch (Exception e) {
            log.error("Failed to parse AI response. Raw response: {}", aiResponse, e);
            throw new AppException(ErrorCode.AI_RESPONSE_PARSE_ERROR);
        }
    }

    private AIGradingResponse parseSpeakingGradingResponse(String aiResponse) {
        try {
            log.debug("Raw AI Response: {}", aiResponse);
            
            // Remove markdown code block if present
            String jsonResponse = aiResponse.trim();
            if (jsonResponse.startsWith("```json")) {
                jsonResponse = jsonResponse.substring(7);
            }
            if (jsonResponse.startsWith("```")) {
                jsonResponse = jsonResponse.substring(3);
            }
            if (jsonResponse.endsWith("```")) {
                jsonResponse = jsonResponse.substring(0, jsonResponse.length() - 3);
            }
            jsonResponse = jsonResponse.trim();
            
            // Find JSON object boundaries
            int jsonStart = jsonResponse.indexOf('{');
            int jsonEnd = jsonResponse.lastIndexOf('}');
            
            if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                jsonResponse = jsonResponse.substring(jsonStart, jsonEnd + 1);
            }
            
            log.debug("Cleaned JSON: {}", jsonResponse);

            JsonNode root = objectMapper.readTree(jsonResponse);

            return AIGradingResponse.builder()
                    .aiScore(root.path("totalScore").asDouble(0.0))
                    .aiFeedback(root.path("feedback").asText("No feedback provided"))
                    .detailedAnalysis(root.path("detailedAnalysis").asText("No detailed analysis"))
                    .pronunciationAnalysis(AIGradingResponse.PronunciationAnalysis.builder()
                            .score(root.path("pronunciation").path("score").asDouble(0.0))
                            .feedback(root.path("pronunciation").path("feedback").asText(""))
                            .build())
                    .fluencyAnalysis(AIGradingResponse.FluencyAnalysis.builder()
                            .score(root.path("fluency").path("score").asDouble(0.0))
                            .feedback(root.path("fluency").path("feedback").asText(""))
                            .wordsPerMinute(root.path("fluency").path("estimatedWPM").asInt(0))
                            .build())
                    .vocabularyAnalysis(AIGradingResponse.VocabularyAnalysis.builder()
                            .score(root.path("vocabulary").path("score").asDouble(0.0))
                            .feedback(root.path("vocabulary").path("feedback").asText(""))
                            .level(root.path("vocabulary").path("level").asText(""))
                            .build())
                    .grammarAnalysis(AIGradingResponse.GrammarAnalysis.builder()
                            .score(root.path("grammar").path("score").asDouble(0.0))
                            .feedback(root.path("grammar").path("feedback").asText(""))
                            .errorCount(root.path("grammar").path("errorCount").asInt(0))
                            .build())
                    .build();
        } catch (Exception e) {
            log.error("Failed to parse AI response. Raw response: {}", aiResponse, e);
            throw new AppException(ErrorCode.AI_RESPONSE_PARSE_ERROR);
        }
    }
}
