package tech.mangosoft.autolinkedin.db.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.mangosoft.autolinkedin.db.entity.*;
import tech.mangosoft.autolinkedin.db.entity.enums.Task;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

import static tech.mangosoft.autolinkedin.db.entity.LinkedInContact.STATUS_ACQUIRED;
import static tech.mangosoft.autolinkedin.db.entity.LinkedInContact.STATUS_ERROR;

@Service
public class LinkedInContactRepositoryCustomImpl implements ILinkedInContactRepositoryCustom {

    private static Logger logger = LogManager.getRootLogger();
    private static String logeMessage = "";

    @Autowired
    private ILinkedInContactRepository contactRepository;

    @Autowired
    private IAssignmentRepository assignmentRepository;

    @Autowired
    private IContactProcessingRepository contactProcessingRepository;

    @Autowired
    private IProcessingReportRepository processingReportRepository;

    @Autowired
    private ILocationRepository locationRepository;

    //OLD
    @Transactional
    @Override
    public LinkedInContact getNextAvailableContact(int page, Assignment assignment) {
//        Page<LinkedInContact> linkedInContacts = contactRepository.findAllByStatus(LinkedInContact.STATUS_NEW, PageRequest.of(page, 1));
        LinkedInContact contact = null;
        if (assignment.getPosition() != null && assignment.getIndustries() != null && assignment.getFullLocationString() != null) {
            Location location = locationRepository.getLocationByLocationLike(assignment.getFullLocationString());
            if (location != null) {
                contact = contactRepository.findFirstByStatusAndLocationAndRoleContainsAndIndustriesContainsAndContactProcessingsIsNull(LinkedInContact.STATUS_NEW, location, assignment.getPosition(), assignment.getIndustries());
                if (contact == null) {
                    contact = contactRepository.findFirstByStatusAndLocationAndIndustriesAndContactProcessingsIsNull(LinkedInContact.STATUS_NEW, location, assignment.getIndustries());
                }
//                if (contact == null) {
//                    contact = contactRepository.findFirstByStatusAndLocationAndRoleContainsAndContactProcessingsIsNull(LinkedInContact.STATUS_NEW, location, assignment.getPosition());
//                }
                if (contact == null) {
                    contact = contactRepository.findFirstByStatusAndLocationAndIndustriesContainsAndRoleContains(LinkedInContact.STATUS_NEW, location, assignment.getIndustries(), assignment.getPosition());
                }
                if (contact == null) {
                    contact = contactRepository.findFirstByStatusAndLocationAndIndustriesContains(LinkedInContact.STATUS_NEW, location, assignment.getIndustries());
                }
                if (contact == null) {
                    contact = contactRepository.findFirstByStatusAndLocationAndContactProcessingsIsNull(LinkedInContact.STATUS_NEW, location);
                }
                if (contact == null) {
                    contact = contactRepository.findFirstByStatusAndLocation(LinkedInContact.STATUS_NEW, location);
                }
                //todo fix
//                if (contact == null) {
//                    contact = contactRepository.findFirstByLocation(location);
//                }
            }
            else {
                logger.error("LOCATION IS NULL");
                return null;
            }
        }
//        if (contact == null) {
//            contact = contactRepository.findFirstByStatusAndRoleContainsAndContactProcessingsIsNull(LinkedInContact.STATUS_NEW, assignment.getPosition());
//        }
        if (contact == null) {
            logger.error("Can't retrieve new contact from db");
            return null;
        }
        return contactRepository.save(contact.setStatus(STATUS_ACQUIRED));
    }

//    @Transactional
//    @Override
//    public LinkedInContact getNextAvailableContact(int page, Assignment assignment) {
//        LinkedInContact linkedInContact = contactRepository.findFirstByAssignment(assignment);
//        return deleteAssignmentFromContact(linkedInContact);
//    }

//    private LinkedInContact deleteAssignmentFromContact(LinkedInContact linkedInContact) {
//        if (linkedInContact == null) {
//            return null;
//        }
//        linkedInContact.setAssignment(null);
//        return contactRepository.save(linkedInContact);
//    }

    @Transactional
    @Override
    public boolean updateContactStatus(Assignment assignment, LinkedInContact contact, Account account, int status, String error, String audit, Long processingReportId) {

        ProcessingReport report = processingReportRepository.getById(processingReportId);

        ContactProcessing contactProcessing = contactProcessingRepository.findFirstByAccountIdAndContactId(account.getId(), contact.getId());
        LinkedInContact linkedInContactDb = contactRepository.findFirstById(contact.getId());
        Assignment assignmentDb = assignmentRepository.getById(assignment.getId());
        if (linkedInContactDb != null && assignmentDb != null) {
            linkedInContactDb.addAssignment(assignmentDb);
            assignmentRepository.save(assignmentDb);
        }

        if (contactProcessing == null) {
            contactProcessing = new ContactProcessing();
            contactProcessing.setAccount(account);
            contactProcessing.setContact(contact);
        }
        contactProcessing.setStatus(status);
        contactProcessing.setAssignment(assignment);
        contactProcessing.setError(error);
        contactProcessing.setAuditLog(audit);
        ContactProcessing contactProcessingDB = contactProcessingRepository.save(contactProcessing);
        if (contactProcessingDB == null) {
            report.incrementFailed(1L);
            processingReportRepository.save(report);
            return false;
        }
        if (status == STATUS_ERROR) {
            report.incrementFailed(1L);
        } else {
            report.incrementSuccessed(1L);
            report.incrementProcessed(1L);
            report.incrementSaved(1L);
        }
        processingReportRepository.save(report);
        return true;
    }

    @Override
    @Transactional
    public boolean saveNewContactsBatch(Account account, Long assignmentId, List<LinkedInContact> contacts, Long processingReportId, String log) {
        logeMessage = log;
        ProcessingReport report = processingReportRepository.getById(processingReportId);
        Assignment assignmentDb = assignmentRepository.getById(assignmentId);

        if (contacts != null && assignmentDb != null){
            for (LinkedInContact contact : contacts) {
                if (contactRepository.existsLinkedInContactByFirstNameAndLastNameAndCompanyName(contact.getFirstName(), contact.getLastName(), contact.getCompanyName())) {
                    LinkedInContact linkedInContactDB = contactRepository.getFirstByFirstNameAndLastName(contact.getFirstName(), contact.getLastName());
                    if (linkedInContactDB != null) {
                        contactRepository.save(updateContact(linkedInContactDB, contact));
                        contactProcessingRepository.save(getNewContactProcessing(account, ContactProcessing.STATUS_GRABBED, linkedInContactDB, assignmentDb));
                        String logByContacts = String.format("Contact id = %-4s %-4s %-4s was added by  %-4s",
                                linkedInContactDB.getId() != null ? linkedInContactDB.getId().toString() : "",
                                linkedInContactDB.getFirstName() != null ? linkedInContactDB.getFirstName() : "",
                                linkedInContactDB.getLastName() != null ? linkedInContactDB.getLastName() : "",
                                getAccountName(linkedInContactDB));
                        report.addLogByContacts(logByContacts);
                        assignmentDb.getContacts().add(linkedInContactDB);
                        linkedInContactDB.addAssignment(assignmentDb);
                        assignmentRepository.save(assignmentDb);
                    }
                    logger.error("Contact " + contact.getFirstName() + " " + contact.getLastName() + " " + contact.getCompanyName() + " already exists");
                } else {
                    logger.info("Contact " + contact.getFirstName() + " " + contact.getLastName() +" "+ contact.getCompanyName() + " saved");
                    try {
                        LinkedInContact linkedInContactDB = contactRepository.save(contact);
                        String logByContacts = String.format("Contact %-4s %-4s saved with id %-4s",
                                linkedInContactDB.getFirstName(), linkedInContactDB.getLastName(), linkedInContactDB.getId().toString());
                        report.addLogByContacts(logByContacts);
                        contactProcessingRepository.save(getNewContactProcessing(account, ContactProcessing.STATUS_GRABBED, linkedInContactDB, assignmentDb));
                        assignmentDb.getContacts().add(linkedInContactDB);
                        linkedInContactDB.addAssignment(assignmentDb);
                        assignmentRepository.save(assignmentDb);
                    } catch (Exception e) {
                        String logByContacts = String.format("Contact %-4s %-4s was not saved",
                                contact.getFirstName(), contact.getLastName());
                        report.addLogByContacts(logByContacts);
                        logger.info("Contact " + contact.getFirstName() + " " + contact.getLastName() +" "+ contact.getCompanyName() + " not saved");
                        report.incrementFailed(1L);
                        contactProcessingRepository.save(getNewContactProcessing(account, ContactProcessing.STATUS_ERROR, contactRepository.save(contact), assignmentDb));
                    }
                    report.incrementSaved(1L);
                }
            }
            report.incrementSuccessed((long) contacts.size());
        }
        processingReportRepository.save(report);
        return true;
    }

    private String getAccountName(LinkedInContact contact) {
        Set<Assignment> assignments = contact.getAssignments();
        for (Assignment assignment : assignments) {
            if (assignment.getAccount() != null && !assignment.getTask().equals(Task.TASK_CONNECTION)) {
                return assignment.getAccount().getUsername();
            }
        }
        return Strings.EMPTY;
    }

    private ContactProcessing getNewContactProcessing(Account account, int status, LinkedInContact contact, Assignment assignment){
        return new ContactProcessing()
                .setContact(contact)
                .setAccount(account)
                .setStatus(status)
                .setAssignment(assignment)
                .setAuditLog(logeMessage);
    }

    private LinkedInContact updateContact(LinkedInContact contactDb, LinkedInContact newContact) {
        return contactDb
                .setIndustries(newContact.getIndustries())
                .setCompanyName(newContact.getCompanyName())
                .setRole(newContact.getRole())
                .setCompanyWebsite(newContact.getCompanyWebsite());
    }
}
