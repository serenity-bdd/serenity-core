package serenitymodel.net.thucydides.core.matchers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import serenitymodel.net.thucydides.core.matchers.dates.BeanFields;

public class BeanUniquenessMatcher implements BeanCollectionMatcher {

    private final String fieldName;
    
    public BeanUniquenessMatcher(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public boolean matches(Object target) {
        return matches((Collection) target);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> boolean matches(Collection<T> elements) {
        List<Object> allFieldValues = elements.stream()
                .map(element -> BeanFields.fieldValueIn(element).forField(fieldName))
                .collect(Collectors.toList());

        Set<Object> uniquefieldValues = new HashSet<Object>();

        uniquefieldValues.addAll(allFieldValues);

        return (uniquefieldValues.size() == elements.size());
    }

    @Override
    public String toString() {
        return "each " + fieldName + " is different";
    }

}
