package org.inanme.springdata;

import org.inanme.springdata.domain.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Query("select c.lastname from Customer c where c.id = ?1")
    String irrelevant(Long id);
}
