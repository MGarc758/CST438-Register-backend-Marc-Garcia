package com.cst438;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.StudentRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.Student;

/*
 * This example shows how to use selenium testing using the web driver 
 * with Chrome browser.
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *      
 *    Make sure that TEST_COURSE_ID is a valid course for TEST_SEMESTER.
 *    
 *    URL is the server on which Node.js is running.
 */

@SpringBootTest
public class EndToEndStudentTest {

	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/marcgarcia/Downloads/chromedriver-mac-x64/chromedriver";

	public static final String URL = "http://localhost:3000/admin";
	
	public static final String TEST_USER_NAME = "Bob Bob";

	public static final String TEST_USER_EMAIL = "bob@csumb.edu";
	
	public static final String EDIT_USER_NAME = "Bob James";
	
	public static final String EDIT_USER_EMAIL = "bobjames@csumb.edu";
	
	public static final String TEST_USER = "tom";
	
	public static final String TEST_EMAIL = "trebold@csumb.edu";

	public static final int SLEEP_DURATION = 1000; // 1 second.


	@Autowired
	StudentRepository studentRepository;


	/*
	 * Admin add a new student test
	 */
	
	@Test
	public void addStudentTest() throws Exception {

		/*
		 * if student is already exists, then delete the student.
		 */
		
		Student x = null;
		do {
			x = studentRepository.findByEmail(TEST_USER_EMAIL);
			if (x != null)
				studentRepository.delete(x);
		} while (x != null);

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// select the last of the radio buttons on the list of semesters page.
			
			//WebElement we = driver.findElement(By.xpath("(//input[@type='radio'])[last()]"));
			//we.click();


			// enter student name and email click Add button
			
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_USER_EMAIL);
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_USER_NAME);
			
			// Locate and click "Add student" button
			
			driver.findElement(By.id("addStudentButton")).click();
			Thread.sleep(SLEEP_DURATION);
	
			
			String studentText = driver.findElement(By.xpath("//td[@id='"+ TEST_USER_NAME +"']")).getText();
			assertEquals(TEST_USER_NAME, studentText);
			
			String emailText = driver.findElement(By.xpath("//td[@id='"+ TEST_USER_EMAIL +"']")).getText();
			assertEquals(TEST_USER_EMAIL, emailText);
			
			
		} catch (Exception ex) {
			throw ex;
		} finally {


			driver.close();
			driver.quit();
		}

	}
	
	@Test
	public void editStudentTest() throws Exception {

		/*
		 * if student is already exists, then delete the student.
		 */
		
		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// Locate and click edit button for user
			driver.findElement(By.id(TEST_USER_NAME + " button")).click();


			// change student name and email
			driver.findElement(By.xpath("//input[@name='email']")).clear();			
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(EDIT_USER_EMAIL);

			driver.findElement(By.xpath("//input[@name='name']")).clear();
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(EDIT_USER_NAME);
			
			// Locate and click submit button
			
			driver.findElement(By.id("submitButton")).click();
			Thread.sleep(SLEEP_DURATION);
	
			
			String studentText = driver.findElement(By.xpath("//td[@id='"+ EDIT_USER_NAME +"']")).getText();
			assertEquals(EDIT_USER_NAME, studentText);
			
			String emailText = driver.findElement(By.xpath("//td[@id='"+ EDIT_USER_EMAIL +"']")).getText();
			assertEquals(EDIT_USER_EMAIL, emailText);
			
			
		} catch (Exception ex) {
			throw ex;
		} finally {


			driver.close();
			driver.quit();
		}

	}
	
	@Test
	public void deleteStudentTest() throws Exception {

		/*
		 * if student is already exists, then delete the student.
		 */
		

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			//Click delete button
			driver.findElement(By.id(TEST_USER + " delete")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Check if delete action worked by looking up student in database
			
			List<WebElement> deleteLinks = driver.findElements(By.id(TEST_USER + " delete"));

			assertEquals(0, deleteLinks.size());
			
			
		} catch (Exception ex) {
			throw ex;
		} finally {


			driver.close();
			driver.quit();
		}

	}
}
