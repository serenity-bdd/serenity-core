package net.thucydides.junit.spring.samples.domain;

import javax.persistence.*;

@Entity
@Table(name="USER")
public class User {

	private Long id;
	private String name;
	private String password;
	private String country;


    public User() {}

    public User(String name, String password, String country) {
        this.name = name;
        this.password = password;
        this.country = country;
    }

    @Id
	@GeneratedValue
	@Column(name="USER_ID")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="USER_NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="USER_PASSWORD")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name="USER_COUNTRY")
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

}
