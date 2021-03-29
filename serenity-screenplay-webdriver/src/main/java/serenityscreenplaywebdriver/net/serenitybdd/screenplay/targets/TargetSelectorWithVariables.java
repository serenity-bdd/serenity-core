package serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets;

public class TargetSelectorWithVariables {
    private final String cssOrXPathSelector;

    public TargetSelectorWithVariables(String cssOrXPathSelector) {
        this.cssOrXPathSelector = cssOrXPathSelector;
    }

    public String resolvedWith(String[] parameters) {

        String instantiatedSelector = cssOrXPathSelector;

        int variableIndex = 0;
        for(String parameter : parameters) {
            String variablePlaceholder = "\\{" + variableIndex++ + "\\}";
            instantiatedSelector = instantiatedSelector.replaceAll(variablePlaceholder, parameter);
        }
        return instantiatedSelector;
    }
}
