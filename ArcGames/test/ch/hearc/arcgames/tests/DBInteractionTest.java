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
public class DBInteractionTest {

    private Selenium selenium;
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    public DBInteractionTest() {
    }

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        baseUrl = "http://localhost:8080/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void loggedUserTryAccessPages() throws Exception {

        // New user informations
        String username = "myUsername";
        String mail = "myMail@gmail.com";
        String location = "Neuchâtel";
        String firstName = "myFName";
        String lastName = "myLName";
        String passwd = "myPasswd";

        // We open the web app
        driver.get(baseUrl + "/ArcGames/");

        // SignUp with a test user
        signUp(username, mail, passwd, location, firstName, lastName);

        // Login as non-admin
        login(username, passwd);

        // Edit user
        String s = "edited";
        driver.findElement(By.linkText(username)).click();
        driver.findElement(By.id("j_idt19:username")).clear();
        driver.findElement(By.id("j_idt19:username")).sendKeys(s);
        driver.findElement(By.id("j_idt19:mail")).clear();
        driver.findElement(By.id("j_idt19:mail")).sendKeys(s);
        driver.findElement(By.id("j_idt19:passwd")).clear();
        driver.findElement(By.id("j_idt19:passwd")).sendKeys(s);
        driver.findElement(By.id("j_idt19:passwdConfirm")).clear();
        driver.findElement(By.id("j_idt19:passwdConfirm")).sendKeys(s);
        driver.findElement(By.id("j_idt19:location")).clear();
        driver.findElement(By.id("j_idt19:location")).sendKeys(s);
        driver.findElement(By.id("j_idt19:firstName")).clear();
        driver.findElement(By.id("j_idt19:firstName")).sendKeys(s);
        driver.findElement(By.id("j_idt19:lastName")).clear();
        driver.findElement(By.id("j_idt19:lastName")).sendKeys(s);
        driver.findElement(By.id("j_idt19:submit")).click();

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
}
