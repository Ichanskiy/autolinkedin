package tech.mangosoft.autolinkedin.processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.mangosoft.autolinkedin.LinkedInDataProvider;
import tech.mangosoft.autolinkedin.db.entity.Assignment;
import tech.mangosoft.autolinkedin.db.entity.ProcessingReport;
import tech.mangosoft.autolinkedin.db.entity.enums.Status;
import tech.mangosoft.autolinkedin.db.repository.IAssignmentRepository;
import tech.mangosoft.autolinkedin.db.repository.IProcessingReportRepository;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class ConnectionProcessor{

    @Autowired
    private LinkedInDataProvider linkedInDataProvider;

    @Autowired
    private IAssignmentRepository assignmentRepository;

    @Autowired
    private IProcessingReportRepository processingReportRepository;

    public void processing(Assignment assignment) {
        Assignment assignmentDB = assignmentRepository.getById(assignment.getId());
        ProcessingReport processingReportDB = processingReportRepository.save(new ProcessingReport()
                .setSaved(0L)
                .setFailed(0L)
                .setProcessed(0L)
                .setSuccessed(0L))
                .setStartTime(new Date());
        assignmentDB.addProcessinReport(processingReportDB);
        assignmentDB.setStatus(Status.STATUS_IN_PROGRESS);
//        todo fix this
        Assignment assignment1 = assignmentRepository.save(assignmentDB);
        linkedInDataProvider.connection(assignment1.getProcessingReports().get(0).getId(), assignmentDB);

        ProcessingReport processingReportSaved = processingReportRepository.getById(processingReportDB.getId());
        processingReportSaved.setFinishTime(new Date());
        processingReportRepository.save(processingReportSaved);
    }
}
