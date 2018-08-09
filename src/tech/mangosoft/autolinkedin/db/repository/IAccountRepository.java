package tech.mangosoft.autolinkedin.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tech.mangosoft.autolinkedin.db.entity.Account;

public interface IAccountRepository extends CrudRepository<Account, Long> {

    Account getById(Long id);

    Account getAccountByUsername(@Param("username") String username);
}
