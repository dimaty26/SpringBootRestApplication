package com.zmeevsky.springmvc.dao;

import com.zmeevsky.springmvc.entity.Role;

import java.util.List;

public interface RoleDao {

    Role getOne(int id);

    List<Role> getAll();

    Role findRoleByName(String name);
}
