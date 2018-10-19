package tech.mangosoft.autolinkedin.db.repository;

import org.springframework.data.repository.CrudRepository;
import tech.mangosoft.autolinkedin.db.entity.Account;
import tech.mangosoft.autolinkedin.db.entity.Assignment;
import tech.mangosoft.autolinkedin.db.entity.Location;
import tech.mangosoft.autolinkedin.db.entity.enums.Status;

import java.util.List;

public interface IAssignmentRepository extends CrudRepository<Assignment, Long> {

    Assignment getById(Long id);

    Assignment getFirstByStatus(Status status);

    List<Assignment> findByStatusOrderById(Status status);
    List<Assignment> findByStatusAndAccountOrderById(Status status, Account account);
    List<Assignment> findByStatusNotAndAccountOrderById(Status status, Account account);
    List<Assignment> getAllByAccount(Account account);
    boolean existsAllByFullLocationStringAndIndustriesAndPositionAndAccount(String location,
                                                                  String industries,
                                                                  String position,
                                                                  Account account);

    Assignment getFirstByFullLocationStringAndIndustriesAndPositionAndAccount(String location,
                                                                  String industries,
                                                                  String position,
                                                                  Account account);

    List<Assignment> findAllByStatusOrderById(Status status);
}
