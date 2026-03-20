package com.example.server.repository;

import com.example.server.config.DataBaseManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class BaseRepositoryImpl<T,ID> implements BaseRepository<T,ID>{
protected final DataBaseManager dataBaseManager;
private final Class<T> entityClass;


public BaseRepositoryImpl(DataBaseManager dataBaseManager, Class<T> entityClass){
    this.dataBaseManager = dataBaseManager;
    this.entityClass = entityClass;
}

    @Override
    public Optional<T> findById(ID id){
    try(EntityManager em = dataBaseManager.getEntityManager()) {
    return Optional.ofNullable(em.find(entityClass, id));
    }
    }

    @Override
    public T save(T entity){
    try(EntityManager em = dataBaseManager.getEntityManager()){
        EntityTransaction et = em.getTransaction();
        try{
            et.begin();
            em.persist(entity);
            et.commit();
            return entity;
        }catch (Exception ex){
            if(et.isActive()) et.rollback();
            log.error("Failed to save entity", ex);
            throw new RuntimeException("Failed to save entity");
        }
    }
    }

    @Override
    public T update(T entity){
    try(EntityManager em = dataBaseManager.getEntityManager()) {
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            T merged = em.merge(entity);
            et.commit();
            log.debug("Entity updated: {}", merged);
            return merged;
        }catch (Exception ex){
            if(et.isActive()) et.rollback();
            log.error("Failed to update entity", ex);
            throw new RuntimeException("Failed to update entity", ex);
        }

    }
    }

    @Override
    public List<T> findAll(){
    try(EntityManager em = dataBaseManager.getEntityManager()) {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e",entityClass)
                .getResultList();
    }
    }

    @Override
    public boolean existsById(ID id){
    return findById(id).isPresent();
    }

    @Override
    public void deleteById(ID id){
    try(EntityManager em = dataBaseManager.getEntityManager()){
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            T entity = em.find(entityClass, id);
            if(entity != null){
                em.remove(entity);
                log.debug("Entity with id {} deleted", id);
            }else {
                log.warn("Entity with id {} not found, nothing to delete", id);
            }
            et.commit();
        }catch (Exception ex){
            if(et.isActive()) et.rollback();
            log.error("Failed to delete entity with id {}", id, ex);
            throw new RuntimeException("Failed to delete entity with id: " + id, ex);
        }
    }
    }

    public void delete(T entity){
    try(EntityManager em = dataBaseManager.getEntityManager()){
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            T managedEntity = em.contains(entity) ? entity : em.merge(entity);
            em.remove(managedEntity);
            et.commit();
        }catch (Exception ex){
            if(et.isActive()) et.rollback();
            log.error("Failed to delete entity", ex);
            throw new RuntimeException("Failed to delete entity", ex);
        }
    }
    }
}
