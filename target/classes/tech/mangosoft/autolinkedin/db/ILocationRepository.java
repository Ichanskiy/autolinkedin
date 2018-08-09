package tech.mangosoft.autolinkedin.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ILocationRepository extends CrudRepository<Location, Long> {

    Location getLocationByLocation(@Param("location") String location);

}
