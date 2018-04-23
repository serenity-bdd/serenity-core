package net.thucydides.junit.spring.samples.dao;

import net.thucydides.junit.spring.samples.domain.User;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;

import java.util.List;

public class HibernateUserDAO implements UserDAO {

	private HibernateTemplate hibernateTemplate;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	public void save(User user) {
		hibernateTemplate.saveOrUpdate(user);
	}

	public List<User> findAll() {
		return (List<User>) hibernateTemplate.find("from User");
	}

    public void remove(User user) {
        hibernateTemplate.delete(user);
    }

    public User findById(String id) {
        return (User) hibernateTemplate.load(User.class, id);
    }
}
