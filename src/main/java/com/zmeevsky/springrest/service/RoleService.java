package com.zmeevsky.springrest.service;

import com.zmeevsky.springrest.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> findAll();
    Role findById(int id);
}
