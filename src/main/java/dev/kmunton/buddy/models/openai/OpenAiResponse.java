package dev.kmunton.buddy.models.openai;

import java.util.List;

public record OpenAiResponse(List<OpenAiChoice> choices) {
}
