package net.thucydides.junit.sampletests.thucydidestests

import net.thucydides.junit.runners.ThucydidesRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(ThucydidesRunner)
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
