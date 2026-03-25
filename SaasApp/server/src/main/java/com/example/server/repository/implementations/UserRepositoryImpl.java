package com.example.server.repository.implementations;

import com.example.server.config.TransactionManager;
import com.example.server.entity.User;
import com.example.server.repository.BaseRepository;
import com.example.server.repository.BaseRepositoryImpl;
import com.example.server.repository.interfaces.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl extends BaseRepositoryImpl<User,Long> implements UserRepository {

    public UserRepositoryImpl(){
        super(User.class);
    }

    public Optional<User> findById(Long id, EntityManager em) {
        return super.findById(id, em);
    }

    public List<User> findAll(EntityManager em) {
        return super.findAll(em);
    }

    public User saveOrUpdate(User entity, EntityManager em) {
        return super.saveOrUpdate(entity, em);
    }

    public void delete(User entity, EntityManager em) {
        super.delete(entity, em);
    }

    public void deleteById(Long id, EntityManager em) {
        super.deleteById(id, em);
    }

    public boolean existsById(Long id, EntityManager em) {
        return super.existsById(id, em);
    }

    public User findByEmail(String email, EntityManager em) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst().orElse(null);
    }

    @Override
    public User findByEmail(String email){
        return TransactionManager.executeInTransaction(em ->{
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u where u.email = :email", User.class);
            query.setParameter("email", email);
            return query.getResultStream().findFirst().orElse(null);

        });
    }

    @Override
    protected Object extractId(User entity){
        return entity.getId();
    }
}
