package tech.mangosoft.autolinkedin.processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.mangosoft.autolinkedin.LinkedInDataProvider;
import tech.mangosoft.autolinkedin.db.entity.Assignment;
import tech.mangosoft.autolinkedin.db.entity.ProcessingReport;
import tech.mangosoft.autolinkedin.db.entity.enums.Status;
import tech.mangosoft.autolinkedin.db.repository.IAssignmentRepository;
import tech.mangosoft.autolinkedin.db.repository.IProcessingReportRepository;

import java.util.Calendar;
import java.util.Date;

@Component
public class ConnectionProcessor{

    private static final boolean finished = true;

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

        int size = assignment1.getProcessingReports().size();
        ProcessingReport processingReport  = assignment1.getProcessingReports().get(size - 1);
        linkedInDataProvider.connection(processingReport.getId(), assignmentDB);
//        boolean statusConnection = linkedInDataProvider.connection(processingReport.getId(), assignmentDB);
//        if (statusConnection == finished) {
//            finished(assignment1,  processingReport);
//        } else {
//            changeAssignmentStatus(assignment1, Status.STATUS_ASLEEP);
//            setNextCallbackTimeToAssignment(assignment1);
//        }
    }

//    public void processingAsleepAssignment(Assignment assignment) {
//        Assignment assignmentDB = assignmentRepository.getById(assignment.getId());
//        assignmentDB.setStatus(Status.STATUS_IN_PROGRESS);
////        todo fix this
//        Assignment assignment1 = assignmentRepository.save(assignmentDB);
//
//        int size = assignment1.getProcessingReports().size();
//        ProcessingReport processingReport  = assignment1.getProcessingReports().get(size <= 0 ? 0 : size - 1);
//
//        boolean statusConnection = linkedInDataProvider.connection(processingReport.getId(), assignmentDB);
//        if (statusConnection == finished) {
//            finished(assignment1, processingReport);
//        } else {
//            changeAssignmentStatus(assignment1, Status.STATUS_ASLEEP);
//            setNextCallbackTimeToAssignment(assignment1);
//        }
//    }

    private void finished(Assignment assignment, ProcessingReport processingReport){
        finishedProcessingReport(processingReport);
        finishedAssignment(assignment);
    }

    private void finishedProcessingReport(ProcessingReport processingReport) {
        ProcessingReport processingReportSaved = processingReportRepository.getById(processingReport.getId());
        processingReportSaved.setFinishTime(new Date());
        processingReportRepository.save(processingReportSaved);
    }

    private void finishedAssignment(Assignment assignment) {
        Assignment assignmentSaved = assignmentRepository.getById(assignment.getId());
        assignmentSaved.setStatus(Status.STATUS_FINISHED);
        assignmentRepository.save(assignmentSaved);
    }

    private void changeAssignmentStatus(Assignment assignment, Status status){
        Assignment assignmentDB = assignmentRepository.getById(assignment.getId());
        assignmentDB.setStatus(status);
        assignmentRepository.save(assignmentDB);
    }

    private void setNextCallbackTimeToAssignment(Assignment a) {
       Assignment assignment = assignmentRepository.getById(a.getId());
       assignment.setNextCallbackTime(getNextDay());
       assignmentRepository.save(assignment);
    }

    private Date getNextDay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Jan = 0, dec = 11
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, dayOfMonth, 12, 0);
        calendar.add(Calendar.DATE, 1);  // number of days to add
        return calendar.getTime();
    }
}