package tech.mangosoft.autolinkedin.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.LastModifiedDate;
import tech.mangosoft.autolinkedin.db.entity.enums.CompanyHeadcount;
import tech.mangosoft.autolinkedin.db.entity.enums.Status;
import tech.mangosoft.autolinkedin.db.entity.enums.Task;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "assignment")
public class Assignment {
//
//    public static final int TASK_DO_NOTHING = 0;
//    public static final int TASK_GRABBING = 1;
//    public static final int TASK_CONNECTION = 2;
//
//    public static final int STATUS_NEW = 0;
//    public static final int STATUS_IN_PROGRESS = 1;
//    public static final int STATUS_SUSPENDED = 2;
//    public static final int STATUS_ERROR = 32;
//    public static final int STATUS_FINISHED = 16;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
    private Integer page;

    @Column
    private Task task;

    @Column
    private Status status;

    @Column(length = 4096)
    private String params;

    @Column(name = "error_message", length = 4096)
    private String errorMessage;

    private String fullLocationString;

    private String position;

    private String industries;

    private String groups;

    private Date dailyLimitUpdateDate = new Date();

    private int dailyLimit = 0;

    private Integer countsFound;
    
    private Integer countMessages;

    @Column(length = 4096)
    private String message;

//    @Column(name = "update_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    @Type(type = "java.sql.Timestamp")
//    @LastModifiedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    private Timestamp updateTime;

//    @Column(columnDefinition = "companyHeadcount")
    private CompanyHeadcount companyHeadcount;

    @Column(name = "nextCallbackTime")
    private Date nextCallbackTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assignment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<LinkedInContact> contacts = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<ContactProcessing> contactProcessings = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<ProcessingReport> processingReports = new LinkedList<>();

    public Assignment() {
    }

    public Assignment(Task task, String fullLocationString, String position, String industries, Account account) {
        this.page = 0;
        this.task = task;
        this.status = Status.STATUS_NEW;
        this.industries = industries;
        this.fullLocationString = fullLocationString;
        this.position = position;
        this.account = account;
    }

    public Assignment(Task task, String fullLocationString, String position, String industries, String message, Account account) {
        this.page = 0;
        this.task = task;
        this.status = Status.STATUS_NEW;
        this.industries = industries;
//        this.location = location;
        this.fullLocationString = fullLocationString;
        this.position = position;
        this.account = account;
        this.message = message;
    }
//    public enum Task {
//
//        TASK_DO_NOTHING(0),
//        TASK_GRABBING(1),
//        TASK_CONNECTION(2);
//
//        private int id;
//
//        Task(int id) {
//            this.id = id;
//        }
//
//        public int getId() {
//            return id;
//        }
//
//        public static Task getTaskById(int id) throws Exception {
//            for (Task e : values()) {
//                if (e.id == id) return e;
//            }
//            throw new Exception("No Task defined with id " + id);
//        }
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public Assignment setTask(Task task) {
        this.task = task;
        return this;
    }

    public Assignment setStatus(Status status) {
        this.status = status;
        return this;
    }

    public void setCompanyHeadcount(CompanyHeadcount companyHeadcount) {
        this.companyHeadcount = companyHeadcount;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public CompanyHeadcount getCompanyHeadcount() {
        return companyHeadcount;
    }

    public Status getStatus() {
        return status;
    }

    public Date getDailyLimitUpdateDate() {
        return dailyLimitUpdateDate;
    }

    public void setDailyLimitUpdateDate(Date dailyLimitUpdateDate) {
        this.dailyLimitUpdateDate = dailyLimitUpdateDate;
    }

    //    public Timestamp getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setUpdateTime(Timestamp updateTime) {
//        this.updateTime = updateTime;
//    }

    public Account getAccount() {
        return account;
    }

    public Assignment setAccount(Account account) {
        this.account = account;
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public Assignment setPage(int page) {
        this.page = page;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getFullLocationString() {
        return fullLocationString;
    }

    public Assignment setFullLocationString(String fullLocationString) {
        this.fullLocationString = fullLocationString;
        return this;
    }

    public String getPosition() {
        return position;
    }

    public Assignment setPosition(String position) {
        this.position = position;
        return this;
    }

    public String getIndustries() {
        return industries;
    }

    public Assignment setIndustries(String industries) {
        this.industries = industries;
        return this;
    }

    public Assignment setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCountsFound() {
        return countsFound;
    }

    public void setCountsFound(Integer countsFound) {
        this.countsFound = countsFound;
    }

    public void addProcessinReport(ProcessingReport pr) {
        processingReports.add(pr);
        pr.setAssignment(this);
    }

    public void removeProcessinReport(ProcessingReport pr) {
        processingReports.remove(pr);
        pr.setAssignment(null);
    }

    public void addContactProcessings(ContactProcessing cp) {
        contactProcessings.add(cp);
        cp.setAssignment(this);
    }

    public void removeContactProcessings(ContactProcessing cp) {
        contactProcessings.remove(cp);
        cp.setAssignment(null);
    }

    public Integer getCountMessages() {
        return countMessages;
    }

    public void setCountMessages(Integer countMessages) {
        this.countMessages = countMessages;
    }

    public List<ProcessingReport> getProcessingReports() {
        return processingReports;
    }

    public List<ContactProcessing> getContactProcessings() {
        return contactProcessings;
    }


    public void addNewContact(LinkedInContact lc) {
        contacts.add(lc);
        lc.setAssignment(this);
    }

    public void addContact(LinkedInContact lc) {
        contacts.add(lc);
        lc.setAssignment(this);
    }

    public void removeContact(LinkedInContact lc) {
        contacts.remove(lc);
        lc.setAssignment(null);
    }

    public List<LinkedInContact> getContacts() {
        return contacts;
    }

    public Date getNextCallbackTime() {
        return nextCallbackTime;
    }

    public void setNextCallbackTime(Date nextCallbackTime) {
        this.nextCallbackTime = nextCallbackTime;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }
}
