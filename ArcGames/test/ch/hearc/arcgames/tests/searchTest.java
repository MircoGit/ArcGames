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
import static javax.ws.rs.client.Entity.form;
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
public class searchTest {

    private Selenium selenium;
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    public searchTest() {
    }

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        baseUrl = "http://localhost:8080/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void loggedUserTryAccessPages() throws Exception {

        // Non-admin user login informations
        String username = "nonAdmin";
        String passwd = "nonAdminPassword";

        // We open the web app
        driver.get(baseUrl + "/ArcGames/");

        // SignUp with a test user
        String pattern = "fake";
        signUp(pattern, pattern, pattern, pattern, pattern, pattern);

        // Login as non-admin
        login(username, passwd);

        // Go to users page
        driver.findElement(By.linkText("Users")).click();

        // Search user that matches "fake" pattern
        simpleSearch(pattern);

        // Check results
        String result = driver.findElement(By.xpath("//form[@id='j_idt21']/table/tbody[2]/tr/td")).getText();
        assertTrue(result.contains(pattern));

        // Search user that matches "fake" user
        advancedSearch(pattern);

        // Check results
        result = driver.findElement(By.xpath("//form[@id='j_idt21']/table/tbody[2]/tr/td")).getText();
        assertTrue(result.contains(pattern));

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

    private void signUp(String username, String mail, String passwd, String location, String firstName, String lastName) {
        driver.findElement(By.linkText("Sign up")).click();
        driver.findElement(By.id("j_idt18:username")).clear();
        driver.findElement(By.id("j_idt18:username")).sendKeys(username);
        driver.findElement(By.id("j_idt18:mail")).clear();
        driver.findElement(By.id("j_idt18:mail")).sendKeys(mail);
        driver.findElement(By.id("j_idt18:passwd")).clear();
        driver.findElement(By.id("j_idt18:passwd")).sendKeys(passwd);
        driver.findElement(By.id("j_idt18:passwdConfirm")).clear();
        driver.findElement(By.id("j_idt18:passwdConfirm")).sendKeys(passwd);
        driver.findElement(By.id("j_idt18:location")).clear();
        driver.findElement(By.id("j_idt18:location")).sendKeys(location);
        driver.findElement(By.id("j_idt18:firstName")).clear();
        driver.findElement(By.id("j_idt18:firstName")).sendKeys(firstName);
        driver.findElement(By.id("j_idt18:lastName")).clear();
        driver.findElement(By.id("j_idt18:lastName")).sendKeys(lastName);
        driver.findElement(By.id("j_idt18:submit")).click();
    }

    private void login(String username, String passwd) {
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("j_idt19:login")).clear();
        driver.findElement(By.id("j_idt19:login")).sendKeys(username);
        driver.findElement(By.id("j_idt19:passwd")).clear();
        driver.findElement(By.id("j_idt19:passwd")).sendKeys(passwd);
        driver.findElement(By.id("j_idt19:submit")).click();
    }

    private void simpleSearch(String pattern) {
        driver.findElement(By.id("j_idt18:search")).clear();
        driver.findElement(By.id("j_idt18:search")).sendKeys(pattern);
        driver.findElement(By.id("j_idt18:submit")).click();
    }

    private void advancedSearch(String pattern) {
        driver.findElement(By.id("j_idt18:advenced")).click();
        driver.findElement(By.id("j_idt18:search")).clear();
        driver.findElement(By.id("j_idt18:search")).sendKeys(pattern);
        driver.findElement(By.id("j_idt18:firstNameSearch")).clear();
        driver.findElement(By.id("j_idt18:firstNameSearch")).sendKeys(pattern);
        driver.findElement(By.id("j_idt18:lastNameSearch")).clear();
        driver.findElement(By.id("j_idt18:lastNameSearch")).sendKeys(pattern);
        driver.findElement(By.id("j_idt18:locationSearch")).clear();
        driver.findElement(By.id("j_idt18:locationSearch")).sendKeys(pattern);
        driver.findElement(By.id("j_idt18:submit")).click();
    }
}
