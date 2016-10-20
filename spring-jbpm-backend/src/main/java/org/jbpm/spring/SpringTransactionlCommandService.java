package org.jbpm.spring;

import javax.persistence.EntityManagerFactory;

import org.drools.persistence.TransactionManager;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.command.Command;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;


public class SpringTransactionlCommandService extends TransactionalCommandService {

    private AbstractPlatformTransactionManager transactionManager;
    private DefaultTransactionDefinition defTransDefinition = new DefaultTransactionDefinition();
    
    public SpringTransactionlCommandService(EntityManagerFactory emf, TransactionManager txm) {
        super(emf, txm);
    }

    public SpringTransactionlCommandService(EntityManagerFactory emf, AbstractPlatformTransactionManager transactionManager) {
        super(emf);
        this.transactionManager = transactionManager;
    }

    
    @Override
    public <T> T execute(Command<T> command) {
        TransactionStatus status = transactionManager.getTransaction(defTransDefinition);
        try {
            T result = super.execute(command);
            transactionManager.commit(status);
            return result;
        } catch (Throwable e) {
            transactionManager.rollback(status);
            throw new RuntimeException(e);
        }
    }


    public AbstractPlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(AbstractPlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
