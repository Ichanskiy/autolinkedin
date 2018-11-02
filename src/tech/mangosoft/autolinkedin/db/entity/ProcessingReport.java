package tech.mangosoft.autolinkedin.db.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "processing_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingReport {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "start_time", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "finish_time", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishTime;

    @Column(name = "update_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Lob
    private String logByContacts = "";

    private Long processed;

    private Long saved;

    private Long successed;

    private Long failed;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    public ProcessingReport(Date startTime, Date finishTime, Timestamp updateTime, Long processed, Long saved, Long successed, Long failed) {
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.updateTime = updateTime;
        this.processed = processed;
        this.saved = saved;
        this.successed = successed;
        this.failed = failed;
    }

    public Long incrementSaved(Long value){
        return this.saved += value;
    }

    public Long incrementProcessed(Long value){
        return this.processed += value;
    }

    public Long incrementSuccessed(Long value){
        return this.successed += value;
    }

    public Long incrementFailed(Long value){
        return this.failed += value;
    }

    public ProcessingReport setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public ProcessingReport setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
        return this;
    }

    public ProcessingReport setProcessed(Long processed) {
        this.processed = processed;
        return this;
    }

    public ProcessingReport setSaved(Long saved) {
        this.saved = saved;
        return this;
    }

    public ProcessingReport setSuccessed(Long successed) {
        this.successed = successed;
        return this;
    }

    public ProcessingReport setFailed(Long failed) {
        this.failed = failed;
        return this;
    }

    public ProcessingReport setAssignment(Assignment assignment) {
        this.assignment = assignment;
        return this;
    }

    public ProcessingReport setLogByContacts(String logContacts) {
        this.logByContacts = logContacts;
        return this;
    }

    public void addLogByContacts(String log){
        this.setLogByContacts(this.getLogByContacts().concat("\n").concat(log));
    }
}
