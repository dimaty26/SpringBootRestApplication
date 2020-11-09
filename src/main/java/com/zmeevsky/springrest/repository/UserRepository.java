package com.zmeevsky.springrest.repository;

import com.zmeevsky.springrest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("from User where lower(email) like :username")
    User findUserByUsername(String username);
}
