package com.zmeevsky.springrest.service;


import com.zmeevsky.springrest.entity.User;

import java.util.List;

public interface UserService {

	List<User> getUsers();

	void saveUser(User theUser);

	User getUser(int theId);

	void deleteUser(int theId);

	void delete(User user);

	User findByUsername(String username);
}
