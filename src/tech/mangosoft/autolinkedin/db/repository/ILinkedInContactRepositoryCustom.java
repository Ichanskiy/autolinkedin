package tech.mangosoft.autolinkedin.db.repository;

import tech.mangosoft.autolinkedin.db.entity.Account;
import tech.mangosoft.autolinkedin.db.entity.Assignment;
import tech.mangosoft.autolinkedin.db.entity.LinkedInContact;

import java.util.List;

public interface ILinkedInContactRepositoryCustom {

    LinkedInContact getNextAvailableContact(int page, Assignment assignment);

    boolean updateContactStatus(Assignment assignment, LinkedInContact contactId, Account account, int status, String error, String audit, Long processingReportId);

    boolean saveNewContactsBatch(Account account, Long assignmentId, List<LinkedInContact> contacts, Long processingReportId, String log);

}
