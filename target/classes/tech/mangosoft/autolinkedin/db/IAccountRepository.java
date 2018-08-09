package tech.mangosoft.autolinkedin.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface IAccountRepository extends CrudRepository<Account, Long> {

    Account getAccountByUsername(@Param("username") String username);
}
