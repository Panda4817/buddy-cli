package dev.kmunton.buddy.styling;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class BuddyPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("my-buddy:>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }
}
