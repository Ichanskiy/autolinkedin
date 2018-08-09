package tech.mangosoft.autolinkedin.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "contact_processing")
public class ContactProcessing {

    public static final int STATUS_IMPORTED = 0;
    public static final int STATUS_GRABBED = 1;

    public static final int STATUS_NEED_UPDATE_URL = 2;
    public static final int STATUS_EMAIL_REQUIRED = 3;

    public static final int STATUS_PROCESSED = 10;

    public static final int STATUS_ERROR = 30;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String compact;

    private Integer status;

    private String error;

    @Column(name = "audit_log", length = 4096)
    private String auditLog;

    @Column(name = "update_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Type(type = "java.sql.Timestamp")
    private Timestamp updateTime;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private LinkedInContact contact;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    public ContactProcessing() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public ContactProcessing setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Account getAccount() {
        return account;
    }

    public ContactProcessing setAccount(Account account) {
        this.account = account;
        return this;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getAuditLog() {
        return auditLog;
    }

    public ContactProcessing setAuditLog(String auditLog) {
        this.auditLog = auditLog;
        return this;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public LinkedInContact getContact() {
        return contact;
    }

    public ContactProcessing setContact(LinkedInContact contact) {
        this.contact = contact;
        return this;
    }

    public String getCompact() {
        return compact;
    }

    public void setCompact(String compact) {
        this.compact = compact;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public ContactProcessing setAssignment(Assignment assignment) {
        this.assignment = assignment;
        return this;
    }
}


