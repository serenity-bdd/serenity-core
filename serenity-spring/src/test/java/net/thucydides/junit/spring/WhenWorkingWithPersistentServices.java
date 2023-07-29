package net.thucydides.junit.spring;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.pages.Pages;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.junit.spring.samples.domain.User;
import net.thucydides.junit.spring.samples.service.UserService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

// This test is designed to show that transactions are automatically rolled back after each test.
// Unfortunately, the Spring configuration seems to be broken, so rolling back the transactions
// after the tests doesn't actually have any effect. Same behaviour as with the Spring runner, so
// the demo config probably needs fixing.
//
@Ignore("Should work but doesn't")
@RunWith(SerenityRunner.class)
@ContextConfiguration(locations = "classpath:spring/db-config.xml")
//@TransactionConfiguration
@Transactional
public class WhenWorkingWithPersistentServices {

    @Managed
    WebDriver driver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;

    @Rule
    public SpringIntegration springIntegration = new SpringIntegration();

    @Autowired
    public UserService userService;

    @Before
    public void setupTestData() {
        userService.addNewUser(new User("Jake", "secret", "USA"));
        userService.addNewUser(new User("Jill", "secret", "USA"));
    }

    @Test
    public void testsShouldHaveTestDataAvailable() {
        List<User> users = userService.listUsers();
        assertThat(users.size(), is(2));
        userService.addNewUser(new User("Jane", "secret", "USA"));
    }

    @Test
    public void testsShouldHaveSameTestDataAvailable() {
        List<User> users = userService.listUsers();
        assertThat(users.size(), is(2));
        userService.addNewUser(new User("Jane", "secret", "USA"));
    }


}
