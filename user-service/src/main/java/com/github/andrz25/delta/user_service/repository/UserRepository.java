package com.github.andrz25.delta.user_service.repository;

import com.github.andrz25.delta.user_service.model.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    public boolean existsByEmail(String email);

    public boolean existsByEmailAndIdNot(String email, Long id);

    public boolean existsByUsernameAndIdNot(String username, Long id);

    public boolean existsByUsername(String username);
}
