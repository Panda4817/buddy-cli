package dev.kmunton.buddy.models.stackoverflow;

import java.util.List;

public record StackOverflowResponse(List<StackOverflowItem> items) {
}
