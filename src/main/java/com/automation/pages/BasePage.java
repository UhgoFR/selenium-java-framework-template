package com.automation.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor jsExecutor;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // Métodos de espera
    protected void waitForElementVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected void waitForElementClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void waitForElementInvisible(WebElement element) {
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    protected void waitForElementPresent(By locator) {
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected void waitForTextPresent(WebElement element, String text) {
        wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    // Métodos de clic
    protected void click(WebElement element) {
        waitForElementClickable(element);
        element.click();
    }

    protected void click(By locator) {
        WebElement element = driver.findElement(locator);
        click(element);
    }

    protected void clickWithJS(WebElement element) {
        jsExecutor.executeScript("arguments[0].click();", element);
    }

    protected void doubleClick(WebElement element) {
        waitForElementVisible(element);
        actions.doubleClick(element).perform();
    }

    protected void rightClick(WebElement element) {
        waitForElementVisible(element);
        actions.contextClick(element).perform();
    }

    // Métodos de escritura
    protected void type(WebElement element, String text) {
        waitForElementVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    protected void typeAndEnter(WebElement element, String text) {
        type(element, text);
        element.sendKeys(Keys.ENTER);
    }

    protected void typeWithAction(WebElement element, String text) {
        waitForElementVisible(element);
        actions.sendKeys(element, text).perform();
    }

    // Métodos de obtención de texto y atributos
    protected String getText(WebElement element) {
        waitForElementVisible(element);
        return element.getText();
    }

    protected String getAttribute(WebElement element, String attribute) {
        waitForElementVisible(element);
        return element.getAttribute(attribute);
    }

    protected String getValue(WebElement element) {
        return getAttribute(element, "value");
    }

    // Métodos de selección
    protected void selectByVisibleText(WebElement dropdown, String text) {
        waitForElementClickable(dropdown);
        Select select = new Select(dropdown);
        select.selectByVisibleText(text);
    }

    protected void selectByValue(WebElement dropdown, String value) {
        waitForElementClickable(dropdown);
        Select select = new Select(dropdown);
        select.selectByValue(value);
    }

    protected void selectByIndex(WebElement dropdown, int index) {
        waitForElementClickable(dropdown);
        Select select = new Select(dropdown);
        select.selectByIndex(index);
    }

    // Métodos de verificación
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected boolean isEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected boolean isSelected(WebElement element) {
        try {
            return element.isSelected();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // Métodos de navegación
    protected void navigateTo(String url) {
        driver.get(url);
    }

    protected void refresh() {
        driver.navigate().refresh();
    }

    protected void goBack() {
        driver.navigate().back();
    }

    protected void goForward() {
        driver.navigate().forward();
    }

    // Métodos de scroll
    protected void scrollToElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void scrollToBottom() {
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    protected void scrollToTop() {
        jsExecutor.executeScript("window.scrollTo(0, 0);");
    }

    protected void scrollBy(int x, int y) {
        jsExecutor.executeScript("window.scrollBy(" + x + "," + y + ");");
    }

    // Métodos de JavaScript
    protected Object executeJS(String script, Object... args) {
        return jsExecutor.executeScript(script, args);
    }

    protected void highlightElement(WebElement element) {
        String originalStyle = element.getAttribute("style");
        jsExecutor.executeScript("arguments[0].style.border='3px solid red'", element);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        jsExecutor.executeScript("arguments[0].style='" + originalStyle + "'", element);
    }

    // Métodos de espera personalizada
    protected void waitForPageLoad() {
        wait.until((Function<WebDriver, Object>) driver -> 
            jsExecutor.executeScript("return document.readyState").equals("complete"));
    }

    protected void waitForAjaxComplete() {
        wait.until((Function<WebDriver, Object>) driver -> 
            jsExecutor.executeScript("return jQuery.active == 0"));
    }

    // Métodos de manejo de ventanas y tabs
    protected void switchToWindow(int windowIndex) {
        Set<String> windows = driver.getWindowHandles();
        if (windows.size() > windowIndex) {
            driver.switchTo().window(windows.toArray(new String[0])[windowIndex]);
        }
    }

    protected void switchToWindowByTitle(String title) {
        String currentWindow = driver.getWindowHandle();
        for (String window : driver.getWindowHandles()) {
            driver.switchTo().window(window);
            if (driver.getTitle().equals(title)) {
                return;
            }
        }
        driver.switchTo().window(currentWindow);
    }

    protected void closeCurrentWindow() {
        driver.close();
    }

    // Métodos de manejo de alerts
    protected String getAlertText() {
        return driver.switchTo().alert().getText();
    }

    protected void acceptAlert() {
        driver.switchTo().alert().accept();
    }

    protected void dismissAlert() {
        driver.switchTo().alert().dismiss();
    }

    protected void typeInAlert(String text) {
        driver.switchTo().alert().sendKeys(text);
    }

    // Métodos de espera con timeout personalizado
    protected void waitForElementVisible(WebElement element, int timeout) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        customWait.until(ExpectedConditions.visibilityOf(element));
    }

    protected void waitForElementClickable(WebElement element, int timeout) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        customWait.until(ExpectedConditions.elementToBeClickable(element));
    }

    // Métodos de utilidad
    public void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected String getTitle() {
        return driver.getTitle();
    }

    protected void maximizeWindow() {
        driver.manage().window().maximize();
    }

    protected void setWindowSize(int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
    }

    // Métodos de drag and drop
    protected void dragAndDrop(WebElement source, WebElement target) {
        waitForElementVisible(source);
        waitForElementVisible(target);
        actions.dragAndDrop(source, target).perform();
    }

    protected void dragAndDropBy(WebElement element, int xOffset, int yOffset) {
        waitForElementVisible(element);
        actions.dragAndDropBy(element, xOffset, yOffset).perform();
    }

    // Métodos de hover
    protected void hoverOverElement(WebElement element) {
        waitForElementVisible(element);
        actions.moveToElement(element).perform();
    }

    // Métodos de manejo de múltiples elementos
    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    protected int getElementCount(By locator) {
        return findElements(locator).size();
    }

    protected WebElement findElementByText(List<WebElement> elements, String text) {
        return elements.stream()
                .filter(element -> element.getText().contains(text))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Element with text '" + text + "' not found"));
    }
}
