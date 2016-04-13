package org.inanme.springbatch;

import org.inanme.springdata.domain.CustomPojo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomPojoRepository extends CrudRepository<CustomPojo, Integer> {

}
