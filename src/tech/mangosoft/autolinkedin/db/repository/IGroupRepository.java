package tech.mangosoft.autolinkedin.db.repository;

import org.springframework.data.repository.CrudRepository;
import tech.mangosoft.autolinkedin.db.entity.Group;

public interface IGroupRepository extends CrudRepository<Group, Long> {
    Group getById(Long id);

    Group getByName(String name);
}
