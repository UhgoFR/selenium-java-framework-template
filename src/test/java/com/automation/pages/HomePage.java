package com.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

    @FindBy(tagName = "h1")
    private WebElement mainTitle;

    @FindBy(name = "q")
    private WebElement searchInput;

    @FindBy(css = "input[type='submit']")
    private WebElement searchButton;

    @FindBy(linkText = "About")
    private WebElement aboutLink;

    @FindBy(className = "navigation")
    private WebElement navigationMenu;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public String getPageTitle() {
        return getTitle();
    }

    public String getMainTitleText() {
        return getText(mainTitle);
    }

    public boolean isMainTitleDisplayed() {
        return isDisplayed(mainTitle);
    }

    public void searchFor(String searchTerm) {
        type(searchInput, searchTerm);
        click(searchButton);
    }

    public void searchAndEnter(String searchTerm) {
        typeAndEnter(searchInput, searchTerm);
    }

    public void clickAboutLink() {
        click(aboutLink);
    }

    public boolean isNavigationMenuDisplayed() {
        return isDisplayed(navigationMenu);
    }

    public void navigateToHomePage() {
        navigateTo("https://example.com");
    }

    public boolean isSearchInputVisible() {
        return isDisplayed(searchInput);
    }

    public String getSearchInputPlaceholder() {
        return getAttribute(searchInput, "placeholder");
    }
}
