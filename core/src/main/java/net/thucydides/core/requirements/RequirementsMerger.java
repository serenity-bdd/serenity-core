package net.thucydides.core.requirements;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.requirements.model.CustomFieldValue;
import net.thucydides.core.requirements.model.Example;
import net.thucydides.core.requirements.model.Requirement;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by john on 16/04/2015.
 */
public class RequirementsMerger {
    public List<Requirement> merge(Iterable<Requirement> baseRequirements, Iterable<Requirement> newRequirements) {
        List<Requirement> mergedRequirements = Lists.newArrayList(baseRequirements);

        for(Requirement newRequirement : newRequirements) {
            mergeNewRequirement(newRequirement, mergedRequirements);
        }

        return ImmutableList.copyOf(mergedRequirements);
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
