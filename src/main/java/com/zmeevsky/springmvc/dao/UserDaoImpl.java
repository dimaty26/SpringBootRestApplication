package com.zmeevsky.springmvc.dao;

import com.zmeevsky.springmvc.entity.User;
import com.zmeevsky.springmvc.exception.UserAlreadyExistsException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    public List<User> getUsers() {
        return entityManager.createQuery("from User").getResultList();
    }

    @Override
    public void saveUser(User user) {

        String email = user.getEmail();

        try {
            findByUsername(email);
            throw new UserAlreadyExistsException("User with email " + email + " already exists.");
        } catch (NoResultException ignored) {
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        entityManager.merge(user);
    }

    @Override
    public void updateUser(User user) {
        User updatedUser = getUser(user.getId());
        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setLastName(user.getLastName());
        updatedUser.setEmail(user.getEmail());
        if (!user.getPassword().equals(updatedUser.getPassword())) {
            if (!bCryptPasswordEncoder.matches(user.getPassword(), updatedUser.getPassword())) {
                updatedUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            }
        }
        entityManager.merge(updatedUser);
    }

    @Override
    public User findByUsername(String username) {

        TypedQuery<User> query = entityManager.createQuery("from User where lower(email) " +
                "like :username", User.class);

        return query
                .setParameter("username", "%" + username.toLowerCase() + "%")
                .getSingleResult();
    }

    @Override
    public User getUser(int id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void deleteUser(int id) {
        User user = getUser(id);
        entityManager.remove(user);
    }

    @Override
    public List<User> searchUsers(String theSearchName) {

        Session session = entityManager.unwrap(Session.class);

        Query<User> query = null;

        if (theSearchName != null && theSearchName.trim().length() > 0) {

            //search for firstName or lastName ... case insensitive
            query = session.createQuery("from User where lower(firstName) " +
                    "like :theName or lower(lastName) like :theName", User.class);
            query.setParameter("theName", "%" + theSearchName.toLowerCase() + "%");
        } else {
            //theSearchName is empty ... so just get all customers
            query = session.createQuery("from User", User.class);
        }

        return query.getResultList();
    }
}
