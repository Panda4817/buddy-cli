package dev.kmunton.buddy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.autoconfigure.ShellTest;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@ShellTest
@ComponentScan("dev.kmunton.buddy")
class ApplicationTests {

	@Autowired
	private ShellTestClient client;

	@Test
	void givenHelpIsTyped_whenApplicationStarts_thenShowAllOptions() {

		// Given
		String command = "help";

		// When
		ShellTestClient.InteractiveShellSession session = client
				.interactive()
				.run();

		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
				.containsText("my-buddy"));

		session.write(session.writeSequence().text(command).carriageReturn().build());

		// Then
		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
				.containsText("AVAILABLE COMMANDS"));
	}

}
