package tech.mangosoft.autolinkedin.db;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "contact")
public class LinkedInContact {
    //new statuses
    public static final int STATUS_IMPORTED = 0;
    public static final int STATUS_NEW = 1;
    public static final int STATUS_REQUIRE_LINKED_IN_URL_UPDATE = 2;
    public static final int STATUS_REQUIRE_EMAIL = 3;
    public static final int STATUS_REQUIRE_LOAD_FROM_OTHER_ACCOUNT = 33;

    //statuses to delete
    public static final int STATUS_ACQUIRED = 5;
    public static final int STATUS_PROCESSED = 4;

    public static final int STATUS_ERROR = 16;
    public static final int STATUS_PREPROCESS_NEEDED = 33;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_website")
    private String companyWebsite;

    @Column(name = "company_linkedin")
    private String companyLinkedin;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
    private String role;

    @Column(name = "person_linkedin")
    private String linkedin;
    private String email;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(columnDefinition = "tinyint(4) default " + STATUS_IMPORTED)
    private Integer status;
/*
    @Column(name = "assigned_linkedin_contact")
    private String assignedLinkedinContact;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "assigned_time")
    private Date assignedTime;


    private String error;
    @Column(name = "audit_log")
    private String auditLog;

    @Column(name = "creation_time")
    @Type(type="java.sql.Timestamp")
    private Timestamp createTime;
*/
    @Column(name = "update_time")
    @Type(type="java.sql.Timestamp")
    private Timestamp updateTime;

    public LinkedInContact(String companyName, String companyWebsite, String companyLinkedin, String firstName, String lastName, String role, String linkedin, String email, Timestamp updateTime) {
        this.companyName = companyName;
        this.companyWebsite = companyWebsite;
        this.companyLinkedin = companyLinkedin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.linkedin = linkedin;
        this.email = email;
        this.updateTime = updateTime;
    }

    public LinkedInContact() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyLinkedin() {
        return companyLinkedin;
    }

    public void setCompanyLinkedin(String companyLinkedin) {
        this.companyLinkedin = companyLinkedin;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
/*

    public String getAssignedLinkedinContact() {
        return assignedLinkedinContact;
    }

    public void setAssignedLinkedinContact(String assignedLinkedinContact) {
        this.assignedLinkedinContact = assignedLinkedinContact;
    }

    public Date getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(Date assignedTime) {
        this.assignedTime = assignedTime;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
*/

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}


