package net.thucydides.model.requirements;

import net.thucydides.model.requirements.model.Requirement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class RequirementsCollector {

    private static final Collector<Requirement, ?, List<Requirement>> INSTANCE =
            Collectors.collectingAndThen(
                    Collectors.toMap(
                            req -> req, // Using the requirement itself as the key since we have no unique ID to rely on
                            req -> req,
                            Requirement::merge
                    ),
                    map -> new ArrayList<>(map.values())
            );

    public static Collector<Requirement, ?, List<Requirement>> merging() {
        return INSTANCE;
    }
}
