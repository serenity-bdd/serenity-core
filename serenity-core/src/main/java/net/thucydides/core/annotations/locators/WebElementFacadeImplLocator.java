package net.thucydides.core.annotations.locators;

import net.serenitybdd.core.annotations.ImplementedBy;
import net.thucydides.core.annotations.NotImplementedException;

public class WebElementFacadeImplLocator {

	/**
	 * Refactored from {@link SmartElementHandler}.
	 * @param interfaceType
	 */
    public Class<?> getImplementer(Class<?> interfaceType) {
    	if (!interfaceType.isInterface()){
		throw new NotImplementedException(interfaceType.getSimpleName() +
    				" is not an interface");
    	}
		Class<?> implementerClass = null;
    	ImplementedBy implBy = interfaceType.getAnnotation(ImplementedBy.class);
    	if (implBy == null){
			// todo Remove when thucydides ImplementedBy is finally removed
            net.thucydides.core.annotations.ImplementedBy implByDep = interfaceType.getAnnotation(net.thucydides.core.annotations.ImplementedBy.class);
			if(implByDep == null) {
				throw new NotImplementedException(interfaceType.getSimpleName() +
						" is not implemented by any class (or not annotated by @ImplementedBy)");
			} else {
				implementerClass = implByDep.value();
			}
	} else {
			implementerClass = implBy.value();
		}
		if (!interfaceType.isAssignableFrom(implementerClass)) {
			throw new NotImplementedException(String.format("implementer Class '%s' does not implement the interface '%s'", implementerClass, interfaceType.getName()));
		}
		return implementerClass;
	}
}
