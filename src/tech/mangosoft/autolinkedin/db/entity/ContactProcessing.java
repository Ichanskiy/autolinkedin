package tech.mangosoft.autolinkedin.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "contact_processing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public ContactProcessing setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public ContactProcessing setAccount(Account account) {
        this.account = account;
        return this;
    }

    public ContactProcessing setAuditLog(String auditLog) {
        this.auditLog = auditLog;
        return this;
    }

    public ContactProcessing setContact(LinkedInContact contact) {
        this.contact = contact;
        return this;
    }

    public ContactProcessing setAssignment(Assignment assignment) {
        this.assignment = assignment;
        return this;
    }
}


