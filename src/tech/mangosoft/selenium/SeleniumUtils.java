package tech.mangosoft.selenium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class SeleniumUtils {

    private static final Logger log = LogManager.getRootLogger();

    @Autowired
    private WebDriver driver;

    public SeleniumUtils() {
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void randomSleep(int sleep) throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextLong(500L * sleep, 1000L * sleep));
    }

    public List<String> openTabAndDoRedirect(final String url) throws InterruptedException{
        ((JavascriptExecutor) driver).executeScript("window.open()");
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        driver.get(url);
        randomSleep(6);
        return tabs;
    }

    public WebElement findElementsAndGetFirst(final By by, final WebElement parent){
        List<WebElement> elements;

        if(parent != null){
            elements = parent.findElements(by);
        }else{
            elements = driver.findElements(by);
        }

        if(!elements.isEmpty()){
            return elements.get(0);
        }else{
            return null;
        }
    }

    public WebElement findElementsAndGetByIndex(final By by, final WebElement parent, final int index){
        List<WebElement> elements;

        if(parent != null){
            elements = parent.findElements(by);
        }else{
            elements = driver.findElements(by);
        }

        if(!elements.isEmpty()){
            if(elements.size() > index){
                return elements.get(index);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    public Optional<String> findTextOrExtractValue(final By by, final WebElement parent, final String attribute){
        WebElement element;

        if(parent != null){
            element = findElementsAndGetFirst(by, parent);
        }else{
            element = findElementsAndGetFirst(by, null);
        }

        if (element == null) {
            log.error("No text was found for: " + by.toString());
            return Optional.empty();
        }

        String result = null;
        if (attribute == null) {
            result = element.getText();
        } else {
            result = element.getAttribute(attribute);
        }
        return Optional.ofNullable(result);
    }

    public List<WebElement> fluentWait(final By locator) {

        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(30, TimeUnit.SECONDS)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException.class);

        List<WebElement> foo = wait.until(
                new Function<WebDriver, List<WebElement>>() {
                    public List<WebElement> apply(WebDriver driver) {
                        return driver.findElements(locator);
                    }
                }
        );
        return foo;
    }

    public List<WebElement> fluentWait(final By locator, final WebElement parent) {

        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(10, TimeUnit.SECONDS)
                .pollingEvery(3, TimeUnit.SECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException.class);

        List<WebElement> foo = wait.until(
                new Function<WebDriver, List<WebElement>>() {
                    public List<WebElement> apply(WebDriver driver) {
                        return parent.findElements(locator);
                    }
                }
        );
        return foo;
    }

    public Optional<String> findTextAndExtractValue(final By locator, final WebElement parent){
        return findTextAndExtractValue(locator, parent, null);
    }

    public Optional<String> findTextAndExtractValue(final By locator, final WebElement parent, final String attribute) {

        List<WebElement> elements;
        if (parent != null) {
            elements = fluentWait(locator, parent);
        } else {
            elements = fluentWait(locator);
        }

        if (elements.isEmpty()) {
            log.error("No text was found for: " + locator.toString() + " " + parent.getTagName() + " tag");
            return Optional.empty() ;
        }

        String result = null;
        if (attribute == null) {
            result = elements.get(0).getText();
        } else {
            result = elements.get(0).getAttribute(attribute);
        }

        return Optional.ofNullable(result);
    }


    public void mouseMoveToElement(WebElement element) {
        Actions builder = new Actions(driver);
        builder.moveToElement(element).build().perform();
    }


    public WebElement ensureTextIsTyped(WebElement element, String text) throws InterruptedException {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            sb.append(text.charAt(i));
            while (!element.getAttribute("value").equals(sb.toString())) {
                element.clear();
                element.sendKeys(sb.toString());
                randomSleep(1);
            }
        }
        return element;
    }


    public WebElement searchGoogle(String searchString, String domain, boolean throwRuntimeException) throws InterruptedException {

        driver.get("http://www.google.com");
        WebElement element = driver.findElement(By.name("q"));
        element.sendKeys(searchString + "\n"); // send also a "\n"
        //element.submit();

        randomSleep(3);

        // wait until the google page shows the result

        List<WebElement> findElements = fluentWait(By.xpath("//*[@id='rso']//h3/a"));
        if (findElements.size() == 0){
            //we found nothing
            log.error("No results returned for " + searchString );
            if (throwRuntimeException) {
                throw new RuntimeException("No results returned for  " + searchString);
            }
            return null;

        }

        log.debug("Searching " + searchString + ". Results: " + findElements.stream().map((x) -> {
            return x.getAttribute("href");
        })
                .collect(Collectors.joining(", ")));

        // this are all the links you like to visit
        for (WebElement webElement : findElements) {
            String href = webElement.getAttribute("href");

            if (href.indexOf(domain) > -1) {
                //click
                log.info("Found corect link at google: " + href + ". Clicking...");
                webElement.click();
                randomSleep(1);
                return webElement;
            }
        }

        log.error("Can't find searched website " + searchString);
        if (throwRuntimeException) {
            throw new RuntimeException("Can't find searched website " + searchString);
        }
        return null;
    }


}
