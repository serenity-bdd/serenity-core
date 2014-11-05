package net.thucydides.junit.spring;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.junit.spring.samples.dao.UserDAO;
import net.thucydides.junit.spring.samples.domain.User;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(ThucydidesRunner.class)
@ContextConfiguration(locations = "/spring/db-config.xml")
public class WhenWorkingWithDAOs {

    @Managed
    WebDriver driver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;

    @Rule
    public SpringIntegration springIntegration = new SpringIntegration();

    @Autowired
    public UserDAO userDAO;

    @Test
    public void shouldCreateRecordsInTestDatabase() {
        User newUser = new User("Bill", "secret", "USA");
        userDAO.save(newUser);

        List<User> users = userDAO.findAll();
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getName(), is("Bill"));
    }

    @After
    public void deleteUsers() {
        List<User> users = userDAO.findAll();
        for(User user : users) {
            userDAO.remove(user);
        }
    }
}