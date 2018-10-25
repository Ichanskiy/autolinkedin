package tech.mangosoft.autolinkedin;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tech.mangosoft.autolinkedin.controller.messages.StatisticResponse;
import tech.mangosoft.autolinkedin.db.entity.Account;
import tech.mangosoft.autolinkedin.db.entity.Assignment;
import tech.mangosoft.autolinkedin.db.entity.enums.Status;
import tech.mangosoft.autolinkedin.db.entity.enums.Task;
import tech.mangosoft.autolinkedin.db.repository.IAccountRepository;
import tech.mangosoft.autolinkedin.db.repository.IAssignmentRepository;
import tech.mangosoft.autolinkedin.processing.ConnectionProcessor;
import tech.mangosoft.autolinkedin.processing.GrabbingProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

import static tech.mangosoft.autolinkedin.utils.CSVUtils.parseLine;


/**
 * <h1> LinkedIn Service!</h1>
 * The LinkedInService implements initial logic application
 * <p>
 *
 * Method annotate @Scheduled is point to start do assignment
 * user friendly and it is assumed as a high quality code.
 *
 *
 * @author  Ichanskiy
 * @version 1.0
 * @since   2018-06-06
 */
@Service
public class LinkedInService {

    private static Logger logger = Logger.getLogger(LinkedInService.class.getName());

    private int accountNumber = 1;
    private static final int STEP = 1;
    private static final String FIRST_ACCOUNT = "abaranovskiy1985@gmail.com";
    private static final String SECOND_ACCOUNT = "oleg.goncharenko@gmail.com";
    private static final String LOCATION = "Location";
    private static final String INDUSTRY = "Industry priority #1";
    private static final String POSITION = "Position";
    private List<String> locations = new LinkedList<>();
    private List<String> industries = new ArrayList<>();
    private List<String> positions = new ArrayList<>();
    private int locationIndex = 0;
    private int industryIndex = 0;
    private int positionIndex = 0;

    @Autowired
    LinkedInDataProvider linkedInDataProvider;

    @Autowired
    GrabbingProcessor grabbingProcessor;

    @Autowired
    ConnectionProcessor connectionProcessor;

    @Autowired
    private IAssignmentRepository assignmentRepository;

    @Autowired
    private IAccountRepository accountRepository;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void precessingAssignment() {
        Account account = accountRepository.getAccountByUsername(getAccountName());
//        List<Assignment> assignmentList = assignmentRepository.findByStatusAndAccountOrderById(Status.STATUS_NEW, account);
        List<Assignment> assignmentList = assignmentRepository.findAllByStatusOrderById(Status.STATUS_NEW);
        for (Assignment assignment : assignmentList) {
            logger.info("assignment with id = " + assignment.getId() + " started!");
            if (assignment.getTask().equals(Task.TASK_GRABBING)) {
                if (!checkField(assignment)) {
                    assignmentRepository.save(assignment
                            .setStatus(Status.STATUS_ERROR)
                            .setErrorMessage("Field must be not null"));
                    continue;
                }
                grabbingProcessor.processing(assignment);
            }
            if (assignment.getTask().equals(Task.TASK_GRABBING_SALES)) {
                if (!checkField(assignment)) {
                    assignmentRepository.save(assignment
                            .setStatus(Status.STATUS_ERROR)
                            .setErrorMessage("Field must be not null"));
                    continue;
                }
                grabbingProcessor.processingSales(assignment);
            }
            if (assignment.getTask().equals(Task.TASK_CONNECTION)) {
                  connectionProcessor.processing(assignment);
            }
        }
    }

    private List<Assignment> assignmentList = new ArrayList<>();

//    @Scheduled(cron = "0 0/1 * * * ?")
//    public void test() {
//        Account account = accountRepository.getById(72L);
//        Assignment assignment = new Assignment();
//        assignment.setFullLocationString("Greater New York City Area");
//        assignment.setPosition("CEO");
//        assignment.setIndustries("Internet");
//        assignment.setCompanyHeadcount(CompanyHeadcount.FIVEHUNDREDONE_ONETHOUSAND);
//        assignment.setAccount(account);
//        assignment.addProcessinReport(new ProcessingReport().setAssignment(assignment));
//        assignmentRepository.save(assignment);
//        grabbingProcessor.processingSales(assignment);
////        linkedInDataProvider.grabbingSales(assignment.getId(), 10L, account);
//    }


//    public void precessingAssignment() {
//        assignmentList.clear();
//
//        Account account = accountRepository.getAccountByUsername(getAccountName());
//        assignmentList.addAll(assignmentRepository.findByStatusAndAccountOrderById(Status.STATUS_NEW, account));
//        assignmentList.addAll(assignmentRepository.findByStatusAndAccountOrderById(Status.STATUS_ASLEEP, account));
//
//        if (assignmentList.size() == 0) {
//            createGrabbingAssignmentsFromFile(new File("data/Targeting Industries - USA.csv"), account);
//        }
//
//        for (Assignment assignment : assignmentList) {
//            logger.info("assignment with id = " + assignment.getId() + " started!");
//            if (assignment.getTask().equals(Task.TASK_GRABBING) && assignment.getStatus().equals(Status.STATUS_NEW)) {
////              null check fields
//                if (!checkField(assignment)) {
//                    assignmentRepository.save(assignment
//                            .setStatus(Status.STATUS_ERROR)
//                            .setErrorMessage("Field must be not null"));
//                    continue;
//                }
//                grabbingProcessor.processing(assignment);
//            }
//            if (assignment.getTask().equals(Task.TASK_CONNECTION)) {
//                if (assignment.getStatus().equals(Status.STATUS_NEW)) {
//                    connectionProcessor.processing(assignment);
//                } else if (assignment.getStatus().equals(Status.STATUS_ASLEEP)) {
//                    if (timeIsCorrect(assignment)) {
//                        connectionProcessor.processingAsleepAssignment(assignment);
//                    }
//                }
//            }
//        }
//    }


//    @Scheduled(cron = "0 0/1 * * * ?")
//    public void precessingAssignment() {
//        assignmentList.clear();
//
//        Account account = accountRepository.getAccountByUsername(getAccountName());
//        assignmentList.addAll(assignmentRepository.findByStatusAndAccountOrderById(Status.STATUS_NEW, account));
//        assignmentList.addAll(assignmentRepository.findByStatusAndAccountOrderById(Status.STATUS_ASLEEP, account));
//
//        for (Assignment assignment : assignmentList) {
//            logger.info("assignment with id = " + assignment.getId() + " started!");
//            if (assignment.getTask().equals(Task.TASK_GRABBING) && assignment.getStatus().equals(Status.STATUS_NEW)) {
////              null check fields
//                if (!checkField(assignment)) {
//                    assignmentRepository.save(assignment.setStatus(Status.STATUS_ERROR).setErrorMessage("Field must be not null"));
//                    continue;
//                }
//                grabbingProcessor.processing(assignment);
//            }
//            if (assignment.getTask().equals(Task.TASK_CONNECTION)) {
//                if (assignment.getStatus().equals(Status.STATUS_NEW)) {
//                    connectionProcessor.processing(assignment);
//                } else if (assignment.getStatus().equals(Status.STATUS_ASLEEP)) {
//                    if (timeIsCorrect(assignment)) {
//                        connectionProcessor.processingAsleepAssignment(assignment);
//                    }
//                }
//            }
//        }
//    }

    private String getAccountName() {
        accountNumber = accountNumber + STEP;
        return accountNumber % 2 == 1 ? FIRST_ACCOUNT : SECOND_ACCOUNT;
    }

    private boolean timeIsCorrect(Assignment a) {
        Assignment assignment = assignmentRepository.getById(a.getId());
        Date nextCallbackDate = assignment.getNextCallbackTime();
        Date toDay = new Date();
        return toDay.after(nextCallbackDate);
    }


//    @PostConstruct
    public void createGrabbingAssignmentsFromFile(File file, Account account) {
        getLinesFromFile(file);
//        reverseLists();
//        getLinesFromFile(new File("data/Targeting Industries - USA.csv"));
//        Account account1 = accountRepository.getAccountByUsername("oleg.goncharenko@gmail.com");
        Assignment assignment = new Assignment();
        for (String location : locations) {
            assignment.setFullLocationString(location);
            for (String industry : industries) {
                assignment.setIndustries(industry);
                for (String position : positions) {
                    assignment.setPosition(position);
                    if (assignmentIsExist(assignment, account)){
                        continue;
                    }
                    assignment.setStatus(Status.STATUS_NEW);
                    assignment.setAccount(account);
                    assignment.setTask(Task.TASK_GRABBING);
                    assignmentRepository.save(assignment);
                }
            }
        }
    }

    private boolean assignmentIsExist(Assignment assignment, Account account) {
        Assignment assignmentDB = assignmentRepository.getFirstByFullLocationStringAndIndustriesAndPositionAndAccount(assignment.getFullLocationString(),
                assignment.getIndustries(),
                assignment.getPosition(),
                account);
        if (assignmentDB == null) {
            return false;
        }
        return assignmentDB.getTask().equals(Task.TASK_GRABBING);
    }

    private void getLinesFromFile(File file) {
        Scanner scanner = getScanner(file);
        if (scanner == null) {
            return;
        }
        int i = 0;
        while (scanner.hasNext()) {
            List<String> line = parseLine(scanner.nextLine());
            if (i == 0) {
                setIndex(line);
                ++i;
                continue;
            }
            setStringsToLists(line);
        }
        scanner.close();
    }

    private Scanner getScanner(File file){
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            logger.info(e.getMessage());
        }
        return scanner;
    }

    private void setIndex(List<String> line) {
        if (!line.containsAll(Arrays.asList(LOCATION, INDUSTRY, POSITION))) {
            return;
        }
        locationIndex = line.indexOf(LOCATION);
        industryIndex = line.indexOf(INDUSTRY);
        positionIndex = line.indexOf(POSITION);
    }

    private void setStringsToLists(List<String> line) {
        if (!Strings.isEmpty(line.get(industryIndex))) {
            industries.add(line.get(industryIndex));
        }
        if (!Strings.isEmpty(line.get(positionIndex))) {
            positions.add(line.get(positionIndex));
        }
        if (!Strings.isEmpty(line.get(locationIndex))) {
            locations.add(line.get(locationIndex));
        }
    }

    private void reverseLists() {
        Collections.reverse(locations);
        Collections.reverse(industries);
        Collections.reverse(positions);
    }

    /**
     * @author  Ichanskiy
     *
     * This is the method chesk fields.
     * @param assignment input object.
     * @return boolean true if field not null, else false
     */
    private boolean checkField(Assignment assignment){
        if (assignment == null) {
            logger.info("Assignment is null");
            return false;
        }
        if (assignment.getFullLocationString() == null) {
            logger.info("Location or full location is null");
            return false;
        }
        return true;
    }

    /**
     * @author  Ichanskiy
     *
     * This is the method chesk all fields.
     * @param assignment input object.
     * @return boolean true if all field not null, else false
     */
    public boolean checkAllField(Assignment assignment){
        return assignment == null
                || assignment.getFullLocationString() == null
                || assignment.getFullLocationString().isEmpty()
                || assignment.getIndustries() == null
                || assignment.getIndustries().isEmpty()
                || assignment.getPosition() == null
                || assignment.getPosition().isEmpty();
    }
    /**
     * @author  Ichanskiy
     *
     * This is the method chesk messege and posiion.
     * @param assignment input object.
     * @return boolean true if all field not null, else false
     */
    public boolean checkMessageAndPosition(Assignment assignment){
        return assignment == null
                || assignment.getMessage() == null
                || assignment.getMessage().isEmpty()
                || assignment.getPosition() == null
                || assignment.getPosition().isEmpty();
    }

    /**
     * @author  Ichanskiy
     *
     * This is the method create statistic.
     * @param account current account.
     * @return object that contains statistics
     */
    public List<StatisticResponse> getStatistics(Account account) {
        List<StatisticResponse> statisticResponses = new ArrayList<>();
        List<Assignment> assignments = assignmentRepository.getAllByAccount(account);
        for (Assignment a : assignments) {
            StatisticResponse statistic = new StatisticResponse();
            statistic.setAssignmentName(concatAllString(a.getTask().name(), a.getPosition(), a.getIndustries(), a.getFullLocationString()));
            statistic.setErrorMessage(a.getErrorMessage());
            statistic.setStatus(a.getStatus().name());
            statistic.setPage(a.getPage());
            if (!CollectionUtils.isEmpty(a.getProcessingReports())) {
                statistic.setProcessed(a.getProcessingReports().get(0).getProcessed());
                statistic.setSaved(a.getProcessingReports().get(0).getSaved());
                statistic.setSuccessed(a.getProcessingReports().get(0).getSuccessed());
                statistic.setFailed(a.getProcessingReports().get(0).getFailed());
            }
            statisticResponses.add(statistic);
        }
        return statisticResponses;
    }

    /**
     * @author  Ichanskiy
     *
     * This is the method concate all string.
     * @param s all strings.
     * @return final string after joining. Example: "SEO; Games; New York;"
     */
    private String concatAllString(String... s) {
        StringBuilder result = new StringBuilder();
        for (String s1 : s) {
            result.append(s1);
            result.append("; ");
        }
        System.out.println("result = " + result);
        return result.toString();
    }

    /**
     * @author  Ichanskiy
     *
     * This is the method who compare date.
     * @param date1 first date.
     * @param date2 second date.
     * @return true if day of date equals, else false.
     */
    private static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
