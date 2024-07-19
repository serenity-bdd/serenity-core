package net.thucydides.model.domain.flags;

import net.serenitybdd.model.collect.NewMap;
import net.thucydides.model.domain.TestOutcome;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlagCounts {
    private final List<? extends TestOutcome> testOutcomes;
    private final  Map<Flag, Integer> flagMap = new HashMap();

    public FlagCounts(List<? extends TestOutcome> testOutcomes) {
        this.testOutcomes = testOutcomes;
    }

    public static FlagCounts in(List<? extends TestOutcome> testOutcomes) {
        return new FlagCounts(testOutcomes);
    }

    public Map<Flag, Integer> asAMap() {
        for(TestOutcome testOutcome : testOutcomes) {
            addToMap(testOutcome.getFlags());
        }
        return NewMap.copyOf(flagMap);
    }

    private void addToMap(Set<? extends Flag> flags) {
        for(Flag flag : flags) {
            if (flagMap.containsKey(flag)) {
                flagMap.put(flag, flagMap.get(flag) + 1);
            } else {
                flagMap.put(flag, 1);
            }
        }
    }
}
