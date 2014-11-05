package net.thucydides.core.steps;

import org.junit.Test;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * A description goes here.
 * User: johnsmart
 * Date: 7/02/12
 * Time: 2:37 PM
 */
public class WhenFindingSensibleDefaultReturnValues {

    @Test
    public void a_string_return_type_should_return_an_empty_string() {
        assertThat((String) DefaultValue.forClass(String.class), is(""));
    }

    @Test
    public void an_integer_return_type_should_return_0() {
        assertThat((Integer) DefaultValue.forClass(Integer.class), is(0));
    }

    @Test
    public void a_long_return_type_should_return_0() {
        assertThat((Long) DefaultValue.forClass(Long.class), is(0L));
    }

    @Test
    public void a_double_return_type_should_return_0() {
        assertThat((Double) DefaultValue.forClass(Double.class), is(0.0));
    }

    @Test
    public void a_list_return_type_should_return_an_empty_list() {
        assertThat(((Collection) DefaultValue.forClass(List.class)).size(), is(0));
    }

    @Test
    public void a_set_return_type_should_return_an_empty_set() {
        assertThat(((Set) DefaultValue.forClass(Set.class)).size(), is(0));
    }

    @Test
    public void an_unknown_type_should_return_null() {
        assertThat(DefaultValue.forClass(Color.class), is(nullValue()));
    }
}
