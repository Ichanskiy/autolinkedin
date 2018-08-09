package tech.mangosoft.autolinkedin;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import tech.mangosoft.autolinkedin.controller.messages.GrabbingMessage;
import tech.mangosoft.autolinkedin.db.entity.LinkedInContact;
import tech.mangosoft.autolinkedin.db.entity.Location;
import tech.mangosoft.autolinkedin.db.repository.ILinkedInContactRepository;
import tech.mangosoft.autolinkedin.db.repository.ILocationRepository;
import tech.mangosoft.autolinkedin.utils.JacksonUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootApplication
@ImportResource("classpath:META-INF/spring/app-context-xml.xml")
@PropertySource("classpath:application.properties")
@EnableScheduling
public class Application implements CommandLineRunner {

    private static final Logger log = LogManager.getRootLogger();

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
//        GrabbingMessage grabbingMessage = new GrabbingMessage("login", "location", "full", "position", "indust");
//        System.out.println(JacksonUtils.getJson(grabbingMessage));
    }

    public void run(String... args) throws Exception {

//        LinkedInDataProvider linkedin = context.getBean(LinkedInDataProvider.class);
//        LinkedInService linkedInService = context.getBean(LinkedInService.class);
//        linkedInService.switchAssignment();

//        linkedin.connection(null);
//        linkedin.grabbing(null);

    }
}