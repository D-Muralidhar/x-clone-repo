package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    // find by email for login
    User findByEmail(String email);

    // used in searchUsers(...)
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrBioContainingIgnoreCase(
            String username, String email, String bio
    );
}
