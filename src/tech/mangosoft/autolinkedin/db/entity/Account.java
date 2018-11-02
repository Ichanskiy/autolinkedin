package tech.mangosoft.autolinkedin.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.mangosoft.autolinkedin.db.entity.enums.Role;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column( length = 50 )
    private String first;

    @Column( length = 50 )
    private String last;

    @Column( length = 50, unique = true)
    private String username;

    @Column( length = 50 )
    private String password;

//    @Column( name = "execution_limit" )
//    private Integer executionLimit;

    @Column( name = "grabbing_limit" )
    private Integer grabbingLimit;

    @Column( name = "last_page" )
    private Integer lastPage;

    private Role role;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.ALL)
    private List<ContactProcessing> contactProcessings;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    public Account(String first, String last, String username, String password, Integer grabbingLimit) {
        this.first = first;
        this.last = last;
        this.username = username;
        this.password = password;
        this.grabbingLimit = grabbingLimit;
    }

//    public Integer getExecutionLimit() {
//        return executionLimit;
//    }

//    public void setExecutionLimit(Integer executionLimit) {
//        this.executionLimit = executionLimit;
//    }

    public Account setLastPage(Integer lastPage) {
        this.lastPage = lastPage;
        return this;
    }
//    public Date getDailyLimitUpdateDate() {
//        return dailyLimitUpdateDate;
//    }
//
//    public Account setDailyLimitUpdateDate(Date dailyLimitUpdateDate) {
//        this.dailyLimitUpdateDate = dailyLimitUpdateDate;
//        return this;
//    }
//
//    public int getDailyLimit() {
//        return dailyLimit;
//    }
//
//    public Account setDailyLimit(int dailyLimit) {
//        this.dailyLimit = dailyLimit;
//        return this;
//    }


    public String getCaption() {
        return first + " " + last;
    }

}
