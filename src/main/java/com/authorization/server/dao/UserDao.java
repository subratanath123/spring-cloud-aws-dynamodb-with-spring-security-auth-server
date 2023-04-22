package com.authorization.server.dao;

import com.authorization.server.entity.User;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableScan
@Repository
public interface UserDao extends CrudRepository<User, Integer> {

    List<User> findById(String id);

    User findByEmail(String email);

    boolean existsByEmail(String email);

}
