package tech.mangosoft.autolinkedin.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public static final int STATUS_ACQUIRED = 5;

    //statuses to delete
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

    private String industries;

    private String role;

    @Column(name = "person_linkedin")
    private String linkedin;

    private String email;

    @Column(columnDefinition = "tinyint(4) default " + STATUS_IMPORTED)
    private Integer status;

    @Column(name = "update_time")
    @UpdateTimestamp
    private Date updateTime;

    @Column(name = "comments", length = 16000)
    private String comments;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contact", cascade = CascadeType.ALL)
    private List<ContactProcessing> contactProcessings;

    @JsonIgnore
    @ManyToMany(mappedBy = "contacts")
    private Set<Assignment> assignments = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "headcount_id")
    private CompanyHeadcount headcount;

    /*
    @Column(name = "assigned_linkedin_contact")
    private String assignedLinkedinContact;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "assigned_time")
    private Date assignedTime;


    private String error;
    @Column(name = "audit_log")
    private String auditLog;
*/

    @Column(name = "creation_time")
    @CreationTimestamp
    private Date createTime;

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

    public LinkedInContact setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public LinkedInContact setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
        return this;
    }

    public String getCompanyLinkedin() {
        return companyLinkedin;
    }

    public LinkedInContact setCompanyLinkedin(String companyLinkedin) {
        this.companyLinkedin = companyLinkedin;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public LinkedInContact setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public LinkedInContact setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getRole() {
        return role;
    }

    public LinkedInContact setRole(String role) {
        this.role = role;
        return this;
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

    public LinkedInContact setEmail(String email) {
        this.email = email;
        return this;
    }
    public Integer getStatus() {
        return status;
    }

    public LinkedInContact setStatus(Integer status) {
        this.status = status;
        return this;
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
    */
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public String getIndustries() {
        return industries;
    }

    public LinkedInContact setIndustries(String industries) {
        this.industries = industries;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public LinkedInContact setLocation(Location location) {
        this.location = location;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<ContactProcessing> getContactProcessings() {
        return contactProcessings;
    }

    public void setContactProcessings(List<ContactProcessing> contactProcessings) {
        this.contactProcessings = contactProcessings;
    }

    public String getComments() {
        return comments;
    }

    public LinkedInContact setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public Set<Assignment> getAssignments() {
        return assignments;
    }

    public void addAssignment(Assignment assignment){
        assignments.add(assignment);
    }

    public void removeAssignment(Assignment assignment){
        assignments.remove(assignment);
    }
}


