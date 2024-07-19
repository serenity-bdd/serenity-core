package net.thucydides.core.model;

import net.thucydides.model.domain.LastElement;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestLastElement {

    @Test
    public void testLastElementOfAFeatureOrStoryFile(){
        LastElement.LastElementOfAFeatureOrStoryFile lastElement = new LastElement.LastElementOfAFeatureOrStoryFile();
        assertThat(lastElement.lastElementIn("package1/package2/myFeature.feature"), is("package2"));
        assertThat(lastElement.lastElementIn("package1/package2/myStory.story"), is("package2"));
        assertThat(lastElement.lastElementIn("package1/package2/myFeature.withDot.feature"), is("package2"));
    }

    @Test
    public void testLastElementOfAFeatureOrStoryFileWithDot(){
        LastElement.LastElementOfAFeatureOrStoryFile lastElement = new LastElement.LastElementOfAFeatureOrStoryFile();
        assertThat(lastElement.lastElementIn("package1/package2/myFeature.withDot.feature"), is("package2"));
    }

    @Test
    public void testLastElementOfATestCase(){
        LastElement.LastElementOfATestCase lastElement = new LastElement.LastElementOfATestCase();
        assertThat(lastElement.lastElementIn("package1.package2"), is("package2"));
    }

}
