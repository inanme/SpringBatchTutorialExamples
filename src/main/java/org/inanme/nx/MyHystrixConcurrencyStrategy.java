package org.inanme.nx;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Callable;

public class MyHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

    private final TransactionTemplate transactionTemplate;

    public MyHystrixConcurrencyStrategy(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        //HystrixPlugins.getInstance().registerConcurrencyStrategy(new MyHystrixConcurrencyStrategy(transactionTemplate));
        return new AuthContextCallable<>(callable, transactionTemplate);
    }

    class AuthContextCallable<K> implements Callable<K> {

        private final Callable<K> actual;

        private final TransactionTemplate transactionTemplate;

        private final Authentication authentication;

        public AuthContextCallable(Callable<K> actual, TransactionTemplate transactionTemplate) {
            this.actual = actual;
            this.transactionTemplate = transactionTemplate;
            this.authentication = SecurityContextHolder.getContext().getAuthentication();
        }

        @Override
        public K call() throws Exception {
            try {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return transactionTemplate.execute(it -> {
                    try {
                        return actual.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } finally {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
    }
}

