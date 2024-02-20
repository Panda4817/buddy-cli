package dev.kmunton.buddy.commands;

import dev.kmunton.buddy.clients.OpenAiClient;
import dev.kmunton.buddy.clients.StackOverflowClient;
import dev.kmunton.buddy.models.openai.OpenAiMessage;
import dev.kmunton.buddy.models.openai.OpenAiRequest;
import dev.kmunton.buddy.models.openai.OpenAiResponse;
import dev.kmunton.buddy.models.stackoverflow.StackOverflowResponse;
import dev.kmunton.buddy.services.VertexAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.table.ArrayTableModel;
import java.util.List;
import org.springframework.stereotype.Component;

import static dev.kmunton.buddy.styling.TableUtils.renderLightTable;

@Component
@Command(group = "Info Commands")
public class InfoCommands {

    private final OpenAiClient openAiClient;

    private final StackOverflowClient stackOverflowClient;

    private final VertexAiService vertexAiService;

    @Autowired
    public InfoCommands(OpenAiClient openAiClient, StackOverflowClient stackOverflowClient,
                        VertexAiService vertexAiService) {
        this.openAiClient = openAiClient;
        this.stackOverflowClient = stackOverflowClient;
        this.vertexAiService = vertexAiService;
    }

    @Command(command = "gpt", description = "Ask ChatGPT a question")
    public String askChatGpt(@Option(longNames = {"question"}, shortNames = {'q'}, required = true,
            description = "Your question in single or double quotes") String question) {
        OpenAiRequest request = new OpenAiRequest("gpt-3.5-turbo", List.of(new OpenAiMessage("user", question)));

        try {
            OpenAiResponse response = openAiClient.getChatGptAnswer(request);
            return response.choices().get(0).message().content();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Check OpenAI API usage or try free https://chat.openai.com/";
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
    public String summariseNonFictionBook(@Option(longNames = {"title"}, shortNames = {'t'}, required = true,
        description = "Title of book in single or double quotes") String title, @Option(longNames = {"author"}, shortNames = {'a'},
        required = true, description = "Author(s) of the book in single or double quotes") String author) {

        String prompt = """
            Summarise the key takeaways and concepts from %s book by %s.
            Make sure output is detailed with key points and explanations.
            """.formatted(title, author);

        try {
            return vertexAiService.getAnswer(prompt);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Check Google Cloud account or try free https://chat.openai.com/";
        }
    }

    @Command(command = "project",
        description = "Generate instructions to create boilerplate project code from a command line tool")
    public String generateBoilerplateProjectInstructions(
        @Option(longNames = {"type"}, shortNames = {'t'}, required = true,
            description = "Project description in single or double quotes e.g. 'java spring boot maven'")
        String projectDescription) {

        String prompt = """
            I want to create a %s project. What is the command to initialise the project from the terminal?
            Explain how to install the command line tool and what the different parts of the command does.
            Explain the project structure and how to run the project once it is initialised.
            """.formatted(projectDescription);

        try {
            return vertexAiService.getAnswer(prompt);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Check Vertex AI API usage or try free https://chat.openai.com/";
        }
    }

    @Command(command = "define", description = "Define an English word")
    public String defineWord(@Option(longNames = {"word"}, shortNames = {'w'}, required = true,
        description = "A word or phrase you want to define, wrap in single or double quotes if spaces") String word) {

        String prompt = "Define the term '%s' in one paragraph".formatted(word);

        try {
            return vertexAiService.getAnswer(prompt);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Check Vertex AI API usage or try free https://chat.openai.com/ or Google search";
        }
    }
}
