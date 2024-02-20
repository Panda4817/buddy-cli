package dev.kmunton.buddy.services;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Candidate;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VertexAiService {

    @Value("${vertex.ai.project.id}")
    private String vertexAiProjectId;

    @Value("${vertex.ai.location}")
    private String vertexAiLocation;

    public String getAnswer(String prompt) throws IOException {
        try (VertexAI vertexAi = new VertexAI(vertexAiProjectId, vertexAiLocation) ) {
            GenerationConfig generationConfig =
                GenerationConfig.newBuilder()
                    .setMaxOutputTokens(2000)
                    .setTemperature(0.6F)
                    .setTopP(1F)
                    .build();
            GenerativeModel model = new GenerativeModel("gemini-pro", generationConfig, vertexAi);
            List<SafetySetting> safetySettings = Arrays.asList(
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_LOW_AND_ABOVE)
                    .build(),
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_LOW_AND_ABOVE)
                    .build(),
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_LOW_AND_ABOVE)
                    .build(),
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_LOW_AND_ABOVE)
                    .build()
            );
            List<Content> contents = new ArrayList<>();
            contents.add(Content.newBuilder().setRole("user").addParts(Part.newBuilder().setText(prompt)).build());

            ResponseStream<GenerateContentResponse> responseStream =
                model.generateContentStream(contents, safetySettings);

            StringBuilder answer = new StringBuilder();
            responseStream.stream().forEach(generateContentResponse -> {
                var candidates = generateContentResponse.getCandidatesList();
                for (Candidate candidate: candidates) {
                    var parts = candidate.getContent().getPartsList();
                    for (Part part : parts) {
                        answer.append(part.getText());
                    }
                }
            });
            return answer.toString();
        }
    }
}
