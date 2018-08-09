package tech.mangosoft.autolinkedin.db;


import org.hibernate.annotations.Type;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "processing_report")
public class ProcessingReport {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @Column(name = "start_time", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "finish_time", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishTime;


    private Long processed;
    private Long saved;
    private Long successed;
    private Long failed;


    @Column(name = "update_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP" )
    @Type(type="java.sql.Timestamp")
    private Timestamp updateTime;

    public ProcessingReport(Assignment assignment, Date startTime, Date finishTime, Long processed, Long saved, Long successed, Long failed, Timestamp updateTime) {
        this.assignment = assignment;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.processed = processed;
        this.saved = saved;
        this.successed = successed;
        this.failed = failed;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Long getProcessed() {
        return processed;
    }

    public void setProcessed(Long processed) {
        this.processed = processed;
    }

    public Long getSaved() {
        return saved;
    }

    public void setSaved(Long saved) {
        this.saved = saved;
    }

    public Long getSuccessed() {
        return successed;
    }

    public void setSuccessed(Long successed) {
        this.successed = successed;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
