package com.zmeevsky.springmvc.service;

import com.zmeevsky.springmvc.dao.UserDao;
import com.zmeevsky.springmvc.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final UserDao userDao;

	@Autowired
	public UserServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	@Transactional
	public List<User> getUsers() {
		return userDao.getUsers();
	}

	@Override
	@Transactional
	public void saveUser(User theUser) {
		userDao.saveUser(theUser);
	}

	@Override
	@Transactional
	public void updateUser(User user) {userDao.updateUser(user);}

	@Override
	@Transactional
	public User getUser(int theId) {
		return userDao.getUser(theId);
	}

	@Override
	@Transactional
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	@Transactional
	public void deleteUser(int theId) {
		userDao.deleteUser(theId);
	}

	@Override
	@Transactional
	public List<User> searchUsers(String theSearchName) {
		return userDao.searchUsers(theSearchName);
	}
}





