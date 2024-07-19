package net.thucydides.model.domain;

import java.util.ArrayList;
import java.util.List;

public class CastMember {
    /**
     * The name of the actor
     */
    private final String name;
    /**
     * An optional description of the actor
     */
    private String description;

    /**
     * The actor's abilities
     */
    private final List<String> can;
    /**
     * The actor's facts
     */
    private final List<String> has;

    public CastMember(String name) {
        this(name, new ArrayList<>(), new ArrayList<>());
    }

    public CastMember(String name, List<String> can, List<String> has) {
        this.name = name;
        this.can = can;
        this.has = has;
    }

    public CastMember addAbility(String ability) {
        if (!can.contains(ability)) {
            can.add(ability);
        }
        return this;
    }

    public CastMember addFact(String fact) {
        if (!has.contains(fact)) {
            has.add(fact);
        }
        return this;
    }

    public CastMember withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getName() {
        return name;
    }

    public List<String> getCan() {
        return can;
    }

    public Boolean hasFacts() { return (has != null && !has.isEmpty()); }
    public Boolean hasAbilities() { return (can != null && !can.isEmpty()); }

    public List<String> getHas() {
        return has;
    }

    public String getDescription() {
        return description;
    }
}
