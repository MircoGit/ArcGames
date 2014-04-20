/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.arcgames.tests;

import com.thoughtworks.selenium.Selenium;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author Mirco
 */
public class AdminSecurityTest {
    
    private Selenium selenium;
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();
    private List<String> adminUrlList = new ArrayList<String>(); // List of admin URLs

    public AdminSecurityTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        baseUrl = "http://localhost:8080/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        // Feeding list of URLs
        adminUrlList.add("ArcGames/faces/news/Create.xhtml");
        adminUrlList.add("ArcGames/faces/game/Create.xhtml");
        adminUrlList.add("ArcGames/faces/news/Edit.xhtml");
        adminUrlList.add("ArcGames/faces/game/Edit.xhtml");
    }
    
    @Test
    public void nonAdminTryAccessAdminPages() throws Exception {

        // Non-admin user login informations
        String username = "nonAdmin";
        String passwd = "nonAdminPassword";

        // We open the web app
        driver.get(baseUrl + "/ArcGames/");

        // Login as non-admin
        login(username, passwd);

        // Try to access non-allowed admin pages
        for (String page : adminUrlList) {
            String adminUrl = baseUrl + page;
            driver.get(adminUrl);
            assertNotEquals(driver.getCurrentUrl(), adminUrl);
        }

        // Logout
        driver.findElement(By.linkText("logout")).click();
        
    }
    
    @Test
    public void adminAccessAdminPages() throws Exception {

        // Non-admin user login informations
        String username = "Admin";
        String passwd = "123456";

        // We open the web app
        driver.get(baseUrl + "/ArcGames/");

        // Login as admin
        login(username, passwd);

        // Try to access non-allowed admin pages
        for (String page : adminUrlList) {
            String adminUrl = baseUrl + page;
            driver.get(adminUrl);
            assertEquals(driver.getCurrentUrl(), adminUrl);
        }

        // Logout
        driver.findElement(By.linkText("logout")).click();
        
    }
    
    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
    
    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }
    
    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
        }
    }
    
    private void login(String username, String passwd) {
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("j_idt19:login")).clear();
        driver.findElement(By.id("j_idt19:login")).sendKeys(username);
        driver.findElement(By.id("j_idt19:passwd")).clear();
        driver.findElement(By.id("j_idt19:passwd")).sendKeys(passwd);
        driver.findElement(By.name("j_idt19:j_idt24")).click();
    }
}
