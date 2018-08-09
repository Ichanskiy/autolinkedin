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

@SpringBootApplication
@ImportResource("classpath:META-INF/spring/app-context-xml.xml")
@PropertySource("classpath:application.properties")
public class Application implements CommandLineRunner {

    private static final Logger log = LogManager.getRootLogger();

    @Autowired
    private ApplicationContext context;


    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }


    public void run(String... args) throws Exception {

      LinkedInDataProvider linkedin = context.getBean(LinkedInDataProvider.class);
      //linkedin.executeAddContact();
      linkedin.executeSearchContacts();

    }


}