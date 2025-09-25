
package com.example.api;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.ITestResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.service.TestResultService;
import com.example.util.ApiArtifactUtil;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.UUID;

/**
 * ReqResAPITest
 * Stateless and thread-safe for parallel execution (Rest-Assured).
 * No shared mutable state. Each test runs independently.
 */
public class ReqResAPITest {
	@Autowired
	private TestResultService testResultService;
	@BeforeSuite
	public void beforeSuite() {
		logger.info("[Suite] Starting ReqRes API Test Suite");
	}

	@AfterSuite
	public void afterSuite() {
		logger.info("[Suite] Completed ReqRes API Test Suite");
	}

	@BeforeMethod
	public void beforeMethod() {
		// No shared resources; each test is stateless and thread-safe
		logger.info("[Test] Starting test method");
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		logger.info("[Test] Finished test method");
		String executionId = System.getProperty("executionId", "manual");
		String testCaseId = result.getMethod().getMethodName();
		
		// Get Response and request body from test method (if available)
		Response response = null;
		String requestBody = null;
		if (result.getAttribute("response") != null) {
			response = (Response) result.getAttribute("response");
		}
		if (result.getAttribute("requestBody") != null) {
			requestBody = (String) result.getAttribute("requestBody");
		}
		
		// Save request/response JSON on failure (as per Step 6 requirement)
		if (result.getStatus() == ITestResult.FAILURE) {
			String artifactPath = ApiArtifactUtil.saveRequestResponseArtifacts(executionId, testCaseId, requestBody, response);
			
			// Set artifact attribute for database linking
			if (artifactPath != null) {
				result.setAttribute("artifactPath", artifactPath);
				logger.info("[Artifact] Saved API artifacts to: " + artifactPath);
			}
		}
	}
	private static final Logger logger = LogManager.getLogger(ReqResAPITest.class);
	private final String BASE_URL = "https://reqres.in/api";

	/** TC-API-01: US-401 TC-401.1 Get list of users (page 2) */
	@Test(description = "Get list of users from page 2", timeOut = 30000) // TestNG per-test timeout: 30s
	public void testGetListOfUsers_TC_API_01() {
		Response response = RestAssured.given()
			.header("x-api-key", "reqres-free-v1")
			.get(BASE_URL + "/users?page=2");
		// Save response for artifact
		org.testng.Reporter.getCurrentTestResult().setAttribute("response", response);
		logger.info("[US-401][TC-401.1][Thread {}] Get list of users response: {}", Thread.currentThread().getId(), response.getBody().asString());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
	}

	/** TC-API-02: US-401 TC-401.2 Get single user (id=2) */
	@Test(description = "Get single user with valid ID", timeOut = 30000)
	public void testGetSingleUserValid_TC_API_02() {
		Response response = RestAssured.given()
			.header("x-api-key", "reqres-free-v1")
			.get(BASE_URL + "/users/2");
		org.testng.Reporter.getCurrentTestResult().setAttribute("response", response);
		logger.info("[US-401][TC-401.2][Thread {}] Get single user (id=2) response: {}", Thread.currentThread().getId(), response.getBody().asString());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getInt("data.id"), 2);
	}

	/** TC-API-03: US-401 TC-401.3 Get single user (id=23, not found) */
	@Test(description = "Get single user with invalid ID (not found)", timeOut = 30000)
	public void testGetSingleUserNotFound_TC_API_03() {
		Response response = RestAssured.given()
			.header("x-api-key", "reqres-free-v1")
			.get(BASE_URL + "/users/23");
		org.testng.Reporter.getCurrentTestResult().setAttribute("response", response);
		logger.info("[US-401][TC-401.3][Thread {}] Get single user (id=23) response: {}", Thread.currentThread().getId(), response.getBody().asString());
		Assert.assertEquals(response.getStatusCode(), 404);
	}

	/** TC-API-04: US-402 TC-402.1 Create user */
	@Test(description = "Create new user with POST request", timeOut = 30000)
	public void testCreateUser_TC_API_04() {
		String uniqueName = "user_" + UUID.randomUUID();
		String body = "{\"name\":\"" + uniqueName + "\",\"job\":\"leader\"}";
		Response response = RestAssured.given()
			.header("Content-Type", "application/json")
			.header("x-api-key", "reqres-free-v1")
			.body(body)
			.post(BASE_URL + "/users");
		org.testng.Reporter.getCurrentTestResult().setAttribute("response", response);
		org.testng.Reporter.getCurrentTestResult().setAttribute("requestBody", body);
		logger.info("[US-402][TC-402.1][Thread {}] Create user response: {}", Thread.currentThread().getId(), response.getBody().asString());
		Assert.assertEquals(response.getStatusCode(), 201);
		Assert.assertTrue(response.jsonPath().getString("name").equals(uniqueName));
	}

	/** TC-API-05: US-402 TC-402.2 Update user (PUT) */
	@Test(description = "Update user details with PUT request", timeOut = 30000)
	public void testUpdateUserPut_TC_API_05() {
		String uniqueName = "user_" + UUID.randomUUID();
		String body = "{\"name\":\"" + uniqueName + "\",\"job\":\"manager\"}";
		Response response = RestAssured.given()
			.header("Content-Type", "application/json")
			.header("x-api-key", "reqres-free-v1")
			.body(body)
			.put(BASE_URL + "/users/2");
		org.testng.Reporter.getCurrentTestResult().setAttribute("response", response);
		org.testng.Reporter.getCurrentTestResult().setAttribute("requestBody", body);
		logger.info("[US-402][TC-402.2][Thread {}] Update user (PUT) response: {}", Thread.currentThread().getId(), response.getBody().asString());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.jsonPath().getString("name").equals(uniqueName));
	}

	/** TC-API-06: US-402 TC-402.3 Patch user */
	@Test(description = "Partially update user with PATCH request", timeOut = 30000)
	public void testPatchUser_TC_API_06() {
		String uniqueJob = "job_" + UUID.randomUUID();
		String body = "{\"job\":\"" + uniqueJob + "\"}";
		Response response = RestAssured.given()
			.header("Content-Type", "application/json")
			.header("x-api-key", "reqres-free-v1")
			.body(body)
			.patch(BASE_URL + "/users/2");
		org.testng.Reporter.getCurrentTestResult().setAttribute("response", response);
		org.testng.Reporter.getCurrentTestResult().setAttribute("requestBody", body);
		logger.info("[US-402][TC-402.3][Thread {}] Patch user response: {}", Thread.currentThread().getId(), response.getBody().asString());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.jsonPath().getString("job").equals(uniqueJob));
	}

	/** TC-API-07: US-402 TC-402.4 Delete user */
	@Test(description = "Delete user with DELETE request", timeOut = 30000)
	public void testDeleteUser_TC_API_07() {
		Response response = RestAssured.given()
			.header("x-api-key", "reqres-free-v1")
			.delete(BASE_URL + "/users/2");
		org.testng.Reporter.getCurrentTestResult().setAttribute("response", response);
		logger.info("[US-402][TC-402.4][Thread {}] Delete user response: {}", Thread.currentThread().getId(), response.getBody().asString());
		Assert.assertEquals(response.getStatusCode(), 204);
	}

	/** TC-API-08: US-403 TC-403.1 Register (valid details) */
	@Test(description = "Register user with valid credentials", timeOut = 30000)
	public void testRegisterValid_TC_API_08() {
		String body = "{\"email\":\"eve.holt@reqres.in\",\"password\":\"pistol\"}";
		Response response = RestAssured.given()
			.header("Content-Type", "application/json")
			.header("x-api-key", "reqres-free-v1")
			.body(body)
			.post(BASE_URL + "/register");
		org.testng.Reporter.getCurrentTestResult().setAttribute("response", response);
		org.testng.Reporter.getCurrentTestResult().setAttribute("requestBody", body);
		int status = response.getStatusCode();
		String respBody = response.getBody().asString();
		logger.info("[US-403][TC-403.1][Thread {}] Register (valid) status: {} response: {}", Thread.currentThread().getId(), status, respBody);
		Assert.assertEquals(status, 200);
		Assert.assertTrue(response.jsonPath().getString("token") != null);
	}

	/** TC-API-09: US-403 TC-403.2 Register (missing password) */
	@Test(description = "Register user with missing password (negative test)", timeOut = 30000)
	public void testRegisterMissingPassword_TC_API_09() {
		String body = "{\"email\":\"eve.holt@reqres.in\"}";
		Response response = RestAssured.given()
			.header("Content-Type", "application/json")
			.header("x-api-key", "reqres-free-v1")
			.body(body)
			.post(BASE_URL + "/register");
		org.testng.Reporter.getCurrentTestResult().setAttribute("response", response);
		org.testng.Reporter.getCurrentTestResult().setAttribute("requestBody", body);
		logger.info("[US-403][TC-403.2][Thread {}] Register (missing password) response: {}", Thread.currentThread().getId(), response.getBody().asString());
		Assert.assertEquals(response.getStatusCode(), 400);
		Assert.assertTrue(response.jsonPath().getString("error") != null);
	}

	/** TC-API-10: US-403 TC-403.3 Login (valid credentials) */
	@Test(description = "Login user with valid credentials", timeOut = 30000)
	public void testLoginValid_TC_API_10() {
		String body = "{\"email\":\"eve.holt@reqres.in\",\"password\":\"cityslicka\"}";
		Response response = RestAssured.given()
			.header("Content-Type", "application/json")
			.header("x-api-key", "reqres-free-v1")
			.body(body)
			.post(BASE_URL + "/login");
		org.testng.Reporter.getCurrentTestResult().setAttribute("response", response);
		org.testng.Reporter.getCurrentTestResult().setAttribute("requestBody", body);
		int status = response.getStatusCode();
		String respBody = response.getBody().asString();
		logger.info("[US-403][TC-403.3][Thread {}] Login (valid credentials) status: {} response: {}", Thread.currentThread().getId(), status, respBody);
		Assert.assertEquals(status, 200);
		Assert.assertTrue(response.jsonPath().getString("token") != null);
	}
}
