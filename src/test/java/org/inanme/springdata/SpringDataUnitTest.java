package org.inanme.springdata;

import com.google.common.collect.Lists;
import org.inanme.springdata.domain.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringDataConfig.class, loader = AnnotationConfigContextLoader.class)
@Transactional(transactionManager = "jpaTransactionManager")
public class SpringDataUnitTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void basicInsertTest() {
        Customer customer = new Customer("mert", "inan");
        Customer persisted = customerRepository.save(customer);
        assertThat(persisted.getId(), is(notNullValue()));
        Iterable<Customer> all = customerRepository.findAll();
        assertThat(Lists.newArrayList(all), hasSize(1));
        assertThat(customerRepository.count(), is(1l));
    }

    @Test
    public void findLastName() {
        Customer customer = new Customer("mert", "inan");
        Customer save = customerRepository.save(customer);

        String lastname = customerRepository.irrelevant(save.getId());

        assertThat(lastname, is(customer.getLastname()));
    }
}
