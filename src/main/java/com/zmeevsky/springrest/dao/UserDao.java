package com.zmeevsky.springrest.dao;

import com.zmeevsky.springrest.entity.User;

import java.util.List;

public interface UserDao {

    List<User> getUsers();

    void saveUser(User user);

    void updateUser(User user);

    User findByUsername(String username);

    User getUser(int id);

    void deleteUser(int id);

    List<User> searchUsers(String theSearchName);
}
