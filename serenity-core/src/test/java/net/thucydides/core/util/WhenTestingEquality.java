package net.thucydides.core.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenTestingEquality {

    @Test
    public void identicalBooleansAreEqual() {
        assertThat(EqualsUtils.areEqual(true,true), is(true));
    }

    @Test
    public void nonIdenticalBooleansAreNotEqual() {
        assertThat(EqualsUtils.areEqual(true,false), is(false));
        assertThat(EqualsUtils.areEqual(false,true), is(false));
    }
    
    @Test
    public void identicalNumbersAreEqual() {
        assertThat(EqualsUtils.areEqual(1,1), is(true));
    }

    @Test
    public void nonIdenticalNumbersAreNotEqual() {
        assertThat(EqualsUtils.areEqual(1,2), is(false));
        assertThat(EqualsUtils.areEqual(2,1), is(false));
    }


    @Test
    public void identicalFloatsAreEqual() {
        assertThat(EqualsUtils.areEqual(1.0F,1.0F), is(true));
    }

    @Test
    public void nonIdenticalFloatsAreNotEqual() {
        assertThat(EqualsUtils.areEqual(1.0F,2.0F), is(false));
        assertThat(EqualsUtils.areEqual(2.0F,1.0F), is(false));
    }

    @Test
    public void identicalCharsAreEqual() {
        assertThat(EqualsUtils.areEqual('a','a'), is(true));
    }

    @Test
    public void nonIdenticalCharsAreNotEqual() {
        assertThat(EqualsUtils.areEqual('a','b'), is(false));
        assertThat(EqualsUtils.areEqual('b','a'), is(false));
    }


    @Test
    public void identicalDoublesAreEqual() {
        assertThat(EqualsUtils.areEqual(1.0D,1.0D), is(true));
    }

    @Test
    public void nonIdenticalDoublesAreNotEqual() {
        assertThat(EqualsUtils.areEqual(1.0D,2.0D), is(false));
        assertThat(EqualsUtils.areEqual(2.0D,1.0D), is(false));
    }


    @Test
    public void identicalStringsAreEqual() {
        assertThat(EqualsUtils.areEqual("a","a"), is(true));
    }

    @Test
    public void nonIdenticalStringsAreNotEqual() {
        assertThat(EqualsUtils.areEqual("a","b"), is(false));
        assertThat(EqualsUtils.areEqual("b","a"), is(false));
    }
    
    @Test
    public void nullsAreEqual() {
        assertThat(EqualsUtils.areEqual(null,null), is(true));
    }
    
    @Test
    public void nullIsNotEqualToNonNull() {
        assertThat(EqualsUtils.areEqual("a",null), is(false));
        assertThat(EqualsUtils.areEqual(null,"a"), is(false));
    }
}
