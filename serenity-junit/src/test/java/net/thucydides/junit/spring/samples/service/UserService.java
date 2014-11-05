package net.thucydides.junit.spring.samples.service;

import net.thucydides.junit.spring.samples.dao.UserDAO;
import net.thucydides.junit.spring.samples.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class UserService {

    private UserDAO userDAO;

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> listUsers() {
        return userDAO.findAll();
    }

    public void addNewUser(User newUser) {
        userDAO.save(newUser);
    }

    public User findById(String id) {
        return userDAO.findById(id);
    }
}
