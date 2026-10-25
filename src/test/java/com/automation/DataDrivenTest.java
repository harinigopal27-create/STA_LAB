package com.automation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
public class DataDrivenTest {

    @DataProvider(name = "loginData")
    public Object[][] getData() throws Exception {

        List<Object[]> data = new ArrayList<>();

        try (BufferedReader br =
                 new BufferedReader(
                     new FileReader("testdata/login.csv"))) {

            br.readLine(); // Skip header

            String line;

            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        }

        return data.toArray(new Object[0][]);
    }

    @Test(dataProvider = "loginData")
    public void loginTest(
            String username,
            String password,
            String expected) {

        WebDriver driver = new ChromeDriver();

        try {
            driver.get(
                "https://practicetestautomation.com/practice-test-login/"
            );

            driver.findElement(By.id("username"))
                  .sendKeys(username);

            driver.findElement(By.id("password"))
                  .sendKeys(password);

            driver.findElement(By.id("submit")).click();

            if (expected.equalsIgnoreCase("SUCCESS")) {

                Assert.assertTrue(
                    driver.getCurrentUrl().contains(
                        "/logged-in-successfully/"
                    ),
                    "Login was not successful"
                );

                Assert.assertTrue(
                    driver.findElement(By.tagName("h1"))
                          .getText()
                          .contains("Logged In Successfully"),
                    "Success heading was not displayed"
                );

                System.out.println(
                    "PASS: Successful login for " + username
                );

            } else {

                // Wait briefly for the error message to appear
                org.openqa.selenium.support.ui.WebDriverWait wait =
                    new org.openqa.selenium.support.ui.WebDriverWait(
                        driver,
                        java.time.Duration.ofSeconds(10)
                    );

                WebElement error = wait.until(
                    org.openqa.selenium.support.ui.ExpectedConditions
                        .visibilityOfElementLocated(By.id("error"))
                );

                String errorMessage = error.getText();

                Assert.assertFalse(
                    errorMessage.trim().isEmpty(),
                    "Error message is empty"
                );

                System.out.println(
                    "PASS: Invalid login for " + username
                );

                System.out.println(
                    "Error message: " + errorMessage
                );
            }

        } finally {
            driver.quit();
        }
    }
}
