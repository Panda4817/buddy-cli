package dev.kmunton.buddy.commands;

import dev.kmunton.buddy.clients.DadJokeClient;
import dev.kmunton.buddy.clients.XkcdClient;
import dev.kmunton.buddy.models.dadjoke.DadJokeResponse;
import dev.kmunton.buddy.models.dadjoke.DadJokesList;
import dev.kmunton.buddy.models.xkcd.XkcdComicResponse;
import dev.kmunton.buddy.services.RockPaperScissorsGameService;
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

    @MockBean
    private XkcdClient xkcdClient;

    @MockBean
    private RockPaperScissorsGameService rockPaperScissorsGameService;

    @Test
    void givenRandomJokeWanted_whenDadJokeCommandProvided_returnRandomJoke() {
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
    void givenSearchTermProvided_whenDadJokeCommandProvided_returnJokesList() {
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

    @Test
    void givenCurrentXkcdComic_whenComicCommandProvided_returnComic() {
        // Given
        XkcdComicResponse response = new XkcdComicResponse("title", "image link", "alt text");
        when(xkcdClient.getCurrentComic()).thenReturn(response);
        String command = "comic";


        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText(response.title()));

    }

    @Test
    void givenUserWins_whenRpsPlayed_returnUserWinText() {
        // Given
        when(rockPaperScissorsGameService.getOptions()).thenReturn(new String[]{"rock", "paper", "scissors"});
        when(rockPaperScissorsGameService.getRandomChoice()).thenReturn("scissors");
        String expectedText = "user win text";
        when(rockPaperScissorsGameService.calculateWinnerBetweenBuddyAndUser("scissors", "rock"))
                .thenReturn(expectedText);
        String command = "rps";


        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText(expectedText));

    }

    @Test
    void givenBuddyWins_whenRpsPlayed_returnUBuddyWinText() {
        // Given
        when(rockPaperScissorsGameService.getOptions()).thenReturn(new String[]{"rock", "paper", "scissors"});
        when(rockPaperScissorsGameService.getRandomChoice()).thenReturn("scissors");
        String expectedText = "buddy win text";
        when(rockPaperScissorsGameService.calculateWinnerBetweenBuddyAndUser("scissors", "paper"))
                .thenReturn(expectedText);
        String command = "rps";


        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().keyDown().carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText(expectedText));

    }

}
