package com.github.andrz25.delta.user_service.repository;

import com.github.andrz25.delta.user_service.model.User;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    public Optional<User> getUserByUsername(String username);

    public boolean existsByEmail(String email);

    public boolean existsByEmailAndIdNot(String email, Long id);

    public boolean existsByUsernameAndIdNot(String username, Long id);

    public boolean existsByUsername(String username);
}
