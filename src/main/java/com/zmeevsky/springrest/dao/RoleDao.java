package com.zmeevsky.springrest.dao;

import com.zmeevsky.springrest.entity.Role;

import java.util.List;

public interface RoleDao {

    Role getOne(int id);

    List<Role> getAll();

    Role findRoleByName(String name);
}
