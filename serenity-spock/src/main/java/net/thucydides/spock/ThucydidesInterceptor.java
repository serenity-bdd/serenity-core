package net.thucydides.spock;

import net.thucydides.core.bootstrap.ThucydidesAgent;
import org.spockframework.runtime.extension.AbstractMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import spock.lang.Specification;

public class ThucydidesInterceptor extends AbstractMethodInterceptor {
    private final ThucydidesAgent agent;

    public ThucydidesInterceptor(final ThucydidesAgent agent) {
        this.agent = agent;
    }

    @Override
    public void interceptInitializerMethod(IMethodInvocation invocation) throws Throwable {
        agent.enrich(specificationFrom(invocation));
        invocation.proceed();
    }

    Specification specificationFrom(IMethodInvocation invocation) {
        if (invocation.getInstance() != null) {
            return (Specification) invocation.getInstance();
        } else {
            return (Specification) invocation.getSharedInstance();
        }
    }

    /////////////////////////////

    @Override
    public void interceptFeatureExecution(IMethodInvocation invocation) throws Throwable {
        invocation.proceed();
    }

    @Override
    public void interceptSpecExecution(IMethodInvocation invocation) throws Throwable {
        invocation.proceed();
    }

    @Override
    public void interceptFeatureMethod(IMethodInvocation invocation) throws Throwable {
        invocation.proceed();
    }

    @Override
    public void interceptSetupSpecMethod(IMethodInvocation invocation) throws Throwable {
        invocation.proceed();
    }

    @Override
    public void interceptSetupMethod(IMethodInvocation invocation) throws Throwable {
        invocation.proceed();
    }

    @Override
    public void interceptSharedInitializerMethod(IMethodInvocation invocation) throws Throwable {
        invocation.proceed();
    }

}
