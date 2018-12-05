package tech.mangosoft.autolinkedin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.mangosoft.autolinkedin.config.GlobalProperties;
import tech.mangosoft.autolinkedin.db.entity.*;
import tech.mangosoft.autolinkedin.db.entity.enums.Status;
import tech.mangosoft.autolinkedin.db.repository.*;
import tech.mangosoft.selenium.SeleniumUtils;
import tech.mangosoft.selenium.WebDriverFactoryBean;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component("linkedInDataProvider")
@Scope("prototype")
@Lazy(value = true)
public class LinkedInDataProvider implements ApplicationContextAware {

    private static Logger log = LogManager.getRootLogger();
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LinkedInDataProvider.class.getName());
    private static StringWriter stringWriter = new StringWriter();

    @Autowired
    private GlobalProperties globalProperties;

    @Autowired
    private WebDriver driver;

    @Autowired
    private SeleniumUtils utils;

    @Autowired
    private WebDriverFactoryBean webDriverFactoryBean;

    @Autowired
    private ILinkedInContactRepositoryCustom contactRepositoryCustom;

    @Autowired
    private ILinkedInContactRepository contactRepository;

    @Autowired
    private ILocationRepository locationRepository;

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private IAssignmentRepository assignmentRepository;

    @Autowired
    private IContactProcessingRepository contactProcessingRepository;

    private String contactNameParsedFromSite = "";

    private boolean emailRequired = false;

    private AtomicInteger executed = new AtomicInteger(0);
    private AtomicInteger grabbed = new AtomicInteger(0);
    private AtomicInteger paggingCounter = new AtomicInteger(0);

    private ApplicationContext context;


    private AtomicLong processed;
    private AtomicLong saved;
    private AtomicLong successed;
    private AtomicLong failed;

    private Location currentLocation;
    private Account currentAccount;

//    public LinkedInDataProvider(IAccountRepository accountRepository) {
//        this.accountRepository = accountRepository;
//        config = readGroovyConfig(null);
//        String loginUsername = config.get("autolinkedin.username").toString();
//
//        currentAccount = accountRepository.getAccountByUsername(loginUsername);
//        if (currentAccount == null) {
    //create new account
//            String loginPassword = config.get("autolinkedin.password").toString();
//            String loginCheck = config.get("autolinkedin.user_caption").toString();
//            int executionLimit = (int) config.get("autolinkedin.execution_limit");
//            int grabbingLimit = (int) config.get("autolinkedin.grabbing_limit");


//            currentAccount = new Account();
//            currentAccount.setUsername(loginUsername);
//            currentAccount.setPassword(loginPassword);
//            currentAccount.setFirst(config.get("autolinkedin.user_first").toString());
//            currentAccount.setLast(config.get("autolinkedin.user_last").toString());
//
//            currentAccount.setExecutionLimit(executionLimit);
//            currentAccount.setGrabbingLimit(grabbingLimit);
//            accountRepository.save(currentAccount);
//        }

//        executionLimit = currentAccount.getExecutionLimit();
//        grabbingLimit = currentAccount.getGrabbingLimit();
//    }

    private static void display(WebElement result) {
        System.out.println(result.getText());
    }

    public SeleniumUtils getUtils() {
        return utils;
    }

    public void setUtils(SeleniumUtils utils) {
        this.utils = utils;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private Logger createLogger() {

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        if (ctx.hasLogger("memory")) {
            // returns logger if we already have it configured
            return ctx.getLogger("memory");
        }
        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %level [%t] [%c] [%M] [%l] - %msg%n").build();

        WriterAppender writerAppender = WriterAppender.newBuilder().setName("writeLogger").setTarget(stringWriter)
                .setLayout(layout).build();
        writerAppender.start();
        config.addAppender(writerAppender);

        AppenderRef ref = AppenderRef.createAppenderRef("writeLogger", null, null);
        AppenderRef[] refs = new AppenderRef[]{ref};

        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.INFO, "memory", null, refs, null, config,
                null);

        loggerConfig.addAppender(writerAppender, null, null);
        config.addLogger("memory", loggerConfig);
        ctx.updateLoggers();

        return ctx.getLogger("memory");

    }


    public boolean isContainsSales(String contactURL) {
        if (contactURL.contains("/sales")) {
            driver.get(contactURL);
            return true;
        } else {
            driver.get(contactURL);
            return false;
        }
    }

    public void loginToAccount() throws InterruptedException {
        driver.get(globalProperties.getLinkedinLink());
        Thread.sleep(8000);

        if (!checkIfUserIsloggedIn(false)) {
            logger.info("Fill Login form: ");

            WebElement login = utils.findElementsAndGetFirst(By.name("session_key"), null);
            if(login != null){
                login.sendKeys(currentAccount.getUsername());
                Thread.sleep(3000);

                WebElement pwd = utils.findElementsAndGetFirst(By.name("session_password"), null);
                if(pwd != null){
                    pwd.sendKeys(currentAccount.getPassword());
                    Thread.sleep(3000);

                    WebElement in = utils.findElementsAndGetFirst(By.id("login-submit"), null);
                    if(in != null){
                        in.click();
                        Thread.sleep(8000);
                        if (!checkIfUserIsloggedIn(false)){
                            throw new InterruptedException("Invalid login or password");
                        }
                    }
                }
            } else {
                throw new InterruptedException("Login page not found");
            }
        }

    }

    public boolean checkIfUserIsloggedIn(boolean showErrors) {
        List<WebElement> userProfile = driver.findElements(By.xpath("//*[contains(@id, 'profile-nav-item')]"));
        if (!userProfile.isEmpty()) {
            logger.info("logged in successfully");
            return true;
        } else {
            if (showErrors) {
                logger.info("Error on login with " + currentAccount.getUsername());
                throw new RuntimeException("Can't login with specified user");
            }
            return false;
        }
    }

    private boolean connectAndSendMessagesToSales(String message) throws InterruptedException {
        logger.info("Openning popup to connect to current user");
        Thread.sleep(4000);
        WebElement popupBlock = utils.findElementsAndGetFirst(By.xpath("//div//button[contains(@class, 'profile-topcard-actions__overflow-toggle')]"), null);
        if(popupBlock != null){
            popupBlock.click();
            Thread.sleep(2000);
            WebElement connectButton = utils.findElementsAndGetFirst(By.xpath("//ul[contains(@class, 'profile-topcard-actions__overflow-dropdown')]//a[contains(@data-control-name, 'connect')]"), null);
            if (connectButton != null) {
                connectButton.click();
                Thread.sleep(4000);
                WebElement customMessageTextArea = utils.findElementsAndGetFirst(By.xpath("//div[contains(@class, 'connect-cta-form__content-container')]//textarea[contains(@placeholder,'custom message')]"), null);
                if (customMessageTextArea != null) {
                    customMessageTextArea.sendKeys(message);
                    Thread.sleep(6000);
                    WebElement sendButton = utils.findElementsAndGetFirst(By.xpath("//button[text() = 'Send Invitation']"), null);
                    if (sendButton != null) {
                        sendButton.click();
                        Thread.sleep(2000);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean connectTo() throws InterruptedException {
        logger.info("Openning popup to connect to current user");
        Thread.sleep(2000);

        this.getNameToMessages();
        List<WebElement> connectButtons = utils.fluentWait(By.xpath("//li-icon[contains(@class,'profile-actions__overflow')]"));
        List<WebElement> connectButton = utils.fluentWait(By.xpath("//span[contains(@class,'profile-actions__label') and contains(text(),'Connect')]"));
        if (connectButtons.size() > 0 || connectButton.size() > 0) {
            if (connectButton.size() == 0) {
                logger.info("Clicking on More... button");
                WebElement moreButton = connectButtons.get(0);
                utils.mouseMoveToElement(moreButton);
                Thread.sleep(1000);
                moreButton.click();
                Thread.sleep(1500);

                connectButton = utils.fluentWait(By.xpath("//span[contains(@class,'profile-actions__label') and contains(text(),'Connect')]"));
            }
            if (connectButton.size() > 0) {
                logger.info("Clicking on Connect button");
                connectButton.get(0).click();
                Thread.sleep(6000);

                //check if connected successfully
                logger.info("Connect popUp opened successfully");
                return true;
            }
        }

        logger.info("Error opening Connect popUp");
        return false;
    }

    private void getNameToMessages() {
        int firstNameIndex = 0;
        List<WebElement> names = utils.fluentWait(By.className("pv-top-card-section__name"));
        if (!CollectionUtils.isEmpty(names)) {
            String allNames = names.get(0).getText();
            contactNameParsedFromSite = getNameByIndex(allNames, firstNameIndex);
        }
    }

    private String getNameByIndex(String inputNames, int index) {
        String names[] = inputNames.split(" ");
        return names[index];
    }

    public boolean sendMessage(String text) throws InterruptedException {
        logger.info("Sending text message to user");
        Thread.sleep(2000);

        List<WebElement> sendButtons = utils.fluentWait(By.xpath("//div[contains(@class, 'send-invite')]/button[contains(@class,'button-secondary-large mr1')]"));
        if (sendButtons.isEmpty()) {
            logger.info("Can't find 'Add a note' button");
            return false;
        }

        List<WebElement> customMessageEmail = utils.fluentWait(By.xpath("//input[@type='email' and @id='email' and @name='email' and contains(@class,'text-field')]"));
        if (!customMessageEmail.isEmpty()) {
            logger.info("Can't send message without email");
            emailRequired = true;
            return false;
        }


        WebElement addANoteButton = sendButtons.get(0);
        utils.mouseMoveToElement(addANoteButton);
        Thread.sleep(1000);
        logger.info("Clicking on Add a Note button");
        addANoteButton.click();
        Thread.sleep(4000);

        List<WebElement> customMessageTextAreas = utils.fluentWait(By.xpath("//textarea[contains(@class,'custom-message')]"));
        if (customMessageTextAreas.isEmpty()) {
            logger.info("Can't find 'Include a personal message' textarea");
            return false;
        }

        Thread.sleep(2500);

        WebElement customMessageTextArea = customMessageTextAreas.get(0);
        logger.info("Typing message text:" + text);
        customMessageTextArea.sendKeys(text);

        List<WebElement> customMessageSubmitButtons = utils.fluentWait(By.xpath("//div[contains(@class, 'send-invite')]/button[contains(@class,'button-primary-large ml1')]"));
        if (customMessageSubmitButtons.isEmpty()) {
            logger.info("Can't find 'Send invitation' button");
            return false;
        }
        WebElement customMessageSubmitButton = customMessageSubmitButtons.get(0);
        utils.mouseMoveToElement(customMessageSubmitButton);
        Thread.sleep(2000);
        logger.info("Clicking on 'Send invitation' button");
        customMessageSubmitButton.click();
        Thread.sleep(5000);

        //check if message was sent

        logger.info("Invitation was send successfully");
        return true;
    }

    public String getMessage(String template, Map context) {
        return readGroovyConfig(context).get("autolinkedin." + template).toString();
    }

    public Map readGroovyConfig(Map<String, Object> context) {
        if (context == null) {
            context = Collections.<String, Object>emptyMap();
        }

        // Read in 'config.groovy'
        try {
            ConfigSlurper confSl = new ConfigSlurper();
            confSl.setBinding(context);

            ConfigObject conf = confSl.parse(new File("resources/config.groovy").toURI().toURL());
            return conf.flatten();
        } catch (MalformedURLException e) {
            logger.info("Can'r read resources/config.groovy file");

        }

        throw new RuntimeException("Error reading config file");
    }

    //OLD WORKED
    public void connection(Long processingReportId, Assignment assignment) {
        currentAccount = accountRepository.getAccountByUsername(assignment.getAccount().getUsername());
        executed = new AtomicInteger(0);

        while (executed.get() <= assignment.getCountMessages()) {
            Assignment assignmentForCheckStatus = assignmentRepository.getById(assignment.getId());
            if (assignmentForCheckStatus.getStatus() == Status.STATUS_SUSPENDED) {
                logoutWithQuitDriver();
                return;
            }

            LinkedInContact contact = contactRepositoryCustom.getNextAvailableContact(assignment);
            if (contact == null) {
                logger.info("CONTACT IS NULL");
                this.setStatusToAssignmentAndSave(assignment.getId(), Status.STATUS_ASLEEP);
                logoutWithQuitDriver();
                return;
            } else {
                boolean sendingResult;
                log = createLogger();
                StringBuffer buf = stringWriter.getBuffer();
                buf.setLength(0);
                try {

                    try {
                        loginToAccount();
                    } catch (InterruptedException e) {
                        this.setStatusToAssignmentAndSave(assignment.getId(), Status.STATUS_ERROR);
                        logoutWithQuitDriver();
                        return;
                    }

                    emailRequired = false;

                    boolean salesMessage;
                    salesMessage = this.isContainsSales(contact.getLinkedin());
                    String message = assignment.getMessage().replace("%%", contact.getFirstName() != null ? contact.getFirstName() : "");
                    if (salesMessage) {
                        sendingResult = this.connectAndSendMessagesToSales(message);
                    } else {
                        sendingResult = this.connectTo() && this.sendMessage(message);
                    }

                    contactRepositoryCustom.updateContactStatus(assignment, contact, currentAccount, sendingResult ? LinkedInContact.STATUS_PROCESSED : LinkedInContact.STATUS_ERROR,
                            "", stringWriter.toString(), processingReportId);
                } catch (InterruptedException e) {
                    contactProcessingRepository.save(new ContactProcessing()
                            .setAccount(currentAccount)
                            .setStatus(ContactProcessing.STATUS_ERROR))
                            .setContact(contactRepository.getById(contact.getId()));
                    e.printStackTrace();
                }
                executed.incrementAndGet();
            }
        }

        if (executed.get() >= assignment.getCountMessages()) {
            //exiting from spring app
            this.setStatusToAssignmentAndSave(assignment.getId(), Status.STATUS_FINISHED);
            logoutWithQuitDriver();
        }
    }

    //    executeSearchContacts
    public void grabbing(Long assignmentId, Long processingReportId, String location, String position, String inductries, Account account) {
        grabbed = new AtomicInteger(0);

        currentAccount = accountRepository.getAccountByUsername(account.getUsername());

        currentLocation = locationRepository.getLocationByLocation(location);
        if (currentLocation == null) {
            //lets create new location
            currentLocation = new Location(location);
            locationRepository.save(currentLocation);

        }
        boolean sendingResult = false;
        String error = null;

        log = createLogger();

        StringBuffer buf = stringWriter.getBuffer();


        try {
            loginToAccount();
            fillSearchForm(location, position, new ArrayList<>(Collections.singletonList(inductries)));
            Thread.sleep(15000);
            String result = "";
            try {
                result = utils.fluentWait(By.xpath("//h3[contains(@class, 'search-results__total')]")).get(0).getText();
                if (result != null) {
                    while (getCountSymbolFromString(result, ',') >= 2) {
                        fillSearchForm(location, position, new ArrayList<>(Collections.singletonList(inductries)));
                        Thread.sleep(15000);
                        result = utils.fluentWait(By.xpath("//h3[contains(@class, 'search-results__total')]")).get(0).getText();
                    }
                }
            } catch (Exception e) {
                logger.info("Result is empty");
            }
            Thread.sleep(20000);

            boolean pagesAvailable = true;
            boolean canExecute = true;
            Assignment assignmentForPage = assignmentRepository.getById(assignmentId);
            if (assignmentForPage.getPage() == null) {
                assignmentForPage.setPage(0);
            }
            if (assignmentForPage.getPage().intValue() != 0) {
                goToTheNextPage(assignmentForPage.getPage(), assignmentForPage.getId());
            }
            assignmentForPage.setCountsFound(getIntFromString(result != null ? result : ""));
            assignmentRepository.save(assignmentForPage);

            while (pagesAvailable && canExecute) {
                buf.setLength(0);
                canExecute = checkGrabbingLimit(currentAccount);
                pagesAvailable = getPageAvailable();

                List<LinkedInContact> contacts = extractLinkedInContactsOnPage();
                if (contacts != null) {
                    contacts.stream().map(linkedInContact -> {
                        linkedInContact.setIndustries(inductries);
                        return linkedInContact;
                    }).collect(Collectors.toList());
                }
                contactRepositoryCustom.saveNewContactsBatch(account, assignmentId, contacts, processingReportId, buf.toString());
                String nextPageXpath = "//ol[contains(@class,'results-paginator')]/li/ol/li[contains(@class,'active')]/following-sibling::li[1]/button";
                List<WebElement> nextPage = utils.fluentWait(By.xpath(nextPageXpath));
                if (nextPage.isEmpty()) {
                    nextPage = utils.fluentWait(By.xpath("//ul[contains(@class, 'artdeco')]/li[contains(@class,'active')][contains(@class,'active')]/following-sibling::li[1]/button"));
                    if (nextPage.isEmpty()) {
                        log.error("No next page found");
                        pagesAvailable = false;
                    }
                }
                goToTheNextPage(0, assignmentId);
                grabbed.incrementAndGet();
            }
            this.setStatusToAssignmentAndSave(assignmentId, Status.STATUS_FINISHED);
            logoutWithQuitDriver();
        } catch (InterruptedException | RuntimeException e) {
            error = e.getMessage();
            System.out.println("Error:" + error);
            e.printStackTrace();
        }
    }

    public void grabbingSales(Long assignmentId, Account account) {
        Assignment assignment = assignmentRepository.getById(assignmentId);
        grabbed = new AtomicInteger(0);
        currentAccount = accountRepository.getAccountByUsername(account.getUsername());
        currentLocation = locationRepository.getLocationByLocation(assignment.getFullLocationString());
        if (currentLocation == null) {
            currentLocation = new Location(assignment.getFullLocationString());
            locationRepository.save(currentLocation);
        }
        try {
            try {
                loginToAccount();
            }catch (InterruptedException e){
                this.setStatusToAssignmentAndSave(assignmentId, Status.STATUS_ERROR);
                return;
            }

            driver.get(globalProperties.getLinkedinSalesLink());
            Thread.sleep(8000);
            logger.info("Click SearchForLeads successfully");

            if (!CollectionUtils.isEmpty(assignment.getHeadcounts())) {
                for (CompanyHeadcount headcount : assignment.getHeadcounts()) {
                    fillSalesSearchForm(assignment, headcount);
                    parsingAndSavingContacts(assignment, account, headcount);

                    Assignment assignmentForCheckStatus = assignmentRepository.getById(assignment.getId());
                    if (assignmentForCheckStatus.getStatus() == Status.STATUS_SUSPENDED) {
                        return;
                    }

                    driver.get(globalProperties.getLinkedinSalesLink());
                    this.setStatusToAssignmentAndSave(assignmentId, Status.STATUS_FINISHED);
                    Thread.sleep(8000);
                }
            } else {
                fillSalesSearchForm(assignment, null);
                parsingAndSavingContacts(assignment, account, null);

                Assignment assignmentForCheckStatus = assignmentRepository.getById(assignment.getId());
                if (assignmentForCheckStatus.getStatus() == Status.STATUS_SUSPENDED) {
                    return;
                }

                this.setStatusToAssignmentAndSave(assignmentId, Status.STATUS_FINISHED);
            }

        } catch (InterruptedException | RuntimeException e) {
            System.out.println("Error:" + e.getMessage());
            e.printStackTrace();
        } finally {
            logoutWithQuitDriver();
        }
    }

    private void logoutWithQuitDriver() {
        driver.get(globalProperties.getLinkedinLink());
//        driver = webDriverFactoryBean.getNewDriver(driver);
//        utils.setDriver(driver);
    }

    private void setStatusToAssignmentAndSave(Long assignmentId, Status status){
        Assignment assignmentDB = assignmentRepository.getById(assignmentId);
        assignmentDB.setStatus(status);
        assignmentRepository.save(assignmentDB);
    }

    private void parsingAndSavingContacts(Assignment assignment, Account account, CompanyHeadcount headcount) throws InterruptedException {

        boolean pagesAvailable = true;
        boolean canExecute = true;

        while (pagesAvailable && canExecute) {
            Assignment assignmentForCheckStatus = assignmentRepository.getById(assignment.getId());
            if (assignmentForCheckStatus.getStatus() == Status.STATUS_SUSPENDED) {
                return;
            }
            StringBuffer buf = stringWriter.getBuffer();
            buf.setLength(0);
            canExecute = checkGrabbingLimit(currentAccount);
            List<LinkedInContact> contacts = extractLinkedInContactsOnPageSales();
            if (contacts != null) {
                contacts.stream().map(linkedInContact -> {
                    linkedInContact.setIndustries(assignment.getIndustries());
                    linkedInContact.setRole(assignment.getPosition());
                    linkedInContact.setHeadcount(headcount);
                    return linkedInContact;
                }).collect(Collectors.toList());
            }

            List<ProcessingReport> reports = assignment.getProcessingReports();
            contactRepositoryCustom.saveNewContactsBatch(account, assignment.getId(),
                    contacts, reports.get(reports.size() - 1).getId(), buf.toString());

            pagesAvailable = goToTheNextPageSales();
            if (pagesAvailable) {
                Assignment byId = assignmentRepository.getById(assignment.getId());
                assignmentRepository.save(byId.incrementPage());
            }
            grabbed.incrementAndGet();
        }
    }

    private void fillSalesSearchForm(Assignment assignment, CompanyHeadcount headcount) throws InterruptedException {
        log.info("Start fillSalesSearchForm");
        Thread.sleep(5000);
        List<WebElement> search = driver.findElements(By.xpath("//div[contains(@class, 'flex pt4 ph4 pb3 flex-wrap')]"));
        if(!search.isEmpty()){
            selectRelationShip(search.get(2));
            if (assignment.getFullLocationString() != null) {
                writeLocation(search.get(1), assignment);
                logger.info("Location " + assignment.getFullLocationString() + " added");
            }

            if (assignment.getIndustries() != null) {
                writeIndustries(search.get(3), assignment);
                logger.info("Industries " + assignment.getIndustries() + " added");
            }

            if (assignment.getPosition() != null) {
                writePosition(search.get(12), assignment);
                logger.info("Position " + assignment.getPosition() + " added");
            }

            if (assignment.getGroups() != null) {
                writeGroups(search.get(19), assignment);
                logger.info("Group added");
            }

            if (headcount != null) {
                writeCompanyHeadcount(search.get(15), assignment, headcount);
                logger.info("Headcount" + headcount.toString() + " added");
            }

            setCountFoundContactsToAssignment(assignment);
        }
    }

    private void clickSearchContactsButton() throws InterruptedException {
        WebElement searchButton = utils.findElementsAndGetFirst(By.xpath("//button[contains(@data-control-name,'advanced_search_profile')]"), null);
        if (searchButton != null) {
            searchButton.click();
            Thread.sleep(5000);
        }else{
            log.error("Can't find Search Contacts Button");
            return;
        }
    }

    private void writeCompanyHeadcount(WebElement headcountElement, Assignment assignment, CompanyHeadcount headcount) throws InterruptedException {
        headcountElement.click();
        Thread.sleep(3000);
        List<WebElement> elements = headcountElement.findElements(By.xpath("//ol[contains(@class, 'search-filter-typeahead__list')]//a"));
        if(!elements.isEmpty()){
            WebElement element = elements.stream().filter(e -> e.getText().equalsIgnoreCase(headcount.getHeadcount())).findFirst().orElse(null);
            if(element != null){
                element.click();
                Thread.sleep(4000);
            }

            WebElement selectedValue = utils.findElementsAndGetFirst(By.xpath("//ul[contains(@class, 'ph4 pb2 list-style-none flex flex-wrap')]//li"), headcountElement);
            if(selectedValue == null){
                assignment.setStatus(Status.STATUS_ERROR);
                assignmentRepository.save(assignment);
                throw new InterruptedException("Invalid headcount: " + headcount.toString());
            }
        }
    }


    private void writeGroups(WebElement groupElement, Assignment assignment) throws InterruptedException {
        groupElement.click();
        WebElement searchField = utils.findElementsAndGetFirst(By.xpath("//input[contains(@placeholder, 'Find people in groups')]"), groupElement);
        if (searchField != null) {
            for(Group group : assignment.getGroups()) {
                searchField.click();
                searchField.sendKeys(group.getName());
                Thread.sleep(3000);
                WebElement selectedValue = utils.findElementsAndGetFirst(By.xpath("//ol[contains(@class, 'search-filter-typeahead__list')]//a"), groupElement);
                if(selectedValue != null){
                    selectedValue.click();
                    Thread.sleep(3000);
                }else{
                    assignment.setStatus(Status.STATUS_ERROR);
                    assignmentRepository.save(assignment);
                    throw new InterruptedException("Invalid group: " + group.getName());
                }
            }
        }
    }

    private void selectRelationShip(WebElement relationShipElement) throws InterruptedException {
        relationShipElement.click();
        Thread.sleep(2000);
        WebElement element = utils.findElementsAndGetFirst(
                By.xpath("//ol[contains(@class, 'search-filter-typeahead__list')]//a"), relationShipElement);
        if(element != null) {
            element.click();
            Thread.sleep(3000);
        }
        element = utils.findElementsAndGetFirst(
                By.xpath("//ol[contains(@class, 'search-filter-typeahead__list')]//a"), relationShipElement);

        if(element != null){
            element.click();
            Thread.sleep(3000);
        }
        element = utils.findElementsAndGetByIndex(
                By.xpath("//ol[contains(@class, 'search-filter-typeahead__list')]//a"), relationShipElement, 1);

        if(element != null){
            element.click();
            Thread.sleep(3000);
        }
    }


    private void writeLocation(WebElement locationElement, Assignment assignment) throws InterruptedException {
        locationElement.click();
        Thread.sleep(3000);
        WebElement searchField = utils.findElementsAndGetFirst(By.xpath("//input[contains(@placeholder, 'Add locations')]"), locationElement);
        if (searchField != null) {
            searchField.click();
            searchField.sendKeys(assignment.getFullLocationString());
            Thread.sleep(3000);

            WebElement selectedValue = utils.findElementsAndGetFirst(By.xpath("//ol[contains(@class, 'search-filter-typeahead__list')]//a"), locationElement);
            if(selectedValue != null){
                selectedValue.click();
                Thread.sleep(3000);
            }else{
                assignment.setStatus(Status.STATUS_ERROR);
                assignmentRepository.save(assignment);
                throw new InterruptedException("Invalid location: " + assignment.getFullLocationString());
            }
        }
    }

    private void writeIndustries(WebElement industryElement, Assignment assignment) throws InterruptedException {
        industryElement.click();
        Thread.sleep(3000);
        WebElement searchField = utils.findElementsAndGetFirst(By.xpath("//input[contains(@placeholder, 'Add industries')]"), industryElement);
        if (searchField != null) {
            searchField.click();
            searchField.sendKeys(assignment.getIndustries());
            Thread.sleep(3000);

            WebElement selectedValue = utils.findElementsAndGetFirst(By.xpath("//ol[contains(@class, 'search-filter-typeahead__list')]//a"), industryElement);
            if(selectedValue != null){
                selectedValue.click();
                Thread.sleep(3000);
            }else{
                assignment.setStatus(Status.STATUS_ERROR);
                assignmentRepository.save(assignment);
                throw new InterruptedException("Invalid industry: " + assignment.getIndustries());
            }
        }
    }

    private void writePosition(WebElement positionElement, Assignment assignment) throws InterruptedException {
        positionElement.click();
        WebElement searchField = utils.findElementsAndGetFirst(By.xpath("//input[contains(@placeholder, 'Add titles')]"), positionElement);
        if (searchField != null) {
            searchField.click();
            searchField.sendKeys(assignment.getPosition());
            searchField.sendKeys(Keys.ENTER);
            Thread.sleep(3000);

            WebElement selectedValue = utils.findElementsAndGetFirst(By.xpath("//ul[contains(@class, 'ph4 pb2 list-style-none flex flex-wrap')]//li"), positionElement);
            if(selectedValue == null){
                assignment.setStatus(Status.STATUS_ERROR);
                assignmentRepository.save(assignment);
                throw new InterruptedException("Invalid position: " + assignment.getPosition());
            }
        }
    }

    private void setCountFoundContactsToAssignment(Assignment assignment) throws InterruptedException {
        String count = utils.findTextOrExtractValue(
                By.xpath("//span[contains(@class, 'ph2')]//b"), null, null).orElse(null);
        Assignment assignmentDb = assignmentRepository.getById(assignment.getId());
        if (count != null && !count.equals("0")) {
            count = count.replaceAll("[^0-9]+", "");
            if (assignmentDb.getCountsFound() != null) {
                assignmentDb.setCountsFound(assignmentDb.getCountsFound() + Integer.valueOf(count));
            } else {
                assignmentDb.setCountsFound(Integer.valueOf(count));
            }
            assignmentRepository.save(assignmentDb);
            clickSearchContactsButton();
        }
    }

    private Integer getIntFromString(String result) {
        return result.equals("") ? 0 : extractDigits(result.replace(",", ""));
    }

    private Integer extractDigits(String src) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
            }
        }
        return Integer.valueOf(builder.toString());
    }


    private int getCountSymbolFromString(String inputString, char symbol) {
        int count = 0;
        for (char element : inputString.toCharArray()) {
            if (element == symbol) ++count;
        }
        return count;
    }

    private boolean getPageAvailable() {
        List<WebElement> resultsPages = utils.fluentWait(By.xpath("//ol[contains(@class,'results-paginator')]/li/ol/li[contains(@class,'active')]"));
//        todo fix it
        if (resultsPages.isEmpty()) {
            resultsPages = utils.fluentWait(By.xpath("//ul[contains(@class, 'artdeco')]/li[contains(@class,'active')]"));
        }
        if (resultsPages.isEmpty()) {
            log.error("No pages found");
            return false;
        }

        return true;
    }

    private boolean checkGrabbingLimit(Account account) {
        if (grabbed.get() >= account.getGrabbingLimit()) {
            log.info("Applivation processed " + grabbed.get() + " pages. No more pages allowed today.");
            System.out.println("Applivation processed " + grabbed.get() + " pages. No more pages allowed today.");

            return false;
        }

        return true;
    }

    private boolean fillSearchForm(String location, String position, List<String> inductries) throws InterruptedException {

        log.info("Start fill search details");

        List<WebElement> searchTypeahead = utils.fluentWait(By.xpath("//div[contains(@class, \"nav-search-typeahead\")]/artdeco-typeahead-deprecated/artdeco-typeahead-deprecated-input/input"));
        if (searchTypeahead.isEmpty()) {
            log.error("Can't find 'Search Type-a-head'");
            return false;
        }

        utils.mouseMoveToElement(searchTypeahead.get(0));
        Thread.sleep(1000);
        searchTypeahead.get(0).click();

        ((JavascriptExecutor) driver).executeScript("window.location='/search/results/people/v2/?origin=DISCOVER_FROM_SEARCH_HOME'");
        log.info("Redirecting to Search Form'");
        Thread.sleep(7000);

        List<WebElement> allFiltersButtons = utils.fluentWait(By.xpath("//button[contains(@class,'search-filters-bar') and contains(@class,'all-filters')]"));
        if (allFiltersButtons.isEmpty()) {
            log.error("Can't find 'All Filters Button'");
            return false;
        }

        allFiltersButtons.get(0).click();
        Thread.sleep(7000);


        //fill position
        List<WebElement> titleFields = utils.fluentWait(By.id("search-advanced-title"));
        if (titleFields.isEmpty()) {
            log.error("Can't find 'Location Filter input'");
            return false;
        }

        WebElement titleField = titleFields.get(0);
        utils.mouseMoveToElement(titleField);
        Thread.sleep(3000);
        titleField.click();
        Thread.sleep(3000);
        titleField.sendKeys(position);
        Thread.sleep(1000);

        //fill network fields
        List<WebElement> searchNetworkFields = utils.fluentWait(By.xpath("//li[contains(@class,'search-facet__value')]"));
        if (searchNetworkFields.isEmpty()) {
            log.error("Can't find 'Network checkboxes'");
            return false;
        }
        for (int i = 0; i < 3; i++) {
            WebElement network = searchNetworkFields.get(i);
            utils.mouseMoveToElement(network);
            Thread.sleep(3000);
            network.click();
            Thread.sleep(1000);
        }
//        for (WebElement network : searchNetworkFields) {
//            utils.mouseMoveToElement(network);
//            Thread.sleep(1000);
//            network.click();
//            Thread.sleep(1000);
//        }

        //search-filters-bar__all-filters button-tertiary-medium-muted mr3
/*
        List<WebElement> locationButtons = utils.fluentWait(By.xpath("//button[contains(@aria-controls,'locations-facet-values')]//h3"));
        if (locationButtons.isEmpty()) {
            log.error("Can't find 'Location Button'");
            return false;
        }

        WebElement locationButton = locationButtons.get(0);
        utils.mouseMoveToElement(locationButton);
        Thread.sleep(1000);
        locationButton.click();
        Thread.sleep(2000);
*/
        List<WebElement> searchLocationFields = utils.fluentWait(By.xpath("//input[contains(@placeholder,'Add a location')]"));
        if (searchLocationFields.isEmpty()) {
            log.error("Can't find 'Location Filter input'");
            return false;
        }

        WebElement searchLocationFilter = searchLocationFields.get(0);
        utils.mouseMoveToElement(searchLocationFilter);
        Thread.sleep(1000);
        searchLocationFilter.click();
        Thread.sleep(4000);
        searchLocationFilter.sendKeys(location);
        Thread.sleep(4000);
        Actions builder = new Actions(driver);
        builder.moveToElement(searchLocationFilter).moveByOffset(0, 20).click().build().perform();


        Thread.sleep(4000);
        //lets check if location is added
        List<WebElement> searchLocationResults = utils.fluentWait(By.xpath("//label[contains(@for,'GeoRegion')]"));
        ///text()[contains(.,'" + location + "')]
        searchLocationResults.forEach(LinkedInDataProvider::display);

        if (searchLocationResults.stream().anyMatch(elem -> elem.getText().contains(location))) {
            log.error("Search result have no location. Pleace, check");
            //return false;
        }

        log.info("Search location added successfully");


        List<WebElement> searchIndustryFields = utils.fluentWait(By.xpath("//input[contains(@placeholder,'Add an industry')]"));
        if (searchIndustryFields.isEmpty()) {
            log.error("Can't find 'Industry Filter input'");
            return false;
        }

        for (String industry : inductries) {

            WebElement searchIndustryFilter = searchIndustryFields.get(0);
            utils.mouseMoveToElement(searchIndustryFilter);
            Thread.sleep(4000);
            searchIndustryFilter.click();
            Thread.sleep(2000);
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight)");
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(40, document.body.scrollHeight)");
            searchIndustryFilter.clear();
            Thread.sleep(8000);
            searchIndustryFilter.sendKeys(industry);
            Thread.sleep(8000);
            builder = new Actions(driver);
            builder.moveToElement(searchIndustryFilter).moveByOffset(0, 20).click().build().perform();

//            Thread.sleep(9000);

//            lets check if location is added
//            List<WebElement> searchIndustryResults = utils.fluentWait(By.xpath("//label[contains(@for,'facetIndustry') and contains(text()," + industry + ")]"));
//            if (searchIndustryResults.isEmpty()) {
//
//                log.error("Search result have no industry:" + industry + ". Pleace, check");
//                return false;
//            }

            log.info("Search industry " + industry + " added successfully");
        }

        Thread.sleep(10000);
        List<WebElement> applyButtons = utils.fluentWait(By.xpath("//button[contains(@data-control-name,'all_filters_apply')]"));
        if (applyButtons.isEmpty()) {
            log.error("Can't find Apply Button");
            return false;
        }

        applyButtons.get(0).click();

        log.info(" Form sucessfully submitted");

        return true;
    }


    private void injectScriptFile(String scriptFile) {
        InputStream input;
        try {
            input = new FileInputStream(scriptFile);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            String execution = null; //"console.log('123')";

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.getEncoder().withoutPadding().encodeToString(buffer);

            ((JavascriptExecutor) driver)
                    .executeScript("window.atob('" + encoded + "');" + execution + ";");


        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private LinkedInContact extractLinkedInContact(WebElement result) throws InterruptedException {

        String name = utils.findTextAndExtractValue(By.xpath(".//span[contains(@class,'name') and contains(@class,'actor-name')]"), result).orElse(null);
        String companyAndPosition = utils.findTextAndExtractValue(By.xpath("./p[contains(@class,'subline-level-1')]"), result).orElse(null);
        String location = utils.findTextAndExtractValue(By.xpath("./p[contains(@class,'subline-level-2')]"), result).orElse(null);
        String snippets = utils.findTextAndExtractValue(By.xpath("./p[contains(@class,'search-result') and contains(@class,'snippets')]"), result).orElse("");
        String link = utils.findTextAndExtractValue(By.xpath("./a[contains(@class,'search-result') and contains(@class,'result-link')]"), result, "href").orElse(null);

        int status = ContactProcessing.STATUS_GRABBED;

        String company = null;
        String position = null;

        snippets = Jsoup.parse(snippets).text();

        if (snippets.contains(":")) {
            snippets = snippets.substring(snippets.indexOf(":") + 1).trim();
        }

        String[] parts = companyAndPosition.split(" at ");
        if (parts.length == 2) {
            position = parts[0];
            company = parts[1];
        } else {
            company = companyAndPosition;
            parts = snippets.split(" at ");
            if (parts.length == 2) {
                position = parts[0];
                company = parts[1];
            }
        }

        LinkedInContact contact = new LinkedInContact();

        String nameParts[] = name.split(" ");

        String firstName = nameParts[0];
        String lastName = nameParts[nameParts.length - 1];
        if (firstName.equalsIgnoreCase("LinkedIn")) {
            firstName = null;
            status = LinkedInContact.STATUS_REQUIRE_LOAD_FROM_OTHER_ACCOUNT;
        }

        if (lastName.equalsIgnoreCase("Member")) {
            lastName = null;
            status = LinkedInContact.STATUS_REQUIRE_LOAD_FROM_OTHER_ACCOUNT;
        }

        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setCompanyName(company);
        contact.setRole(position);
        contact.setLinkedin(link);
        contact.setStatus(status);
        contact.setLocation(currentLocation);

        return contact;
    }

    private LinkedInContact extractLinkedInContactSales(WebElement result) throws InterruptedException {

        String name = utils.findTextOrExtractValue(By.xpath(".//dt[contains(@class,'result-lockup__name')]"), result, null)
                .orElse(null);

        if(name.equalsIgnoreCase("LinkedIn Member")){
            return null;
        }

        String company = utils.findTextOrExtractValue(By.xpath(".//span[contains(@class,'result-lockup__position-company')]"), result, null)
                .orElse(null);
        String companyLink = utils.findTextOrExtractValue(By.xpath(".//span[contains(@class,'result-lockup__position-company')]/a"), result, "href").orElse(null);
        String userLink = utils.findTextOrExtractValue(By.xpath(".//dt[contains(@class,'result-lockup__name')]/a"), result, "href").orElse(null);

        int status = ContactProcessing.STATUS_GRABBED;

        String firstName = null;
        String lastName = null;

        if (name != null) {
            String nameParts[] = name.split(" ");

            firstName = nameParts[0];
            lastName = nameParts[nameParts.length - 1];
        } else {
            log.error("ERROR CONTACT");
            return null;
        }

        LinkedInContact contact = new LinkedInContact();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setCompanyName(getCompanyNameFromString(company));
        contact.setLinkedin(userLink);
        contact.setCompanyLinkedin(companyLink);
        contact.setStatus(status);
        contact.setLocation(currentLocation);
        getCompanySiteByLinkedInContact(contact);

        return contact;
    }

    private String getCompanyNameFromString(String fullString) {
        if (fullString == null) {
            return "";
        }
        int index = fullString.indexOf("\n");
        return index != -1 ? fullString.substring(0, index) : fullString;
    }


    private List<LinkedInContact> extractLinkedInContactsOnPage() throws InterruptedException {

        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(4000);
        List<WebElement> searchResults = utils.fluentWait(By.xpath("//div[contains(@class,'search-result') and contains(@class,'info')]"));
        if (searchResults.isEmpty()) {
            log.error("No results found");
            return null;
        }

        List<LinkedInContact> contacts = new LinkedList<>();

        for (WebElement result : searchResults) {
            utils.mouseMoveToElement(result);
            Thread.sleep(1000);

            contacts.add(extractLinkedInContact(result));
        }

        return contacts;
    }

    private List<LinkedInContact> extractLinkedInContactsOnPageSales() throws InterruptedException {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(4000);
        List<WebElement> searchResults = driver.findElements(
                By.xpath("//ol[contains(@class,'search-results__result-list')]//li[contains(@class, 'search-results__result-item')]"));
        if (searchResults.isEmpty()) {
            log.error("No results found");
            return null;
        }

        List<LinkedInContact> contacts = new LinkedList<>();

        for (WebElement result : searchResults) {
            LinkedInContact contact = extractLinkedInContactSales(result);
            if(contact != null){
                contacts.add(contact);
            }

        }

        return contacts;
    }

    private void getCompanySiteByLinkedInContact(LinkedInContact linkedInContact) {
        try {
            List<String> tabs = utils.openTabAndDoRedirect(linkedInContact.getCompanyLinkedin());
            logger.info("Link " + linkedInContact.getCompanyLinkedin() + " opened successfully");
            String companySite = utils.findTextOrExtractValue(By.xpath("//a[@data-control-name='topcard_website']"),
                    null, "href").orElse(null);
            if (companySite != null) {
                linkedInContact.setCompanyWebsite(companySite);
            } else {
                WebElement findedCompany = utils.findElementsAndGetFirst(By.xpath("//ol[contains(@class,'search-results__result-list')]/li"), null);
                if (findedCompany != null) {
                    String linkOnCompany = utils.findTextOrExtractValue(By.xpath(".//dt[contains(@class,'name')]/a"),
                            findedCompany, "href").orElse(null);

                    if (linkOnCompany != null) {
                        linkedInContact.setCompanyLinkedin(linkOnCompany);
                        driver.get(linkedInContact.getCompanyLinkedin());
                        Thread.sleep(6000);

                        companySite = utils.findTextOrExtractValue(By.xpath("//a[@data-control-name='topcard_website']"),
                                null, "href").orElse(null);
                        if (companySite != null) {
                            linkedInContact.setCompanyWebsite(companySite);
                        }
                    }
                }
            }

            driver.close();
            driver.switchTo().window(tabs.get(0));
        } catch (InterruptedException | RuntimeException e) {
            System.out.println("Error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean goToTheNextPageSales() throws InterruptedException {
        WebElement nextPage = utils.findElementsAndGetFirst(By.xpath("//nav[contains(@class, 'search-results__pagination')]//button[contains(@class, 'search-results__pagination-next-button')]"), null);
        if (nextPage != null) {
            if (nextPage.isEnabled()) {
                nextPage.click();
                Thread.sleep(6000);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean goToTheNextPage(int page, Long assignmentId) throws InterruptedException {
        Thread.sleep(2000);
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);
        List<WebElement> resultsPages = utils.fluentWait(By.xpath("//ol[contains(@class,'results-paginator')]/li/ol/li[contains(@class,'active')]"));
        if (resultsPages.isEmpty()) {
            resultsPages = utils.fluentWait(By.xpath("//ul[contains(@class, 'artdeco')]/li[contains(@class,'active')][contains(@class,'active')]/following-sibling::li[1]/button"));
            ;
            if (resultsPages.isEmpty()) {
                log.error("No pages found");
                return false;
            }
        }
        log.info("Pages found");
        if (page == 0) {
            String nextPageXpath = "//ol[contains(@class,'results-paginator')]/li/ol/li[contains(@class,'active')]/following-sibling::li[1]/button";
            List<WebElement> nextPage = utils.fluentWait(By.xpath(nextPageXpath));
            if (nextPage.isEmpty()) {
                nextPage = utils.fluentWait(By.xpath("//ul[contains(@class,'artdeco')]/li[contains(@class,'active')]/following-sibling::li[1]/button"));
                if (nextPage.isEmpty()) {
                    log.error("No next page found");
                    return false;
                }
            }
            String nextPageNum = nextPage.get(0).getText();
            Assignment assignment = assignmentRepository.getById(assignmentId);
            int pageInt;
            try {
                pageInt = Integer.parseInt(nextPageNum) - 1;
                assignment.setPage(pageInt);
            } catch (NumberFormatException e) {
                logger.info(e.getMessage());
            }
            assignmentRepository.save(assignment);
            nextPage.get(0).click();
            Thread.sleep(5000);
            return true;
        }
        if (page != 0) {
            if (paggingCounter.get() > 1000) {
                log.error("Too deep paging paggingCounter = " + paggingCounter.get() + ", but we searching for page #" + page);
                return false;
            }
            WebElement foundPage = checkIfPageLinkDisplayed(page);
            if (foundPage != null) {
                foundPage.click();
                Thread.sleep(5000);
                return true;
            }
            WebElement currentLastPage = getLastPageDisplayed();
            if (currentLastPage == null) {
                log.error("No last page found on current page.");
                return false;
            }
            int lastPageIndex = Integer.parseInt(currentLastPage.getText());
            if (lastPageIndex < page) {
                currentLastPage.click();
                Thread.sleep(5000);
                paggingCounter.incrementAndGet();
                return goToTheNextPage(page, assignmentId);
                //paggingCounter.decrementAndGet();

            } else {
                log.error("Something going wrong lastPageIndex = " + lastPageIndex + ", but we searching for page #" + page);
                return false;
            }
        }
        return false;
    }


    private WebElement checkIfPageLinkDisplayed(int page) {

        String pageXpath = "//ol[contains(@class,'results-paginator')]/li/ol/li/button[contains(text(),'" + page + "')]";
        List<WebElement> nextPage = utils.fluentWait(By.xpath(pageXpath));
        if (nextPage.isEmpty()) {
            log.error("No page # " + page + " found");
            return null;
        }
        return nextPage.get(0);


    }

    private WebElement getLastPageDisplayed() {

        String pageXpath = "//ol[contains(@class,'results-paginator')]/li/ol/li[contains(@class,'active')]/following-sibling::li/button";
        List<WebElement> nextPage = utils.fluentWait(By.xpath(pageXpath));
        if (nextPage.isEmpty()) {
            log.error("No pages found");
            return null;
        }
        return nextPage.get(nextPage.size() - 1);

    }

}