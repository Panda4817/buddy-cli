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
    void givenFutureYearAndAllParamsProvided_whenCountdown_returnCountdownString() {
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
    void givenFutureMonthAndSomeParamsProvided_whenCountdown_returnCountdownString() {
        // Given
        LocalDateTime goal = LocalDateTime.now().plusMonths(3).plusHours(1).plusMinutes(50).plusDays(1);
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
                .containsText("3 months")
                .containsText("1 day")
                .containsText("1 hour")
                .containsText("50 minutes"));

    }

    @Test
    void givenSameDateAndFutureTime_whenCountdown_returnCountdownString() {
        // Given
        LocalDateTime goal = LocalDateTime.now().plusHours(2).plusMinutes(30);
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
                .containsText("2 hours")
                .containsText("30 minutes"));

    }



    @Test
    void givenDateParamsProvided_whenAdd_returnFutureDate() {
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
    void givenOnlyMonthsProvided_whenAdd_returnFutureDate() {
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
    void givenDateOfBirth_whenAge_returnHowManyYearsOld() {
        // Given
        LocalDate dob = LocalDate.now().minusYears(31);
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
                .containsText("31 years"));

    }

    @Test
    void givenDateOfBirthWithDifferentMonthAndDay_whenAge_returnHowManyYearsMonthsDaysOld() {
        // Given
        LocalDate dob = LocalDate.now()
            .minusYears(31).minusMonths(2).minusDays(1);
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
                .containsText("31 years")
                .containsText("2 months")
                .containsText("1 day"));

    }
}
