package com.zmeevsky.springmvc.security;

import com.zmeevsky.springmvc.dao.UserDao;
import com.zmeevsky.springmvc.entity.Role;
import com.zmeevsky.springmvc.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userDao.findByUsername(s);

        if (user == null) {
            throw new UsernameNotFoundException("User " + s + " was not found.");
        }

        // it's already in use
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        // should change to my own user
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}
