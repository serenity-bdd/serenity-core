package net.thucydides.core.requirements.model;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Narrative {
    private final Optional<String> title;
    private final Optional<String> id;
    private final Optional<String> cardNumber;
    private final List<String> versionNumbers;
    private final String text;
    private String type;

    public Narrative(Optional<String> title, Optional<String> id, Optional<String> cardNumber, List<String> versionNumbers, String type, String text) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(text);
        this.title = title;
        this.id = id;
        this.cardNumber = cardNumber;
        this.versionNumbers = versionNumbers;
        this.type = type;
        this.text = text;
    }

    public Narrative(String type, String text) {
        this(Optional.<String>absent(), Optional.<String>absent(), Optional.<String>absent(), ImmutableList.<String>of(), type, text);
    }

    public Optional<String> getId() {
        return id;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public Optional<String> getCardNumber() {
        return cardNumber;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public List<String> getVersionNumbers() {
        return ImmutableList.copyOf(versionNumbers);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("title", title)
                .append("text", text)
                .append("type", type)
                .toString();
    }
}
