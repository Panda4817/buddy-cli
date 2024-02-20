package dev.kmunton.buddy.models.datetime;

public record DateDifference(int years, int months, int days, int hours, int minutes) {

    public String toString(String postfix) {

        StringBuilder resultBuilder = new StringBuilder();

        if (hours < 0 || minutes < 0) {
            resultBuilder.append("Just under").append("\n");
        }

        if (years > 0) {
            resultBuilder.append(years)
                .append(checkPlurality(years, " year", " years")).append("\n");
        }

        if (months > 0) {
            resultBuilder.append(months)
                .append(checkPlurality(months, " month", " months")).append("\n");
        }

        if (days > 0) {
            resultBuilder.append(days)
                .append(checkPlurality(days, " day", " days")).append("\n");
        }

        if (hours > 0) {
            resultBuilder.append(hours)
                .append(checkPlurality(hours, " hour", " hours")).append("\n");
        }

        if (minutes > 0) {
            resultBuilder.append(minutes)
                .append(checkPlurality(minutes, " minute", " minutes")).append("\n");
        }

        resultBuilder.append(postfix);
        return resultBuilder.toString();
    }

    private String checkPlurality(int amount, String forOne, String forMany) {
        if (amount > 1) {
            return forMany;
        }

        return forOne;
    }
}
