package tech.mangosoft.autolinkedin.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "headcount")
public class CompanyHeadcount {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column( length = 250, unique = true )
    private String headcount;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "headcount", cascade = CascadeType.ALL)
    private List<LinkedInContact> linkedInContacts;

    @ManyToMany(mappedBy = "headcounts")
    private Set<Assignment> assignment = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeadcount() {
        return headcount;
    }

    public void setHeadcount(String headcount) {
        this.headcount = headcount;
    }

    public List<LinkedInContact> getLinkedInContacts() {
        return linkedInContacts;
    }

    public void setLinkedInContacts(List<LinkedInContact> linkedInContacts) {
        this.linkedInContacts = linkedInContacts;
    }

    public void addLinkedInContacts(LinkedInContact linkedInContact) {
        this.linkedInContacts.add(linkedInContact);
    }
}
