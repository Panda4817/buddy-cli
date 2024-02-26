package dev.kmunton.buddy.commands;

import dev.kmunton.buddy.clients.DadJokeClient;
import dev.kmunton.buddy.clients.XkcdClient;
import dev.kmunton.buddy.models.dadjoke.DadJokeResponse;
import dev.kmunton.buddy.models.dadjoke.DadJokesList;
import dev.kmunton.buddy.models.xkcd.XkcdComicResponse;
import dev.kmunton.buddy.services.RockPaperScissorsGameService;
import org.jline.utils.InfoCmp.Capability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.StringInput.StringInputContext;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.table.ArrayTableModel;

import java.util.*;
import org.springframework.stereotype.Component;

import static dev.kmunton.buddy.styling.TableUtils.renderLightTable;
import static org.springframework.util.StringUtils.hasText;

@Component
@Command(group = "Fun Commands")
public class FunCommands extends AbstractShellComponent {

    private final DadJokeClient dadJokeClient;

    private final XkcdClient xkcdClient;

    private final RockPaperScissorsGameService rockPaperScissorsGameService;

    @Autowired
    public FunCommands(DadJokeClient dadJokeClient, XkcdClient xkcdClient,
                       RockPaperScissorsGameService rockPaperScissorsGameService) {
        this.dadJokeClient = dadJokeClient;
        this.xkcdClient = xkcdClient;
        this.rockPaperScissorsGameService = rockPaperScissorsGameService;
    }

    @Command(command = "dadjoke", description = "I will tell you a random dad joke or give you a list of jokes based on a search term")
    public String getDadJoke(@Option(longNames = {"search"}, shortNames = {'s'}, description = "Search term to get a list of maximum 30 jokes") String term) {
        if (!hasText(term)) {
            DadJokeResponse response = dadJokeClient.random();
            return response.joke();
        }

        DadJokesList response = dadJokeClient.search(term, 30);
        if (response.results().isEmpty()) {
            return "No jokes found :(";
        }
        ArrayTableModel model = new ArrayTableModel(response.results().stream().map(joke -> new String[]{joke.joke()}).toArray(String[][]::new));
        return renderLightTable(model);
    }

    @Command(command = "comic", description = "I will show you today's XKCD comic")
    public String getXkcdComic() {
        XkcdComicResponse response = xkcdClient.getCurrentComic();
        ArrayTableModel model = new ArrayTableModel(new String[][]{
                new String[]{response.title()},
                new String[]{response.img()},
                new String[]{response.alt()}
        });
        return renderLightTable(model);
    }

    @Command(command = "rps", description = "Play rock paper scissors")
    public String playRockPaperScissors() {
        String[] options = rockPaperScissorsGameService.getOptions();
        String buddyChoice = rockPaperScissorsGameService.getRandomChoice();

        List<SelectorItem<String>> items = getSelectorStringItems(options);
        SingleItemSelector<String, SelectorItem<String>> component = getSingleStringItemSelectorComponent(items);
        String userChoice = getSingleUserChoice(component);

        return rockPaperScissorsGameService.calculateWinnerBetweenBuddyAndUser(buddyChoice, userChoice);

    }

    @Command(command = "guess", description = "Play guess the number game")
    public String playGuessTheNumber() {
        int buddyNumber = new Random().nextInt(1, 100);
        System.out.println("I have chosen a number. You need to guess it.");

        var inputComponent = getStringInputComponent();

        int chosenNumber = 0;
        int guesses = 0;

        while (chosenNumber != buddyNumber) {
            StringInputContext context = inputComponent.run(StringInputContext.empty());
            try {
                chosenNumber = Integer.parseInt(context.getResultValue());
                if (chosenNumber > buddyNumber) {
                    System.out.println("Lower");
                } else if (chosenNumber < buddyNumber) {
                    System.out.println("Higher");
                }
                guesses++;
            } catch (Exception e) {
                System.out.println("Enter a number between 0 and 100");
            }

        }

        getTerminal().puts(Capability.cursor_normal);
        if (guesses == 1) {
            return "1 guess to the correct number";
        }
        return "%s guesses to the correct number".formatted(guesses);
    }

    private StringInput getStringInputComponent() {
        StringInput component = new StringInput(getTerminal(), "Enter your guess", "50");
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        return component;
    }

    private List<SelectorItem<String>> getSelectorStringItems(String[] options) {
        List<SelectorItem<String>> items = new ArrayList<>();
        for (String option : options) {
            items.add(SelectorItem.of(option, option));
        }
        return items;
    }

    private SingleItemSelector<String, SelectorItem<String>> getSingleStringItemSelectorComponent(List<SelectorItem<String>> items) {
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                items, "your choice", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        return component;
    }

    private String getSingleUserChoice(SingleItemSelector<String, SelectorItem<String>> component) {
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());
        getTerminal().puts(Capability.cursor_normal);
        return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).orElseThrow();
    }
}
