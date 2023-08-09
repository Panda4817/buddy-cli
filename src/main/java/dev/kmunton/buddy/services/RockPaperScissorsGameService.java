package dev.kmunton.buddy.services;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RockPaperScissorsGameService {

    private final String[] options = new String[]{"rock", "paper", "scissors"};

    public String getRandomChoice() {
        return options[new Random().nextInt(options.length)];
    }

    public String calculateWinnerBetweenBuddyAndUser(String buddyChoice, String userChoice) {
        if (buddyChoice.equals(userChoice)) {
            return "Tie: We both win :)";
        }

        return switch (buddyChoice) {
            case "rock" -> {
                if (userChoice.equals("paper")) {
                    yield "Paper beats rock: You win";
                }  else {
                    yield "Rock beats scissors: Buddy wins";
                }
            }
            case "paper" -> {
                if (userChoice.equals("scissors")) {
                    yield "Scissors beats paper: You win";
                }  else {
                    yield "Paper beats rock: Buddy wins";
                }
            }
            case "scissors" -> {
                if (userChoice.equals("rock")) {
                    yield "Rock beats scissors: You win";
                }  else {
                    yield "Scissors beats paper: Buddy wins";
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + buddyChoice);
        };
    }

    public String[] getOptions() {
        return options;
    }
}
