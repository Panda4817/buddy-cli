package dev.kmunton.buddy.services;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class RockPaperScissorsGameServiceTest {

    private final static String ROCK = "rock";
    private final static String PAPER = "paper";
    private final static String SCISSORS = "scissors";

    private final RockPaperScissorsGameService rockPaperScissorsGameService = new RockPaperScissorsGameService();

    @Test
    public void whenGetOptions_thenReturnOptionsArray() {
        String[] options = rockPaperScissorsGameService.getOptions();
        assertTrue(Arrays.asList(options).contains(ROCK));
        assertTrue(Arrays.asList(options).contains(PAPER));
        assertTrue(Arrays.asList(options).contains(SCISSORS));
    }

    @Test
    public void whenGetBuddyChoice_theReturnAnOption() {
        String buddyChoice = rockPaperScissorsGameService.getRandomChoice();
        assertTrue(Objects.equals(buddyChoice, ROCK)
                || Objects.equals(buddyChoice, PAPER)
                || Objects.equals(buddyChoice, SCISSORS));
    }

    @Test
    public void givenUserPicksRockAndBuddyPicksPaper_whenCalculateWinner_thenReturnYouWinText() {
        String winnerText = rockPaperScissorsGameService.calculateWinnerBetweenBuddyAndUser(ROCK, PAPER);
        assertTrue(winnerText.contains("You win"));
    }

    @Test
    public void givenUserPicksRockAndBuddyPicksRock_whenCalculateWinner_thenReturnTieText() {
        String winnerText = rockPaperScissorsGameService.calculateWinnerBetweenBuddyAndUser(ROCK, ROCK);
        assertTrue(winnerText.contains("Tie"));
    }

    @Test
    public void givenUserPicksRockAndBuddyPicksScissors_whenCalculateWinner_thenReturnBuddyWinText() {
        String winnerText = rockPaperScissorsGameService.calculateWinnerBetweenBuddyAndUser(ROCK, SCISSORS);
        assertTrue(winnerText.contains("Buddy wins"));
    }

}