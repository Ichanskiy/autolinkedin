package tech.mangosoft.autolinkedin.db;

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

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private LinkedInContact contact;

    private String compact;

    private Integer status;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private String error;

    @Column(name = "audit_log")
    private String auditLog;

    @Column(name = "update_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP" )
    @Type(type="java.sql.Timestamp")
    private Timestamp updateTime;



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

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public void setAuditLog(String auditLog) {
        this.auditLog = auditLog;
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

    public void setContact(LinkedInContact contact) {
        this.contact = contact;
    }

    public String getCompact() {
        return compact;
    }

    public void setCompact(String compact) {
        this.compact = compact;
    }
}


