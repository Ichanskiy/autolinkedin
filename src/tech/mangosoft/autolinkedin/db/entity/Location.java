package tech.mangosoft.autolinkedin.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column( length = 250, unique = true )
    private String location;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location", cascade = CascadeType.ALL)
    private List<LinkedInContact> linkedInContacts;

    public Location(String location) {
        this.location = location;
    }

}
