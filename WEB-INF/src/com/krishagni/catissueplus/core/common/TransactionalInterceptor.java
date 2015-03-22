
package com.krishagni.catissueplus.core.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

import edu.wustl.common.util.logger.Logger;

@Aspect
public class TransactionalInterceptor {

	private static Logger LOGGER = Logger.getCommonLogger(TransactionalInterceptor.class);

	private PlatformTransactionManager transactionManager;
	
	private TransactionTemplate txTmpl;

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
		this.txTmpl = new TransactionTemplate(transactionManager);
		this.txTmpl.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	}

	@Pointcut("execution(@com.krishagni.catissueplus.core.common.PlusTransactional * *(..))")
	public void transactionalMethod() {
	}

	@Around("transactionalMethod()")
	public Object startTransaction(final ProceedingJoinPoint pjp) {
		return txTmpl.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {				
				try {
					Object result = pjp.proceed();					
					if (result instanceof ResponseEvent) {
						ResponseEvent<?> resp = (ResponseEvent<?>)result;
						if (!resp.isSuccessful() && !resp.isForceTxCommitEnabled()) {
							status.setRollbackOnly();
						}
					}
					
					return result ;
				} catch (Throwable t) {
					status.setRollbackOnly();
					t.printStackTrace();
					throw OpenSpecimenException.serverError(t);
				}
			}			
		});
	}
}
