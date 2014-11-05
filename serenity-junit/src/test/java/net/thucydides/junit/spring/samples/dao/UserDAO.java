package net.thucydides.junit.spring.samples.dao;

import net.thucydides.junit.spring.samples.domain.User;

import java.util.List;

public interface UserDAO {

	void save(User user) ;
	List<User> findAll() ;
    void remove(User user);
    User findById(String id);
}
