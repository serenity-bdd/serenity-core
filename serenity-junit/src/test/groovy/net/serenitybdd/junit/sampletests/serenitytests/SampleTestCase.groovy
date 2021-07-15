package net.serenitybdd.junit.sampletests.serenitytests

import org.junit.Test
import org.junit.runner.RunWith
import net.serenitybdd.junit.runners.SerenityRunner

@RunWith(SerenityRunner)
class SampleTestCase {

    @Test
    void testSomethingElse(){
       println("testSomethingElse")
    }

    @Test
    void testSomething(){
        println("testSomething")
        assert 1 == 2

    }

}
