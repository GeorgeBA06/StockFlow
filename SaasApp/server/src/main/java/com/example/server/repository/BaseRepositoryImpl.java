package com.example.server.repository;

import com.example.server.config.DataBaseManager;
import com.example.server.config.TransactionManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class BaseRepositoryImpl<T,ID> implements BaseRepository<T,ID>{
private final Class<T> entityClass;


public BaseRepositoryImpl(Class<T> entityClass){
    this.entityClass = entityClass;
}

    @Override
    public Optional<T> findById(ID id){
    return TransactionManager.executeInTransaction(em -> findById(id,em));
    }

    @Override
    public List<T> findAll(){
    return TransactionManager.executeInTransaction(this::findAll);
    }

    @Override
    public T save(T entity){
    return TransactionManager.executeInTransaction(em -> saveOrUpdate(entity, em));
    }

    @Override
    public T update(T entity){
    return TransactionManager.executeInTransaction(em -> saveOrUpdate(entity, em));
    }

    @Override
    public void delete(T entity){
    TransactionManager.executeInTransactionVoid(em -> delete(entity, em));
    }

    @Override
    public void deleteById(ID id){
    TransactionManager.executeInTransactionVoid(em -> deleteById(id, em));
    }

    @Override
    public boolean existsById(ID id){
    return TransactionManager.executeInTransaction(em -> existsById(id,em));
    }

    public Optional<T> findById(ID id, EntityManager em){
    return Optional.ofNullable(em.find(entityClass, id));
    }

    public T saveOrUpdate(T entity, EntityManager em){
           Object id = extractId(entity);
           if(id == null || (id instanceof Number && ((Number)id).longValue() == 0)){
               em.persist(entity);
               return entity;
           }else{
              return em.merge(entity);
           }
    }

    public List<T> findAll(EntityManager em){
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e",entityClass)
                .getResultList();
    }

    public void delete(T entity, EntityManager em){
        T managedEntity = em.contains(entity) ? entity : em.merge(entity);
        em.remove(managedEntity);
    }

    public void deleteById(ID id, EntityManager em){
        findById(id, em).ifPresent(entity -> delete(entity, em));
    }

    public boolean existsById(ID id, EntityManager em){
        String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE e.id = :id";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("id", id);
        Long count = query.getSingleResult();
        return count > 0;
    }

    protected abstract Object extractId(T entity);
}
