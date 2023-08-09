package dev.kmunton.buddy.commands;

import dev.kmunton.buddy.clients.DadJokeClient;
import dev.kmunton.buddy.clients.XkcdClient;
import dev.kmunton.buddy.models.dadjoke.DadJokeResponse;
import dev.kmunton.buddy.models.dadjoke.DadJokesList;
import dev.kmunton.buddy.models.xkcd.XkcdComicResponse;
import dev.kmunton.buddy.services.RockPaperScissorsGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.table.ArrayTableModel;

import java.util.*;

import static dev.kmunton.buddy.styling.TableUtils.renderLightTable;
import static org.springframework.util.StringUtils.hasText;

@Command(group = "Fun Commands")
public class FunCommands extends AbstractShellComponent {

    @Autowired
    private DadJokeClient dadJokeClient;

    @Autowired
    private XkcdClient xkcdClient;

    @Autowired
    private RockPaperScissorsGameService rockPaperScissorsGameService;

    @Command(command = "dadjoke", description = "I will tell you a random dad joke or give you a list of jokes bases on search term")
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
        return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).orElseThrow();
    }
}
