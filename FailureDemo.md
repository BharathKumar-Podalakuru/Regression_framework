# Screenshot Capture Demo - Only for Failed Tests

## What Was Fixed

### **BEFORE** ❌
- Screenshots were taken for **EVERY test** (both passed and failed)
- This was inefficient and created unnecessary artifacts
- Did not meet your teacher's requirement

### **AFTER** ✅  
- Screenshots are taken **ONLY for FAILED tests**
- Efficient artifact storage
- Meets your teacher's requirement exactly

## How It Works Now

### **@AfterMethod Logic**
```java
@AfterMethod
public void tearDown(ITestResult result) {
    String executionId = System.getProperty("executionId", "manual");
    String testCaseId = result.getMethod().getMethodName();
    boolean isFailure = result.getStatus() == ITestResult.FAILURE;
    
    // Screenshot ONLY for failed tests
    if (isFailure && driver != null) {
        ScreenshotUtil.takeScreenshot(driver, executionId, testCaseId);
    }
}
```

### **Test Scenarios**

1. **✅ PASSED Test**: No screenshot saved
2. **❌ FAILED Test**: Screenshot automatically saved to `artifacts/{executionId}/{testCaseId}/screenshot.png`

## Demo for Your Teacher

### Step 1: Create a Test That Will Fail
Run a test that has an intentional failure:
```bash
mvn test -Dtest=BlazeDemoUITest#testBookingWithEmptyFields
```

### Step 2: Check Artifacts Folder
Only failed tests will have screenshots:
```
artifacts/
└── {executionId}/
    └── testBookingEmptyFields_TC_UI_06/
        └── screenshot.png  ← Only created for failed test
```

### Step 3: Access Failed Test Screenshot via API
```bash
GET http://localhost:8081/api/artifacts/{executionId}/testBookingEmptyFields_TC_UI_06/screenshot.png
```

## Explanation to Your Teacher

**"Ma'am, we have updated our framework so that screenshots are captured ONLY when UI tests fail. Successful tests do not create any artifacts, which makes our artifact storage efficient and focused on actual problems that need investigation."**

## Test Cases That Are Designed to Fail

1. `testBookingWithEmptyFields` - Tries to submit booking form with empty required fields
2. `testBookingWithInvalidCard` - Uses invalid credit card information

These tests will demonstrate the screenshot capture for failures.