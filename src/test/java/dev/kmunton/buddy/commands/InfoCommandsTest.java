package dev.kmunton.buddy.commands;

import dev.kmunton.buddy.clients.OpenAiClient;
import dev.kmunton.buddy.clients.StackOverflowClient;
import dev.kmunton.buddy.models.openai.OpenAiChoice;
import dev.kmunton.buddy.models.openai.OpenAiMessage;
import dev.kmunton.buddy.models.openai.OpenAiRequest;
import dev.kmunton.buddy.models.openai.OpenAiResponse;
import dev.kmunton.buddy.models.stackoverflow.StackOverflowItem;
import dev.kmunton.buddy.models.stackoverflow.StackOverflowResponse;
import dev.kmunton.buddy.services.VertexAiService;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.shell.command.annotation.EnableCommand;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.autoconfigure.ShellTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ShellTest
@EnableCommand(InfoCommands.class)
class InfoCommandsTest {

    @Autowired
    private ShellTestClient client;

    @MockBean
    private OpenAiClient openAiClient;

    @MockBean
    private StackOverflowClient stackOverflowClient;

    @MockBean
    private VertexAiService vertexAiService;

    @Test
    void givenQuestion_whenAskGpt_returnAnswer() {
        // Given
        OpenAiMessage message = new OpenAiMessage("user", "answer");
        OpenAiChoice choice = new OpenAiChoice("id", message);
        OpenAiResponse response = new OpenAiResponse(List.of(choice));
        OpenAiRequest request = new OpenAiRequest("gpt-3.5-turbo", List.of(new OpenAiMessage("user", "question")));
        when(openAiClient.getChatGptAnswer(request)).thenReturn(response);
        String command = "gpt -q question";


        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText(message.content()));

    }

    @Test
    void givenQuery_whenAskStack_returnListOfQuestions() {
        // Given
        StackOverflowItem item1 = new StackOverflowItem("1", "question 1", true);
        StackOverflowItem item2 = new StackOverflowItem("2", "question 2", false);
        when(stackOverflowClient.getQuestions("query", "desc", "relevance", "stackoverflow"))
                .thenReturn(new StackOverflowResponse(List.of(item1, item2)));
        String command = "stack -q query";


        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText(item1.title())
                .containsText(item2.title()));

    }

    @Test
    void givenNonFictionBook_whenSummaryRequested_returnSummary() throws IOException {
        // Given
        when(vertexAiService.getAnswer(anyString())).thenReturn("summary");
        String command = "book -t 'test' -a 'John Smith'";

        // When
        ShellTestClient.InteractiveShellSession session = client
            .interactive()
            .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
            .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
            .containsText("summary"));
    }

    @Test
    void givenProjectDescription_whenBoilerplateInstructionsRequested_returnInstructions() throws IOException {
        // Given
        when(vertexAiService.getAnswer(anyString())).thenReturn("instructions");
        String command = "project -t 'java spring boot maven'";

        // When
        ShellTestClient.InteractiveShellSession session = client
            .interactive()
            .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
            .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
            .containsText("instructions"));
    }

    @Test
    void givenWord_whenDefinitionRequested_returnDefinition() throws IOException {
        // Given
        when(vertexAiService.getAnswer(anyString())).thenReturn("definition");
        String command = "define -w 'word'";

        // When
        ShellTestClient.InteractiveShellSession session = client
            .interactive()
            .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
            .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
            .containsText("definition"));
    }

}
