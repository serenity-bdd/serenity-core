package net.thucydides.core.requirements;

import net.thucydides.core.requirements.model.Requirement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 16/04/2015.
 */
public class RequirementsMerger {
    public List<Requirement> merge(Iterable<Requirement> baseRequirements, Iterable<Requirement> newRequirements) {
        List<Requirement> mergedRequirements = new ArrayList<>();
        baseRequirements.forEach(
                req -> mergedRequirements.add(req)
        );

        for(Requirement newRequirement : newRequirements) {
            mergeNewRequirement(newRequirement, mergedRequirements);
        }

        return new ArrayList<>(mergedRequirements);
    }

    private void mergeNewRequirement(Requirement newRequirement, List<Requirement> existingRequirements) {
        if (!existingRequirements.contains(newRequirement)) {
            existingRequirements.add(newRequirement);
        } else {
            Requirement existing = existingRequirements.remove(existingRequirements.indexOf(newRequirement));
            existingRequirements.add(existing.merge(newRequirement));
        }
    }

}
