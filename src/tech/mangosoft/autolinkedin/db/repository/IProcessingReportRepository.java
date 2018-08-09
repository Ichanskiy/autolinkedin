package tech.mangosoft.autolinkedin.db.repository;

import org.springframework.data.repository.CrudRepository;
import tech.mangosoft.autolinkedin.db.entity.ProcessingReport;

public interface IProcessingReportRepository extends CrudRepository<ProcessingReport, Long> {

    ProcessingReport getById(Long id);
}
