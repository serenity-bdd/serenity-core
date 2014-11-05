package net.thucydides.core.csv;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by IntelliJ IDEA.
 * User: johnsmart
 * Date: 3/06/11
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class WhenCreatingNewDataDrivenTestSteps {

    @Test
    public void should_create_new_instances_using_default_constructor() throws Exception {
        Object[] args = {"a","b"};

        Person person = InstanceBuilder.newInstanceOf(Person.class);

        assertThat(person, is(not(nullValue())));
    }

    @Test
    public void should_create_new_instances_using_arguments() throws Exception {
        Object[] args = {"a","b", "c", "d"};

        Person person = InstanceBuilder.newInstanceOf(Person.class, args);

        assertThat(person.getName(), is("a"));
        assertThat(person.getAddress(), is("b"));
        assertThat(person.getPhone(), is("c"));
        assertThat(person.getDateOfBirth(), is("d"));
    }

    @Test
    public void should_set_properties() throws Exception {
        Person person = new Person();

        InstanceBuilder.inObject(person).setPropertyValue("name","Smith");
        assertThat(person.getName(), is("Smith"));
    }

    @Test
    public void should_set_public_fields() throws Exception {
        Person person = new Person();

        InstanceBuilder.inObject(person).setPropertyValue("nickname","Smithy");
        assertThat(person.nickname, is("Smithy"));
    }


    @Test(expected = FailedToInitializeTestData.class)
    public void should_fail_if_field_does_not_exist() throws Exception {
        Person person = new Person();

        InstanceBuilder.inObject(person).setPropertyValue("unknown_field","Smithy");
    }




    @Test(expected = IllegalStateException.class)
    public void should_fail_if_no_default_constructor_available() throws Exception {
        Object[] args = {};
        Address address = InstanceBuilder.newInstanceOf(Address.class, args);
    }

    @Test(expected = IllegalStateException.class)
    public void should_fail_with_wrong_number_of_arguments() throws Exception {
        Object[] args = {"a","b"};
        Address address = InstanceBuilder.newInstanceOf(Address.class, args);
    }


}
