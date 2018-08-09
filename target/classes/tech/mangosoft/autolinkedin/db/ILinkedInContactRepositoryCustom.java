package tech.mangosoft.autolinkedin.db;

import java.util.List;

public interface ILinkedInContactRepositoryCustom {

    public LinkedInContact getNextAvailableContact(Account account);

    public boolean updateContactStatus(Long contactId, Account account,  int status, String error, String audit);

    public boolean saveNewContactsBatch(List<LinkedInContact> contacts);

}
