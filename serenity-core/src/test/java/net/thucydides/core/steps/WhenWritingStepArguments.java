package net.thucydides.core.steps;

import net.thucydides.model.steps.StepArgumentWriter;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenWritingStepArguments {
    
    @Test
    public void shouldPrintArraysAsACommaSeparatedList() {
        String[] colors = new String[] {"red","green","yellow"};
        assertThat(StepArgumentWriter.readableFormOf(colors), is("{red,green,yellow}"));
    }

    @Test
    public void shouldPrintObjectsUsingToString() {
        String color = "red";
        assertThat(StepArgumentWriter.readableFormOf(color), is("red"));
    }

    @Test
    public void shouldWorkWithNullParameters() {
        String color = null;
        assertThat(StepArgumentWriter.readableFormOf(color), is("<null>"));
    }
}
