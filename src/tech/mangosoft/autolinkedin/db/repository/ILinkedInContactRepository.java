package tech.mangosoft.autolinkedin.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.mangosoft.autolinkedin.db.entity.LinkedInContact;
import tech.mangosoft.autolinkedin.db.entity.Location;

import java.util.List;
import java.util.stream.Stream;

public interface ILinkedInContactRepository extends JpaRepository<LinkedInContact, Long> {

    LinkedInContact findFirstByStatusAndRoleContainsAndContactProcessingsIsNull(int status, String role);

    LinkedInContact findFirstByStatusAndLocationAndContactProcessingsIsNull(int status, Location location);

    LinkedInContact findFirstByLocation(Location location);

    LinkedInContact findFirstByStatusAndLocation(Integer status, Location location);

    List<LinkedInContact> findAllByLocation(Location location);

    List<LinkedInContact> findAllByStatusAndLocationAndContactProcessingsIsNull(int status, Location location);

    LinkedInContact findFirstByStatusAndLocationAndRoleContainsAndContactProcessingsIsNull(int status, Location location, String role);

    LinkedInContact findFirstByStatusAndLocationAndRoleContains(int status, Location location, String role);

    LinkedInContact findFirstByStatusAndLocationAndRoleContainsAndIndustriesContainsAndContactProcessingsIsNull(int status, Location location, String role, String industries);

    List<LinkedInContact> findAllByStatusAndLocationAndRoleContainsAndIndustriesContainsAndContactProcessingsIsNull(int status, Location location, String role, String industries);

    LinkedInContact getById(Long id);

    @Query("select c.id " +
        "from LinkedInContact c " +
        "join ContactProcessing processing on c.id = processing.contact.id " +
        "join Account a on processing.account.id = a.id " +
        "where a.username = :username " +
        "order by c.id")
    Page<Long> getAvailableContact(@Param("username") String username, Pageable pageable);

    public boolean existsLinkedInContactByFirstNameAndLastNameAndCompanyName(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("companyName") String companyName);

    LinkedInContact getFirstByFirstNameAndLastName(String firstName, String lastName);

}
