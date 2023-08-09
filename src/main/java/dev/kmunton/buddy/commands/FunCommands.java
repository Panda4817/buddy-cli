package dev.kmunton.buddy.commands;

import dev.kmunton.buddy.clients.DadJokeClient;
import dev.kmunton.buddy.models.dadjoke.DadJokeResponse;
import dev.kmunton.buddy.models.dadjoke.DadJokesList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.table.ArrayTableModel;

import static dev.kmunton.buddy.styling.TableUtils.renderLightTable;
import static org.springframework.util.StringUtils.hasText;

@Command(group = "Fun Commands")
public class FunCommands {

    @Autowired
    private DadJokeClient dadJokeClient;

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
}
