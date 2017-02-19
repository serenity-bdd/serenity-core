package net.thucydides.junit.spring;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.junit.spring.samples.dao.GizmoDao;
import net.thucydides.junit.spring.samples.service.BazingaService;
import net.thucydides.junit.spring.samples.service.GizmoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ContextConfiguration(locations = "/spring/dirty-config.xml")
public class NestedSpringEnabledStepLibrary {

    @Autowired
    public GizmoService gizmoService;

    @Resource
    public GizmoDao gizmoDao;

    @Autowired
    @Qualifier("premiumBazingaService")
    public BazingaService premiumBazingaService;

    @Steps
    public NestedSpringEnabledStepLibrary nestedSpringEnabledStepLibrary;

    @Step
    public void shouldInstanciateGizmoService() {
        assertThat(gizmoService, is(not(nullValue())));
    }

    @Step
    public void shouldInstanciateNestedServices() {
        assertThat(gizmoService.getWidgetService(), is(not(nullValue())));
    }

    @Step
    public void shouldInstanciateServicesUsingTheResourceannotation() {
        assertThat(gizmoDao, is(not(nullValue())));
    }

    @Step
    public void shouldAllowQualifiers() {
        assertThat(premiumBazingaService.getName(), is("Premium Bazingas"));
    }

}
