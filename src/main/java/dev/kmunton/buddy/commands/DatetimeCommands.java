package dev.kmunton.buddy.commands;

import dev.kmunton.buddy.models.datetime.DateDifference;
import java.time.Duration;
import java.time.Period;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
@Command(group = "Date / Time Commands")
public class DatetimeCommands {

    private static final String TODAY = "today";

    @Command(command = "now", description = "I will tell you today's date and time")
    public String getCurrentDatetime() {
        return formatDate(LocalDateTime.now());
    }

    @Command(command="countdown", description = "I will tell you how many years, months, days, hours and minutes left until given date and/or time")
    public String getCountdownToDatetime(@Option(defaultValue = TODAY, longNames = {"year"}, shortNames = {'y'}, description = "YYYY format e.g. 2055") String year,
                                         @Option(defaultValue = TODAY, longNames = {"month"}, shortNames = {'m'}, description = "e.g 6 for June") String month,
                                         @Option(defaultValue = TODAY, longNames = {"day"}, shortNames = {'d'}, description = "e.g. 5 for 5th of the month") String day,
                                         @Option(defaultValue = "00", longNames = {"hour"}, shortNames = {'r'}, description = "24hr format e.g. 20 for 8pm") String hour,
                                         @Option(defaultValue = "00", longNames = {"minute"}, shortNames = {'i'}, description = "e.g. 15 for fifteen past the hour") String minute) {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        int goalYear = year.equals(TODAY) ? now.getYear() : Integer.parseInt(year);
        int goalMonth = month.equals(TODAY) ? now.getMonthValue() : Integer.parseInt(month);
        int goalDay = day.equals(TODAY) ? now.getDayOfMonth() : Integer.parseInt(day);
        int goalHour = Integer.parseInt(hour);
        int goalMinute = Integer.parseInt(minute);

        LocalDateTime goal = LocalDateTime.of(goalYear, goalMonth, goalDay, goalHour, goalMinute, 0, 0);

        if (goal.isBefore(now) || goal.isEqual(now)) {
            return "Countdown complete. Provide a date and/or time in the future.";
        }


        DateDifference diff = calculateDateDifference(now, goal);

        return diff.toString("to go");
    }

    @Command(command="add", description = "I will tell you the date and time in the future after adding the number of years, months, days, hours and minutes given")
    public String addToDatetime(@Option(defaultValue = "0", longNames = {"years"}, shortNames = {'y'}, description = "number of years") int years,
                                @Option(defaultValue = "0", longNames = {"months"}, shortNames = {'m'}, description = "number of months") int months,
                                @Option(defaultValue = "0", longNames = {"days"}, shortNames = {'d'}, description = "number of days") int days,
                                @Option(defaultValue = "0", longNames = {"hours"}, shortNames = {'r'}, description = "number of hours") int hours,
                                @Option(defaultValue = "0", longNames = {"minutes"}, shortNames = {'i'}, description = "number of minutes") int minutes) {

        return formatDate(LocalDateTime.now()
                .plusYears(years)
                .plusMonths(months)
                .plusDays(days)
                .plusHours(hours)
                .plusMinutes(minutes)
        );
    }

    @Command(command="age", description = "I will tell you the age based on the given date of birth")
    public String getAge(@Option(longNames = {"dob"}, shortNames = {'d'}, description = "YYYY-MM-DD", required = true) String dob) {
        LocalDateTime dateOfBirth = LocalDate.parse(dob).atStartOfDay();
        LocalDateTime now = LocalDate.now().atStartOfDay();

        DateDifference diff = calculateDateDifference(dateOfBirth, now);

        return diff.toString("old");
    }


    private String formatDate(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("E dd MMMM yyyy HH:mm:ss"));
    }

    private DateDifference calculateDateDifference(LocalDateTime current, LocalDateTime goal) {
        var period = Period.between(current.toLocalDate(), goal.toLocalDate());
        var duration = Duration.between(goal.withHour(current.getHour()).withMinute(current.getMinute()), goal);
        int diffYears = period.getYears();
        int diffMonths = period.getMonths();
        int diffDays = period.getDays();
        int diffHours = duration.toHoursPart();
        int diffMinutes = duration.toMinutesPart();

        return new DateDifference(diffYears, diffMonths, diffDays, diffHours, diffMinutes);
    }
}
