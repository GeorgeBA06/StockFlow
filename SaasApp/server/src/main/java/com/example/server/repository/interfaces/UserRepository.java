package com.example.server.repository.interfaces;

import com.example.server.entity.User;
import com.example.server.repository.BaseRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {
    User findByEmail(String email);

    Optional<User> findById(Long id, EntityManager em);
    List<User> findAll(EntityManager em);
    User saveOrUpdate(User entity, EntityManager em);
    void deleteById(Long id, EntityManager em);
    boolean existsById(Long id, EntityManager em);
    User findByEmail(String email, EntityManager em);
}
