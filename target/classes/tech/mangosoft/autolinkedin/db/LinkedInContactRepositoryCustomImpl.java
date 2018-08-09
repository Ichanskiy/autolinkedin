package tech.mangosoft.autolinkedin.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class LinkedInContactRepositoryCustomImpl implements ILinkedInContactRepositoryCustom {

    private static Logger log = LogManager.getRootLogger();

    @Autowired
    ILinkedInContactRepository contactRepository;

    @Autowired
    IContactProcessingRepository contactProcessingRepository;

    @Transactional
    @Override
    public LinkedInContact getNextAvailableContact(Account account) {
/*        //finding new contact and save it to STATUS_ACQUIRED state;
        List<LinkedInContact> contactOpt = contactRepository.findAssignedLinkedInContactsFor(username, PageRequest.of(0, 1));
        if (contactOpt.isEmpty()){
            //mark new contract and retrieve contact again
            contactRepository.markContact(username);
            contactOpt = contactRepository.findAssignedLinkedInContactsFor(username, PageRequest.of(0, 1));
            if (contactOpt.isEmpty()){
                throw new RuntimeException("Can't retrieve new contact from db");
            }
        }
        */
        LinkedInContact contact = new LinkedInContact();
        return contact;
    }


    @Transactional
    @Override
    public boolean updateContactStatus(Long contactId, Account accountId, int status, String error, String audit) {

        //todo implement

        /*        LinkedInContact fContact = contactRepository.findById(contactId).get();
        ContactProcessing contactProcessing = contactProcessingRepository.findById(contactId).get();
        //saving results
        fContact.setAuditLog(audit);
        fContact.setStatus(status);
        fContact.setError(error);

        contactRepository.save(fContact);
        */
        return true;
    }

    @Override
    public boolean saveNewContactsBatch(List<LinkedInContact> contacts) {

        if (contacts != null)
            for (LinkedInContact contact:contacts) {
                if (contactRepository.existsLinkedInContactByFirstNameAndLastNameAndCompanyName(contact.getFirstName(), contact.getLastName(), contact.getCompanyName())) {
                    log.error("Contact " + contact.getFirstName() + " " + contact.getLastName() +" "+ contact.getCompanyName() + " already exists");
                } else {
                    log.info("Contact " + contact.getFirstName() + " " + contact.getLastName() +" "+ contact.getCompanyName() + " saved");
                    contactRepository.save(contact);
                }

            }



        return true;
    }

}
