package com.zmeevsky.springmvc.service;


import com.zmeevsky.springmvc.entity.User;

import java.util.List;

public interface UserService {

	List<User> getUsers();

	void saveUser(User theUser);

	void updateUser(User user);

	User getUser(int theId);

	void deleteUser(int theId);

	List<User> searchUsers(String theSearchName);

	User findByUsername(String username);
}
