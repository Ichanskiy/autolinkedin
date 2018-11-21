package tech.mangosoft.autolinkedin.db.repository;

import org.springframework.data.repository.CrudRepository;
import tech.mangosoft.autolinkedin.db.entity.CompanyHeadcount;

import java.util.List;

public interface ICompanyHeadcountRepository extends CrudRepository<CompanyHeadcount, Long> {

    List<CompanyHeadcount> findAll();
    CompanyHeadcount getById(Long id);
    CompanyHeadcount getByHeadcount(String headcount);

}
