package tech.mangosoft.autolinkedin.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "headcount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public void addLinkedInContacts(LinkedInContact linkedInContact) {
        this.linkedInContacts.add(linkedInContact);
    }
}
