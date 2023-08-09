package dev.kmunton.buddy.commands;

import dev.kmunton.buddy.clients.DadJokeClient;
import dev.kmunton.buddy.models.dadjoke.DadJokeResponse;
import dev.kmunton.buddy.models.dadjoke.DadJokesList;
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
import static org.mockito.Mockito.when;

@ShellTest
@EnableCommand(FunCommands.class)
class FunCommandsTest {

    @Autowired
    private ShellTestClient client;

    @MockBean
    private DadJokeClient dadJokeClient;

    @Test
    public void givenRandomJokeWanted_whenDadJokeCommandProvided_returnRandomJoke() {
        // Given
        DadJokeResponse testResponse = new DadJokeResponse("test", "joke");
        when(dadJokeClient.random()).thenReturn(testResponse);
        String command = "dadjoke";


        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText(testResponse.joke()));

    }

    @Test
    public void givenSearchTermProvided_whenDadJokeCommandProvided_returnJokesList() {
        // Given
        DadJokeResponse testResponse1 = new DadJokeResponse("test1", "joke1");
        DadJokeResponse testResponse2 = new DadJokeResponse("test2", "joke2");
        when(dadJokeClient.search("test", 30)).thenReturn(new DadJokesList(List.of(testResponse1, testResponse2)));
        String command = "dadjoke -s test";


        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText(testResponse1.joke())
                .containsText(testResponse2.joke()));

    }

}