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

    public void processing(Assignment a) {
        Assignment assignmentDB = assignmentRepository.getById(a.getId());
        ProcessingReport processingReportDB = processingReportRepository.save(new ProcessingReport()
                .setSaved(0L)
                .setFailed(0L)
                .setProcessed(0L)
                .setSuccessed(0L))
                .setStartTime(new Date());
        assignmentDB.addProcessinReport(processingReportDB);
        assignmentDB.setStatus(Status.STATUS_IN_PROGRESS);
        assignmentRepository.save(assignmentDB);
        linkedInDataProvider.grabbing(a.getId(), processingReportDB.getId(), a.getLocation(), a.getFullLocationString(), a.getPosition(), a.getIndustries(), a.getAccount());

        ProcessingReport processingReportSaved = processingReportRepository.getById(processingReportDB.getId());
        processingReportSaved.setFinishTime(new Date());
        processingReportRepository.save(processingReportSaved);
    }

    private List<String> parsingIndustries(String allIndustriesString){
        List<String> resultList = new ArrayList<>();
        allIndustriesString = allIndustriesString.replace("; ", ";");
        StringTokenizer stk = new StringTokenizer(allIndustriesString, ";");
        String[] ar = new String[stk.countTokens()];
        for (int i = 0; i < ar.length; i++) {
            resultList.add(stk.nextToken());
        }
        return resultList;
    }
}
