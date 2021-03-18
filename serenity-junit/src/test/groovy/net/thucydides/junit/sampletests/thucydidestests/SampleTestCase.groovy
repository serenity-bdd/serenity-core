package net.thucydides.junit.sampletests.thucydidestests

import org.junit.Test
import org.junit.runner.RunWith
import net.serenitybdd.junit.runners.SerenityRunner

@RunWith(SerenityRunner)
class SampleTestCase {

    @Test
    public void testSomethingElse(){
       println("testSomethingElse")
    }

    @Test
    public void testSomething(){
        println("testSomething")
        assert 1 == 2

    }

}
