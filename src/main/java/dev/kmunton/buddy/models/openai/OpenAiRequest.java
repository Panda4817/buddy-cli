package dev.kmunton.buddy.models.openai;

import java.util.List;

public record OpenAiRequest(String model, List<OpenAiMessage> messages) {
}
