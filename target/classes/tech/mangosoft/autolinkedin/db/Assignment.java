package tech.mangosoft.autolinkedin.db;

import org.hibernate.annotations.Type;

import javax.persistence.*;

import java.sql.Timestamp;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "assignment")
public class Assignment {

    public static final int TASK_DO_NOTHING = 0;
    public static final int TASK_GRABBING = 1;
    public static final int TASK_CONNECTION = 2;

    public static final int STATUS_NEW = 0;
    public static final int STATUS_IN_PROGRESS = 1;
    public static final int STATUS_SUSPENDED = 2;
    public static final int STATUS_ERROR = 32;
    public static final int STATUS_FINISHED = 16;


    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column( columnDefinition = "tinyint(4) default " + TASK_DO_NOTHING )
    private Integer task;

    @Column( length = 4096)
    private String params;

    @Column( name = "saved_params", length = 4096)
    private String savedParams;

    @Column( columnDefinition = "tinyint(4) default " + STATUS_NEW )
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "update_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP" )
    @Type(type="java.sql.Timestamp")
    private Timestamp updateTime;

}
