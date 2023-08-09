package dev.kmunton.buddy.models.datetime;

public record DateDifference(int years, int months, int days, int hours, int minutes) {

    public String toString(String postfix) {

        StringBuilder resultBuilder = new StringBuilder();

        if (years > 0) {
            resultBuilder.append(years).append(years > 1 ? " years" : " year").append("\n");
        }

        if (months > 0) {
            resultBuilder.append(months).append(months > 1 ? " months": " month").append("\n");
        }

        if (days > 0) {
            resultBuilder.append(days).append(days > 1 ? " days": " day").append("\n");
        }

        if (hours > 0) {
            resultBuilder.append(hours).append(hours > 1 ? " hours": " hour").append("\n");
        }

        if (minutes > 0) {
            resultBuilder.append(minutes).append(minutes > 1 ? " minutes": " minute").append("\n");
        }

        resultBuilder.append(postfix);
        return resultBuilder.toString();
    }
}
