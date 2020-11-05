package com.zmeevsky.springmvc.dao;

import com.zmeevsky.springmvc.entity.Role;
import com.zmeevsky.springmvc.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<Role> getAll() {
        return entityManager.createQuery("from Role").getResultList();
    }

    @Override
    @Transactional
    public Role getOne(int id) {
        return entityManager.find(Role.class, id);
    }

    @Override
    @Transactional
    public Role findRoleByName(String name) {
        TypedQuery<Role> query = entityManager.createQuery("from Role where lower(name) " +
                "like :name", Role.class);

        return query
                .setParameter("name", "%" + name.toLowerCase() + "%")
                .getSingleResult();
    }
}
