package com.example.server.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionManager {

    @FunctionalInterface
    public interface TransactionalOperation<T>{
        T execute(EntityManager em);
    }

    @FunctionalInterface
    public interface VoidTransactionalOperation{
        void execute(EntityManager em);
    }

    public static <T> T executeInTransaction(TransactionalOperation<T> operation){
        EntityManager em = DataBaseManager.getEntityManager();
        EntityTransaction et = em.getTransaction();

        boolean isNewTransaction = !et.isActive();

        try{
            if(isNewTransaction){
                et.begin();
                log.debug("Started new transaction");
            }else {
                log.debug("Joining existing transaction");
            }

            T result = operation.execute(em);

            if(isNewTransaction){
                et.commit();
                log.debug("Commited transaction!");
            }

            return result;
        } catch (Exception ex){
            if(isNewTransaction && et.isActive()){
                et.rollback();
                log.error("Transaction rolled back ", ex);
            } else if (!isNewTransaction) {
                et.setRollbackOnly();
                log.error("Marked transaction for rollback due to error ", ex);
            }
            throw new RuntimeException("Transaction failed", ex);
        } finally {
            if(isNewTransaction){
                DataBaseManager.closeEntityManager();
            }
        }

    }

    public static void executeInTransactionVoid(VoidTransactionalOperation operation){
        executeInTransaction(em -> {
            operation.execute(em);
            return null;
        });
    }
}
