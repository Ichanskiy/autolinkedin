package tech.mangosoft.autolinkedin.db.repository;

import org.springframework.data.repository.CrudRepository;
import tech.mangosoft.autolinkedin.db.entity.ContactProcessing;
import tech.mangosoft.autolinkedin.db.entity.enums.Status;

import java.util.List;

public interface IContactProcessingRepository extends CrudRepository<ContactProcessing, Long> {

    ContactProcessing findFirstByAccountIdAndContactId(Long accountId, Long contactId);
}
