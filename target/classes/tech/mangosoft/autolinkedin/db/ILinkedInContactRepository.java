package tech.mangosoft.autolinkedin.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ILinkedInContactRepository extends CrudRepository<LinkedInContact, Long> {
/*
    @Query("SELECT c FROM LinkedInContact c WHERE c.status= 0 AND (LOWER(c.assignedLinkedinContact) = LOWER(:assignedContact) OR c.assignedLinkedinContact IS NULL ) ORDER BY c.id DESC")
    public List<LinkedInContact> findLinkedInContactsFor(@Param("assignedContact") String assignedContact, Pageable pageable );

    final String markContactQuery = "update linkedin_contacts c set c.assigned_linkedin_contact =:assignedContact, c.assigned_time = NOW(), c.status = "+ LinkedInContact.STATUS_ACQUIRED +
            " WHERE c.status= "+LinkedInContact.STATUS_NEW +" AND (LOWER(c.assigned_linkedin_contact) = LOWER(:assignedContact) OR c.assigned_linkedin_contact IS NULL ) LIMIT 1";
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = markContactQuery, nativeQuery = true)
    void markContact(@Param("assignedContact") String assignedContact);

    final String findAssignedLinkedInContactsForQuery = "SELECT c FROM LinkedInContact c " +
            "WHERE c.status= "+ LinkedInContact.STATUS_ACQUIRED +" AND LOWER(c.assignedLinkedinContact) = LOWER(:assignedContact) ORDER BY c.id DESC";
    @Query(findAssignedLinkedInContactsForQuery)
    public List<LinkedInContact> findAssignedLinkedInContactsFor(@Param("assignedContact") String assignedContact, Pageable pageable );
*/

    public boolean existsLinkedInContactByFirstNameAndLastNameAndCompanyName(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("companyName") String companyName);

}
