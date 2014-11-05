package net.thucydides.core.requirements.model;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Narrative {
    private final Optional<String> title;
    private final Optional<String> cardNumber;
    private final List<String> versionNumbers;
    private final String text;
    private String type;

    public Narrative(Optional<String> title, Optional<String> cardNumber, List<String> versionNumbers, String type, String text) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(text);
        this.title = title;
        this.cardNumber = cardNumber;
        this.versionNumbers = versionNumbers;
        this.type = type;
        this.text = text;
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
}
