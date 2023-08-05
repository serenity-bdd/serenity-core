package net.thucydides.core.pages;


import net.serenitybdd.core.pages.PagesAnnotatedField;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.model.reflection.FieldSetter;
import net.thucydides.core.steps.InvalidManagedPagesFieldException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.doThrow;

public class WhenUsingThePagesAnnotation {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void the_ManagedPages_annotation_should_identify_the_pages_field() {
        SimpleScenario testCase = new SimpleScenario();
        PagesAnnotatedField pagesField = PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass()).get();
        assertThat(pagesField, is(not(nullValue())));
    }

    @Test
    public void the_ManagedPages_annotation_should_define_the_default_url() {
        SimpleScenario testCase = new SimpleScenario();
        PagesAnnotatedField pagesField = PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass()).get();
        assertThat(pagesField.getDefaultBaseUrl(), is("http://www.google.com"));
    }


    @Test
    public void should_be_able_to_inject_a_Pages_object_into_a_test_case() {
        SimpleScenario testCase = new SimpleScenario();
        Pages pages = new Pages();
        PagesAnnotatedField pagesField = PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass()).get();

        pagesField.setValue(testCase, pages);
        assertThat(testCase.getPages(), is(pages));
    }

    class SimpleUnannotatedScenario {
        public Pages pages;
    }

    class SimpleBadlyannotatedScenario {
        @ManagedPages(defaultUrl = "http://www.google.com")
        public String pages;
    }

    @Test(expected = InvalidManagedPagesFieldException.class)
    public void should_throw_exception_if_pages_object_is_not_a_Pages_instance() {
        SimpleBadlyannotatedScenario testCase = new SimpleBadlyannotatedScenario();
        PagesAnnotatedField.findFirstAnnotatedField(testCase.getClass());
    }

    @Mock Pages pages;
    @Mock Object testCase;
    @Mock ManagedPages managedPages;
    @Mock FieldSetter fieldSetter;

    class TestPagesAnnotatedField extends PagesAnnotatedField {

        TestPagesAnnotatedField(Field field, ManagedPages annotation) {
            super(field, annotation);
        }

        @Override
        protected FieldSetter set(Object targetObject) {
            return fieldSetter;
        }
    }

    @Test(expected = InvalidManagedPagesFieldException.class)
    public void should_throw_exception_if_pages_object_field_cannot_be_accessed() throws Exception {

        doThrow(new IllegalAccessException()).when(fieldSetter).to(anyObject());

        Field field = null;
        TestPagesAnnotatedField testField = new TestPagesAnnotatedField(field, managedPages);
        testField.setValue(testCase, pages);
    }


}
