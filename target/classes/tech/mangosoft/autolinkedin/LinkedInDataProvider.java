package tech.mangosoft.autolinkedin;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import tech.mangosoft.autolinkedin.db.*;
import tech.mangosoft.selenium.SeleniumUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


//CREATE TRIGGER creation_time_calc BEFORE INSERT ON linkedin_contacts
// FOR EACH ROW	SET NEW.creation_time = CURRENT_TIMESTAMP;



@Component("linkedInDataProvider")
@Scope("prototype")
@Lazy(value = true)

public class LinkedInDataProvider implements ApplicationContextAware {

    private static Logger log = LogManager.getRootLogger();

    private static StringWriter stringWriter = new StringWriter();

    @Autowired
    private WebDriver driver;

    @Autowired
    private SeleniumUtils utils;

    @Autowired
    ILinkedInContactRepositoryCustom contactRepository;

    @Autowired
    ILocationRepository locationRepository;


    IAccountRepository accountRepository;

//    private String loginUsername;

//    private String loginPassword;

//    private String loginCheck;

    private int executionLimit;
    private int grabbingLimit;
    private boolean emailRequired = false;

    private AtomicInteger executed = new AtomicInteger(0);
    private AtomicInteger grabbed = new AtomicInteger(0);
    private AtomicInteger paggingCounter = new AtomicInteger(0);

    private ApplicationContext context;

    final Map config;

    private AtomicLong processed;
    private AtomicLong saved;
    private AtomicLong successed;
    private AtomicLong failed;

    private Location currentLocation;
    private Account currentAccount;

    public LinkedInDataProvider(IAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        config = readGroovyConfig(null);
        String loginUsername = config.get("autolinkedin.username").toString();

        currentAccount = accountRepository.getAccountByUsername(loginUsername);
        if (currentAccount == null) {
            //create new account
            String loginPassword = config.get("autolinkedin.password").toString();
            String loginCheck = config.get("autolinkedin.user_caption").toString();
            int executionLimit = (int) config.get("autolinkedin.execution_limit");
            int grabbingLimit = (int) config.get("autolinkedin.grabbing_limit");


            currentAccount = new Account();
            currentAccount.setUsername(loginUsername);
            currentAccount.setPassword(loginPassword);
            currentAccount.setFirst(config.get("autolinkedin.user_first").toString());
            currentAccount.setLast(config.get("autolinkedin.user_last").toString());

            currentAccount.setExecutionLimit(executionLimit);
            currentAccount.setGrabbingLimit(grabbingLimit);
            accountRepository.save(currentAccount);
        }

        executionLimit = currentAccount.getExecutionLimit();
        grabbingLimit = currentAccount.getGrabbingLimit();
    }

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


    public void searchGoogle(String name, String contactURL) throws InterruptedException {
        //going google
        log.info("Looking at google for link to linked in: " + name);
        WebElement element = utils.searchGoogle(name + " site:linkedin.com", "linkedin.com/in", false);

        //if no element was found - just
        if (element == null) {
            driver.get(contactURL);
        }
    }

    public void loginTo() throws InterruptedException {
        driver.get("http://www.linkedin.com");
        Thread.sleep(7000);


        if (!checkIfUserIsloggedIn(false)) {

            log.info("Fill Login form: ");

            //<a class="nav-link" href="https://www.linkedin.com/uas/login?session_redirect=&amp;goback=&amp;trk=hb_signin" title="Sign in">Sign in</a>
/*
            Thread.sleep(5000);
            //check if user is logged in
            List<WebElement> loginButtons = utils.fluentWait(By.partialLinkText("Sign in"));

            if (loginButtons.size()==0){
                loginButtons = utils.fluentWait(By.partialLinkText("Sign In"));
            }

            if (loginButtons.size() == 0) {
                checkIfUserIsloggedIn(true);
            } else {
*/
            //            loginButtons.get(0).click();
            //          Thread.sleep(5000);

            //if user is not logged in - plz login
            WebElement login = utils.fluentWait(By.name("session_key")).get(0);
            login.sendKeys(currentAccount.getUsername());

            Thread.sleep(3000);

            WebElement pwd = utils.fluentWait(By.name("session_password")).get(0);

            pwd.sendKeys(currentAccount.getPassword());
            Thread.sleep(3000);


            WebElement in = driver.findElement(By.id("login-submit"));
            in.click();

            Thread.sleep(10000);

            //check if user logged in successfully
            checkIfUserIsloggedIn(true);
            //          }
        }

    }


    public boolean checkIfUserIsloggedIn(boolean showErrors) {
        List<WebElement> users = utils.fluentWait(By.xpath("//*[contains(@class,'profile-member-photo')]"));
        if (users.size() > 0 && users.get(0).getAttribute("alt").contains(currentAccount.getCaption())) {
            log.info("logged in successfully");
            return true;
        } else {
            if (showErrors) {
                log.error("Error on login with " + currentAccount.getUsername());
                throw new RuntimeException("Can't login with specified user");
            }
            return false;
        }
    }

    public boolean connectTo() throws InterruptedException {
        log.info("Openning popup to connect to current user");
        Thread.sleep(2000);

        List<WebElement> connectButtons = utils.fluentWait(By.xpath("//li-icon[contains(@class,'profile-actions__overflow')]"));
        List<WebElement> connectButton = utils.fluentWait(By.xpath("//span[contains(@class,'profile-actions__label') and contains(text(),'Connect')]"));
        if (connectButtons.size() > 0 || connectButton.size() > 0) {
            if (connectButton.size() == 0) {
                log.info("Clicking on More... button");
                WebElement moreButton = connectButtons.get(0);
                utils.mouseMoveToElement(moreButton);
                Thread.sleep(1000);
                moreButton.click();
                Thread.sleep(1500);

                connectButton = utils.fluentWait(By.xpath("//span[contains(@class,'profile-actions__label') and contains(text(),'Connect')]"));
            }
            if (connectButton.size() > 0) {
                log.info("Clicking on Connect button");
                connectButton.get(0).click();
                Thread.sleep(6000);

                //check if connected successfully
                List<WebElement> popUpHeader = utils.fluentWait(By.xpath("//h2[contains(@id,'modal-description')]"));
                if (popUpHeader.size() > 0) {
                    log.info("Connect popUp opened successfully");
                    return true;
                }

            }
        }

        log.error("Error opening Connect popUp");
        return false;
    }

    public boolean sendMessage(String text) throws InterruptedException {
        log.info("Sending text message to user");
        Thread.sleep(2000);

        List<WebElement> sendButtons = utils.fluentWait(By.xpath("//div[contains(@class, 'send-invite')]/button[contains(@class,'button-secondary-large mr1')]"));
        if (sendButtons.isEmpty()) {
            log.error("Can't find 'Add a note' button");
            return false;
        }

        List<WebElement> customMessageEmail = utils.fluentWait(By.xpath("//input[@type='email' and @id='email' and @name='email' and contains(@class,'text-field')]"));
        if (! customMessageEmail.isEmpty()) {
            log.error("Can't send message without email");
            emailRequired = true;
            return false;
        }


        WebElement addANoteButton = sendButtons.get(0);
        utils.mouseMoveToElement(addANoteButton);
        Thread.sleep(1000);
        log.info("Clicking on Add a Note button");
        addANoteButton.click();
        Thread.sleep(4000);

        List<WebElement> customMessageTextAreas = utils.fluentWait(By.xpath("//textarea[contains(@class,'custom-message')]"));
        if (customMessageTextAreas.isEmpty()) {
            log.error("Can't find 'Include a personal message' textarea");
            return false;
        }

        Thread.sleep(2000);

        WebElement customMessageTextArea = customMessageTextAreas.get(0);
        log.info("Typing message text:" + text);
        customMessageTextArea.sendKeys(text);

        List<WebElement> customMessageSubmitButtons = utils.fluentWait(By.xpath("//div[contains(@class, 'send-invite')]/button[contains(@class,'button-primary-large ml1')]"));
        if (customMessageSubmitButtons.isEmpty()) {
            log.error("Can't find 'Send invitation' button");
            return false;
        }
        WebElement customMessageSubmitButton = customMessageSubmitButtons.get(0);
        utils.mouseMoveToElement(customMessageSubmitButton);
        Thread.sleep(1000);
        log.info("Clicking on 'Send invitation' button");
        customMessageSubmitButton.click();
        Thread.sleep(4000);

        //check if message was sent
        List<WebElement> sendConfirmations = utils.fluentWait(By.xpath("//div[contains(@class,'confirmation-text')]/span"));
        if (sendConfirmations.isEmpty()) {
            log.error("Can't find 'Send invitation confirmation'");
            return false;
        }

        WebElement sendConfirmation = sendConfirmations.get(0);
        if (!sendConfirmation.getText().contains("Your invitation") || !sendConfirmation.getText().contains("was sent")) {
            log.error("Incorrect 'Send invitation confirmation'" + sendConfirmation.getText());
            return false;
        }

        log.info("Invitation was send successfully");
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
            log.info("Can'r read resources/config.groovy file");

        }

        throw new RuntimeException("Error reading config file");
    }

    public void executeAddContact() {

        //lets limit exicution time
        if (executed.get() >= executionLimit) {
            log.info("Applivation processed " + executed.get() + " accounts. No more accounts allowed today.");
            System.out.println("Applivation processed " + executed.get() + " accounts. No more accounts allowed today.");

            //exiting from spring app
            SpringApplication.exit(context, () -> 0);
            return;
        }


        LinkedInContact contact = contactRepository.getNextAvailableContact(currentAccount);
        Long contactId = contact.getId();

        boolean sendingResult = false;
        String error = null;

        log = createLogger();

        //stringWriter = new StringWriter();
        StringBuffer buf = stringWriter.getBuffer();
        buf.setLength(0);


        try {
            //starting contact processing
            emailRequired = false;
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            Map<String, Object> contextMap = mapper.convertValue(contact, new TypeReference<Map<String, Object>>() {
            });

            String template = this.getMessage("first_follow_up", contextMap);

            this.loginTo();
            this.searchGoogle(contact.getFirstName() + " " + contact.getLastName() + " " + contact.getCompanyName(), contact.getLinkedin());

            sendingResult = this.connectTo()
                    && this.sendMessage(template);

        } catch (InterruptedException | RuntimeException e) {
            error = e.getMessage();
            System.out.println("Error:" + error);
            e.printStackTrace();

        }



        contactRepository.updateContactStatus(contactId, new Account(), sendingResult ? LinkedInContact.STATUS_PROCESSED : LinkedInContact.STATUS_ERROR,
                error, stringWriter.toString());

        executed.incrementAndGet();
    }


    public void executeSearchContacts() {


        String location = config.get("autolinkedin.search_contacts_location").toString();
        String fullLocationString = config.get("autolinkedin.search_contacts_fullLocationString").toString();
        String position = config.get("autolinkedin.search_contacts_position").toString();
        List<String> inductries = (List<String>)config.get("autolinkedin.search_contacts_industries");
                //Collections.emptyList();
        //Arrays.asList("games", "gamb", "soft", "program", "consult", "graphic", "techno");


        currentLocation = locationRepository.getLocationByLocation(fullLocationString);
        if (currentLocation == null) {
            //lets create new location
            currentLocation = new Location(fullLocationString);
            locationRepository.save(currentLocation);

        }

        //todo  read search parameters
/*
        LinkedInContact contact = contactRepository.getNextAvailableContact(this.getLoginCheck());
        Long contactId = contact.getId();
*/
        boolean sendingResult = false;
        String error = null;

        log = createLogger();

        StringBuffer buf = stringWriter.getBuffer();
        buf.setLength(0);


        try {
            //starting contact processing
/*
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            Map<String, Object> contextMap = mapper.convertValue(contact, new TypeReference<Map<String, Object>>() {});

            String template = this.getMessage("first_follow_up", contextMap);
*/

            this.loginTo();

            fillSearchForm(location, position, inductries);
            boolean pagesAvailable = true;
            boolean canExecute = true;

            if (currentAccount.getLastPage() == null) {
                currentAccount.setLastPage(0);
            }
            if (currentAccount.getLastPage().intValue() !=0) {
                goToTheNextPage(currentAccount.getLastPage());
            }



            while (pagesAvailable && canExecute) {
                canExecute = checkGrabbingLimit();

                List<LinkedInContact> contacts = extractLinkedInContactsOnPage();
                contactRepository.saveNewContactsBatch(contacts);
                //WebElement lastPage = getLastPageDisplayed();
                goToTheNextPage(0);
                grabbed.incrementAndGet();
            }

            List<LinkedInContact> contacts = extractLinkedInContactsOnPage();
            contactRepository.saveNewContactsBatch(contacts);

        } catch (InterruptedException | RuntimeException e) {
            error = e.getMessage();
            System.out.println("Error:" + error);
            e.printStackTrace();

        }
/*
        contactRepository.updateContactStatus(contactId, sendingResult ? LinkedInContact.STATUS_PROCESSED : LinkedInContact.STATUS_ERROR,
                error, stringWriter.toString()  );
*/

    }

    private boolean checkGrabbingLimit() {
        //lets limit exicution time
        if (grabbed.get() >= grabbingLimit) {
            log.info("Applivation processed " + grabbed.get() + " pages. No more pages allowed today.");
            System.out.println("Applivation processed " + grabbed.get() + " pages. No more pages allowed today.");

            //exiting from spring app
            //SpringApplication.exit(context, () -> 0);
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
        utils.randomSleep(1);
        searchTypeahead.get(0).click();

        //injectScriptFile("resources/injectingToDOM.js");

        ((JavascriptExecutor) driver).executeScript("window.location='/search/results/people/?origin=DISCOVER_FROM_SEARCH_HOME'");
        log.info("Redirecting to Search Form'");
        utils.randomSleep(10);

        List<WebElement> allFiltersButtons = utils.fluentWait(By.xpath("//button[contains(@class,'search-filters-bar') and contains(@class,'all-filters')]"));
        if (allFiltersButtons.isEmpty()) {
            log.error("Can't find 'All Filters Button'");
            return false;
        }

        allFiltersButtons.get(0).click();
        utils.randomSleep(10);


        //fill position
        List<WebElement> titleFields = utils.fluentWait(By.id("search-advanced-title"));
        if (titleFields.isEmpty()) {
            log.error("Can't find 'Location Filter input'");
            return false;
        }

        WebElement titleField = titleFields.get(0);
        utils.mouseMoveToElement(titleField);
        utils.randomSleep(1);
        titleField.click();
        utils.randomSleep(1);
        titleField.sendKeys(position);
        utils.randomSleep(1);

        //fill network fields
        List<WebElement> searchNetworkFields = utils.fluentWait(By.xpath("//label[contains(@for,'facetNetwork')]"));
        if (searchNetworkFields.isEmpty()) {
            log.error("Can't find 'Network checkboxes'");
            return false;
        }

        for (WebElement network : searchNetworkFields) {
            utils.mouseMoveToElement(network);
            utils.randomSleep(1);
            network.click();
            utils.randomSleep(1);
        }

        //search-filters-bar__all-filters button-tertiary-medium-muted mr3
/*
        List<WebElement> locationButtons = utils.fluentWait(By.xpath("//button[contains(@aria-controls,'locations-facet-values')]//h3"));
        if (locationButtons.isEmpty()) {
            log.error("Can't find 'Location Button'");
            return false;
        }

        WebElement locationButton = locationButtons.get(0);
        utils.mouseMoveToElement(locationButton);
        utils.randomSleep(1);
        locationButton.click();
        utils.randomSleep(2);
*/
        List<WebElement> searchLocationFields = utils.fluentWait(By.xpath("//input[contains(@placeholder,'Add a location')]"));
        if (searchLocationFields.isEmpty()) {
            log.error("Can't find 'Location Filter input'");
            return false;
        }

        WebElement searchLocationFilter = searchLocationFields.get(0);
        utils.mouseMoveToElement(searchLocationFilter);
        utils.randomSleep(1);
        searchLocationFilter.click();
        utils.randomSleep(1);
        searchLocationFilter.sendKeys(location);
        utils.randomSleep(1);
        Actions builder = new Actions(driver);
        builder.moveToElement(searchLocationFilter).moveByOffset(0, 20).click().build().perform();


        utils.randomSleep(2);
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
            utils.randomSleep(1);
            searchIndustryFilter.click();
            searchIndustryFilter.clear();
            utils.randomSleep(1);
            searchIndustryFilter.sendKeys(industry);
            utils.randomSleep(1);
            builder = new Actions(driver);
            builder.moveToElement(searchIndustryFilter).moveByOffset(0, 20).click().build().perform();

            utils.randomSleep(2);

            //lets check if location is added
            List<WebElement> searchIndustryResults = utils.fluentWait(By.xpath("//label[contains(@for,'facetIndustry') and contains(text()," + industry + ")]"));
            if (searchIndustryResults.isEmpty()) {

                log.error("Search result have no industry:" + industry + ". Pleace, check");
                //return false;
            }

            log.info("Search industry " + industry + " added successfully");
        }


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


    private List<LinkedInContact> extractLinkedInContactsOnPage() throws InterruptedException {

        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        utils.randomSleep(4);
        List<WebElement> searchResults = utils.fluentWait(By.xpath("//div[contains(@class,'search-result') and contains(@class,'info')]"));
        if (searchResults.isEmpty()) {
            log.error("No results found");
            return null;
        }

        List<LinkedInContact> contacts = new LinkedList<>();

        for (WebElement result : searchResults) {
            utils.mouseMoveToElement(result);
            utils.randomSleep(1);

            contacts.add(extractLinkedInContact(result));
        }

        return contacts;
    }


    private boolean goToTheNextPage(int page) throws InterruptedException {

        utils.randomSleep(2);
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        utils.randomSleep(1);
        List<WebElement> resultsPages = utils.fluentWait(By.xpath("//ol[contains(@class,'results-paginator')]/li/ol/li[contains(@class,'active')]"));
        if (resultsPages.isEmpty()) {
            log.error("No pages found");
            return false;
        }
        log.info("Pages found");

        if (page == 0) {
            String nextPageXpath = "//ol[contains(@class,'results-paginator')]/li/ol/li[contains(@class,'active')]/following-sibling::li[1]/button";
            List<WebElement> nextPage = utils.fluentWait(By.xpath(nextPageXpath));
            if (nextPage.isEmpty()) {
                log.error("No next page found");
                return false;
            }
            String nextPageNum = nextPage.get(0).getText();
            currentAccount.setLastPage(Integer.parseInt(nextPageNum)-1);
            accountRepository.save(currentAccount);
            nextPage.get(0).click();

            utils.randomSleep(5);
            return true;
        }

        if (page != 0) {
            //then ve can go to the page # passed as parameter.
           // driver.get(driver.getCurrentUrl() + "page=" + page);

            if (paggingCounter.get() >1000) {
                log.error("Too deep paging paggingCounter = " + paggingCounter.get() + ", but we searching for page #" + page);
                return false;
            }

            WebElement foundPage = checkIfPageLinkDisplayed(page);
            if (foundPage != null) {
                foundPage.click();
                utils.randomSleep(5);
                return true;
            }

            WebElement currentLastPage = getLastPageDisplayed();
            if (currentLastPage == null) {
                log.error("No last page found on current page." );
                return false;
            }

            int lastPageIndex = Integer.parseInt(currentLastPage.getText());
            if (lastPageIndex< page){
                currentLastPage.click();
                utils.randomSleep(5);
                paggingCounter.incrementAndGet();
                return goToTheNextPage(page);
                //paggingCounter.decrementAndGet();

            } else {
                log.error("Something going wrong lastPageIndex = " + lastPageIndex + ", but we searching for page #" + page);
                return false;
            }



        }

        return false;

    }


    private WebElement checkIfPageLinkDisplayed(int page) throws InterruptedException {

        String pageXpath = "//ol[contains(@class,'results-paginator')]/li/ol/li/button[contains(text(),'" + page + "')]";
        List<WebElement> nextPage = utils.fluentWait(By.xpath(pageXpath));
        if (nextPage.isEmpty()) {
            log.error("No page # " + page + " found");
            return null;
        }
        return nextPage.get(0);


    }

    private WebElement getLastPageDisplayed() throws InterruptedException {

        String pageXpath = "//ol[contains(@class,'results-paginator')]/li/ol/li[contains(@class,'active')]/following-sibling::li/button";
        List<WebElement> nextPage = utils.fluentWait(By.xpath(pageXpath));
        if (nextPage.isEmpty()) {
            log.error("No pages found");
            return null;
        }
        return nextPage.get(nextPage.size()-1);

    }




}