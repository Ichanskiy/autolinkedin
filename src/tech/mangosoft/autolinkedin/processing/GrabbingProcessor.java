package tech.mangosoft.autolinkedin.processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.mangosoft.autolinkedin.LinkedInDataProvider;
import tech.mangosoft.autolinkedin.db.entity.Assignment;
import tech.mangosoft.autolinkedin.db.entity.ProcessingReport;
import tech.mangosoft.autolinkedin.db.entity.enums.Status;
import tech.mangosoft.autolinkedin.db.repository.IAssignmentRepository;
import tech.mangosoft.autolinkedin.db.repository.IProcessingReportRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

@Component
public class GrabbingProcessor{

    @Autowired
    private LinkedInDataProvider linkedInDataProvider;

    @Autowired
    private IAssignmentRepository assignmentRepository;

    @Autowired
    private IProcessingReportRepository processingReportRepository;

    public void processing(Long assignmentId) {
        Assignment assignment = assignmentRepository.getById(assignmentId);
        ProcessingReport processingReportDB = processingReportRepository.save(new ProcessingReport()
                .setSaved(0L)
                .setFailed(0L)
                .setProcessed(0L)
                .setSuccessed(0L))
                .setStartTime(new Date());
        assignment.addProcessinReport(processingReportDB);
        assignment.setStatus(Status.STATUS_IN_PROGRESS);
        Assignment assignmentDB = assignmentRepository.save(assignment);
        linkedInDataProvider.grabbing(assignmentDB.getId(), processingReportDB.getId(),
                assignmentDB.getFullLocationString(), assignmentDB.getPosition(), assignmentDB.getIndustries(), assignmentDB.getAccount());

        ProcessingReport processingReportSaved = processingReportRepository.getById(processingReportDB.getId());
        processingReportSaved.setFinishTime(new Date());
        processingReportRepository.save(processingReportSaved);
    }

    public void processingSales(Long assignmentId) {
        Assignment assignment = assignmentRepository.getById(assignmentId);
        ProcessingReport processingReportDB = processingReportRepository.save(new ProcessingReport()
                .setSaved(0L)
                .setFailed(0L)
                .setProcessed(0L)
                .setSuccessed(0L))
                .setStartTime(new Date());
        assignment.addProcessinReport(processingReportDB);
        assignment.setStatus(Status.STATUS_IN_PROGRESS);
        Assignment assignmentDB = assignmentRepository.save(assignment);
        linkedInDataProvider.grabbingSales(assignmentDB.getId(), assignmentDB.getAccount());
        ProcessingReport processingReportSaved = processingReportRepository.getById(processingReportDB.getId());
        processingReportSaved.setFinishTime(new Date());
        processingReportRepository.save(processingReportSaved);
    }
}
