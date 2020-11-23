package com.zmeevsky.springrest.service;

import com.zmeevsky.springrest.entity.User;
import com.zmeevsky.springrest.exception.UserAlreadyExistsException;
import com.zmeevsky.springrest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	@Transactional
	public List<User> getUsers() {
		return userRepository.getUsers();
	}

	@Override
	@Transactional
	public void saveUser(User theUser) {

		String email = theUser.getEmail();

		if (userRepository.existsByUsername(email)) {
			throw new UserAlreadyExistsException("User with email " + email + " already exists.");
		}
		theUser.setPassword(bCryptPasswordEncoder.encode(theUser.getPassword()));
		userRepository.save(theUser);
	}

	@Override
	@Transactional
	public void updateUser(User user) {

		User userFromDB = getUser(user.getId());

		if (!user.getPassword().equals(userFromDB.getPassword())) {
			if (!bCryptPasswordEncoder.matches(user.getPassword(), userFromDB.getPassword())) {
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
		}

		userRepository.save(user);
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





