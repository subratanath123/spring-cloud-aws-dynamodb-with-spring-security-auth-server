package com.authorization.server.dao;

import com.authorization.server.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class AuthUserDao {

    @Autowired
    private UserDao userDao;

    public UserDetails getUserDetails(String email) {
        return userDao.findByEmail(email);
    }
}
