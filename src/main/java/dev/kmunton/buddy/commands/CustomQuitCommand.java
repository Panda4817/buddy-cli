package dev.kmunton.buddy.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.commands.Quit;
import org.springframework.stereotype.Component;

@Component
@Command
public class CustomQuitCommand implements Quit.Command{

    // Gracefully shutdown Spring application
    // Default quit command only exits the shell rather than the whole application
    private final ApplicationContext context;

    @Autowired
    public CustomQuitCommand(ApplicationContext context) {
        this.context = context;
    }

    @Command(command = "quit", interactionMode = InteractionMode.INTERACTIVE, description = "Exit the shell", alias = "exit")
    public void quit() {
        SpringApplication.exit(context);
        System.out.println("Goodbye :)");
        throw new ExitRequest();
    }
}
