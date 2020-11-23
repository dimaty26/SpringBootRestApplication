package com.zmeevsky.springrest.repository;

import com.zmeevsky.springrest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select distinct u from User u left join fetch u.roles order by u.id")
    List<User> getUsers();

    @Query("from User where lower(email) like :username")
    User findUserByUsername(String username);

    @Query("select case when count(c)> 0 then true else false end from User c where lower(c.email) like lower(:username)")
    boolean existsByUsername(String username);
}
