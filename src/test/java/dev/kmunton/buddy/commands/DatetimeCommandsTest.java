package dev.kmunton.buddy.commands;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.EnableCommand;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.autoconfigure.ShellTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;


@ShellTest
@EnableCommand(DatetimeCommands.class)
class DatetimeCommandsTest {

    @Autowired
    private ShellTestClient client;

    @Test
    public void givenFutureYearAndAllParamsProvided_whenCountdown_returnCountdownString() {
        // Given
        LocalDateTime goal = LocalDateTime.now().plusYears(2);
        String command = String.format("countdown --year %s --month %s --day %s --hour %s --minute %s",
                goal.getYear(), goal.getMonthValue(), goal.getDayOfMonth(), goal.getHour(), goal.getMinute());


        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("2 years"));

    }

    @Test
    public void givenFutureMonthAndSomeParamsProvided_whenCountdown_returnCountdownString() {
        // Given
        LocalDateTime goal = LocalDateTime.now().plusMonths(2);
        String command = String.format("countdown --month %s --day %s --hour %s --minute %s",
                goal.getMonthValue(), goal.getDayOfMonth(), goal.getHour(), goal.getMinute());

        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("2 months"));

    }

    @Test
    public void givenSameDateAndFutureTime_whenCountdown_returnCountdownString() {
        // Given
        LocalDateTime goal = LocalDateTime.now().plusHours(2);
        String command = String.format("countdown --hour %s --minute %s", goal.getHour(), goal.getMinute());

        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("2 hours"));

    }



    @Test
    public void givenDateParamsProvided_whenAdd_returnFutureDate() {
        // Given
        int numberToAdd = 2;
        String expected = LocalDateTime.now()
                .plusYears(numberToAdd).plusMonths(numberToAdd).plusDays(numberToAdd)
                .format(DateTimeFormatter.ofPattern("E dd MMMM yyyy"));
        String command = String.format("add --years %s --months %s --days %s", numberToAdd, numberToAdd, numberToAdd);


        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText(expected));

    }

    @Test
    public void givenOnlyMonthsProvided_whenAdd_returnFutureDate() {
        // Given
        int monthsToAdd = 9;
        String expected = LocalDateTime.now()
                .plusMonths(monthsToAdd)
                .format(DateTimeFormatter.ofPattern("E dd MMMM yyyy"));
        String command = String.format("add --months %s", monthsToAdd);


        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText(expected));

    }

    @Test
    public void givenDateOfBirth_whenAge_returnHowManyYearsOld() {
        // Given
        LocalDate dob = LocalDate.now().minusYears(30);
        String command = String.format("age -d %s", dob.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));


        // When
        ShellTestClient.InteractiveShellSession session = client
                .interactive()
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("shell"));

        session.write(session.writeSequence().text(command).carriageReturn().build());

        // Then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> ShellAssertions.assertThat(session.screen())
                .containsText("30 years"));

    }



}