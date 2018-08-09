//package tech.mangosoft.autolinkedin.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import tech.mangosoft.autolinkedin.LinkedInService;
//import tech.mangosoft.autolinkedin.controller.messages.ConnectionMessage;
//import tech.mangosoft.autolinkedin.controller.messages.GrabbingMessage;
//import tech.mangosoft.autolinkedin.controller.messages.StatisticResponse;
//import tech.mangosoft.autolinkedin.db.entity.Account;
//import tech.mangosoft.autolinkedin.db.entity.Assignment;
//import tech.mangosoft.autolinkedin.db.repository.IAccountRepository;
//import tech.mangosoft.autolinkedin.db.repository.IAssignmentRepository;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import static tech.mangosoft.autolinkedin.db.entity.enums.Task.TASK_CONNECTION;
//import static tech.mangosoft.autolinkedin.db.entity.enums.Task.TASK_GRABBING;
//
//@RestController
//@RequestMapping("/assignment")
//public class AssignmentController {
//
//    private Logger logger = Logger.getLogger(AssignmentController.class.getName());
//
//    @Autowired
//    private IAssignmentRepository assignmentRepository;
//
//    @Autowired
//    private IAccountRepository accountRepository;
//
//    @Autowired
//    private LinkedInService linkedInService;
//
//    @CrossOrigin
//    @PostMapping(value = "/createGrabbing")
//    public ResponseEntity<Assignment> createGrabbingAssignment(GrabbingMessage gm) {
//        Account account = accountRepository.getAccountByUsername(gm.getLogin());
//        if (account == null) {
//            logger.log(Level.WARNING, "Account must be not null");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        Assignment assignment = new Assignment(TASK_GRABBING, gm.getLocation(), gm.getFullLocationString(), gm.getPosition(), gm.getIndustries(), account);
//        if (linkedInService.checkAllField(assignment)) {
//            logger.log(Level.WARNING, "Fields must be not null");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        Assignment assignmentDB = assignmentRepository.save(assignment);
////        linkedInService.precessingAssignment();
//        return new ResponseEntity<>(assignmentDB, HttpStatus.OK);
//    }
//
//    @CrossOrigin
//    @PostMapping(value = "/createConnection")
//    public ResponseEntity<Assignment> createConnectionAssignment(ConnectionMessage cm) {
//        Account account = accountRepository.getAccountByUsername(cm.getLogin());
//        if (account == null) {
//            logger.log(Level.WARNING, "Account must be not null");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        Assignment assignment = new Assignment(TASK_CONNECTION, cm.getLocation(), cm.getFullLocationString(), cm.getPosition(), cm.getIndustries(), cm.getMessage(), account);
//        if (linkedInService.checkMessageAndPosition(assignment)) {
//            logger.log(Level.WARNING, "Message or position must be not null");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        Assignment assignmentDB = assignmentRepository.save(assignment);
//        linkedInService.precessingAssignment();
//        return new ResponseEntity<>(assignmentDB, HttpStatus.OK);
//    }
//
//    @CrossOrigin
//    @GetMapping(value = "/getStatistics")
//    public ResponseEntity<List<StatisticResponse>> getStatistics(String email) {
//        Account account = accountRepository.getAccountByUsername(email);
//        if (account == null) {
//            logger.log(Level.WARNING, "Account must be not null");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        List<StatisticResponse> statisticResponse = linkedInService.getStatistics(account);
//        return new ResponseEntity<>(statisticResponse, HttpStatus.OK);
//    }
//}
