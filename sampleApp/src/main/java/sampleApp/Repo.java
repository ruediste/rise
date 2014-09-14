package sampleApp;

import java.util.Calendar;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import sampleApp.entities.User;

@ApplicationScoped
public class Repo {

	private User user;

	@PostConstruct
	public void initialize() {
		user = new User();
		user.setFistName("John");
		user.setLastName("Smith");

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.clear();
		cal.set(2014, 0, 1);

		user.setLastLogin(cal.getTime());
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
