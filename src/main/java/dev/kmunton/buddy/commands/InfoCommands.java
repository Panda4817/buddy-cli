package dev.kmunton.buddy.commands;

import dev.kmunton.buddy.clients.OpenAiClient;
import dev.kmunton.buddy.clients.StackOverflowClient;
import dev.kmunton.buddy.models.openai.OpenAiMessage;
import dev.kmunton.buddy.models.openai.OpenAiRequest;
import dev.kmunton.buddy.models.openai.OpenAiResponse;
import dev.kmunton.buddy.models.stackoverflow.StackOverflowResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.table.ArrayTableModel;
import java.util.List;

import static dev.kmunton.buddy.styling.TableUtils.renderLightTable;

@Command(group = "Info Commands")
public class InfoCommands {

    @Autowired
    private OpenAiClient openAiClient;

    @Autowired
    private StackOverflowClient stackOverflowClient;

    @Command(command = "askgpt", description = "Ask ChatGPT a question")
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

    @Command(command = "askStack", description = "Get a list of StackOverflow questions matching your query")
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
}
