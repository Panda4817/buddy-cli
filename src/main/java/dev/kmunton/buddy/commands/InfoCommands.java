package dev.kmunton.buddy.commands;

import com.google.cloud.vertexai.api.Candidate;
import dev.kmunton.buddy.clients.OpenAiClient;
import dev.kmunton.buddy.clients.StackOverflowClient;
import dev.kmunton.buddy.models.openai.OpenAiMessage;
import dev.kmunton.buddy.models.openai.OpenAiRequest;
import dev.kmunton.buddy.models.openai.OpenAiResponse;
import dev.kmunton.buddy.models.stackoverflow.StackOverflowResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.table.ArrayTableModel;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.kmunton.buddy.styling.TableUtils.renderLightTable;

@Command(group = "Info Commands")
public class InfoCommands {

    @Autowired
    private OpenAiClient openAiClient;

    @Autowired
    private StackOverflowClient stackOverflowClient;

    @Value("${vertex.ai.project.id}")
    private String vertexAiProjectId;

    @Value("${vertex.ai.location}")
    private String vertexAiLocation;

    @Command(command = "gpt", description = "Ask ChatGPT a question")
    public String askChatGpt(@Option(longNames = {"question"}, shortNames = {'q'}, required = true,
            description = "Your question in single or double quotes") String question) {
        OpenAiRequest request = new OpenAiRequest("gpt-3.5-turbo", List.of(new OpenAiMessage("user", question)));

        try {
            OpenAiResponse response = openAiClient.getChatGptAnswer(request);
            return response.choices().get(0).message().content();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Try https://chat.openai.com/";
        }

    }

    @Command(command = "stack", description = "Get a list of StackOverflow questions matching your query")
    public String askStackOverflow(@Option(longNames = {"query"}, shortNames = {'q'}, required = true,
            description = "Your query in single or double quotes") String query) {

        StackOverflowResponse response;

        try {
            response = stackOverflowClient.getQuestions(query, "desc", "relevance", "stackoverflow");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Try https://stackoverflow.com/";
        }

        if (response.items().isEmpty()) {
            return "No matching questions found :(";
        }

        ArrayTableModel model = new ArrayTableModel(response.items().stream().map(item -> new String[]{
                "https://stackoverflow.com/q/%s".formatted(item.question_id()),
                item.title(),
                item.is_answered() ? "answered" : ""
        }).toArray(String[][]::new));
        return renderLightTable(model);

    }

    @Command(command = "book", description = "Summarise the key points from a non-fiction book")
    public String summariseBookByVertexAI(@Option(longNames = {"title"}, shortNames = {'t'}, required = true,
        description = "Title of book in single or double quotes") String title, @Option(longNames = {"author"}, shortNames = {'a'},
        required = true, description = "Author(s) of the book in single or double quotes") String author) {

        String prompt = """
            Summarise the key takeaways and concepts from %s book by %s.
            Make sure output is detailed with key points and explanations.
            """.formatted(title, author);

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
            // Do something with the response
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Check Google Cloud dashboard";
        }
    }
}
