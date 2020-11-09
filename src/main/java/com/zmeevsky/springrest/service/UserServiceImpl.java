package com.zmeevsky.springrest.service;

import com.zmeevsky.springrest.entity.User;
import com.zmeevsky.springrest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	@Transactional
	public void saveUser(User theUser) {
		userRepository.save(theUser);
	}

	@Override
	@Transactional
	public User getUser(int theId) {
		return userRepository.getOne(theId);
	}

	@Override
	@Transactional
	public User findByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	@Override
	@Transactional
	public void deleteUser(int theId) {
		userRepository.deleteById(theId);
	}

	@Override
	@Transactional
	public void delete(User user) {
		userRepository.delete(user);
	}
}





