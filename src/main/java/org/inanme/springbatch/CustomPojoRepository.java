package org.inanme.springbatch;

import org.inanme.springdata.domain.CustomPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomPojoRepository extends JpaRepository<CustomPojo, Integer> {

}
