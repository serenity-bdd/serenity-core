package net.thucydides.junit.spring;

import net.serenitybdd.annotations.Step;
import net.thucydides.junit.spring.samples.dao.GizmoDao;
import net.thucydides.junit.spring.samples.service.BazingaService;
import net.thucydides.junit.spring.samples.service.GizmoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(classes=SpringBootTestAnnotatedStepLibrary.Config.class)
public class SpringBootTestAnnotatedStepLibrary {
	@ImportResource("/spring/dirty-config.xml")
	public static class  Config{
	}

    @Autowired
    public GizmoService gizmoService;

    @Resource
    public GizmoDao gizmoDao;

    @Autowired
    @Qualifier("premiumBazingaService")
    public BazingaService premiumBazingaService;

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
