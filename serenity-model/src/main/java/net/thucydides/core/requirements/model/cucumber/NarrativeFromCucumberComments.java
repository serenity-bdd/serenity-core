package net.thucydides.core.requirements.model.cucumber;


import io.cucumber.messages.types.Comment;

import java.util.List;
import java.util.stream.Collectors;

public class NarrativeFromCucumberComments {
    public static String in(List<Comment> comments) {
        return comments.stream().map(Comment::getText).collect(Collectors.joining(System.lineSeparator()));
//        return "";
    }
}
