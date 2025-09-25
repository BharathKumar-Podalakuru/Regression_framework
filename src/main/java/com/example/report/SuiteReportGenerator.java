package com.example.report;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.service.TestResultService;
import com.example.model.TestResult;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class SuiteReportGenerator implements IReporter {
    @Autowired
    private TestResultService testResultService;
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        System.out.println("[DEBUG] SuiteReportGenerator: generateReport called");
        String executionId = System.getProperty("executionId", "manual");
        String reportDir = "reports/" + executionId + "/";
        File reportFolder = new File(reportDir);
        reportFolder.mkdirs();
        System.out.println("[DEBUG] Report folder: " + reportFolder.getAbsolutePath());
        // Collect test results
        List<Map<String, String>> results = new java.util.ArrayList<>();
        for (ISuite suite : suites) {
            suite.getResults().forEach((testName, result) -> {
                result.getTestContext().getPassedTests().getAllResults().forEach(tr -> {
                    Map<String, String> m = mapResult(tr, suite.getName(), "PASSED");
                    results.add(m);
                    saveTestResultToDb(m);
                });
                result.getTestContext().getFailedTests().getAllResults().forEach(tr -> {
                    Map<String, String> m = mapResult(tr, suite.getName(), "FAILED");
                    results.add(m);
                    saveTestResultToDb(m);
                });
                result.getTestContext().getSkippedTests().getAllResults().forEach(tr -> {
                    Map<String, String> m = mapResult(tr, suite.getName(), "SKIPPED");
                    results.add(m);
                    saveTestResultToDb(m);
                });
            });
        }
        // Generate HTML, CSV, JUnit XML
        generateHtmlReport(results, reportDir + "report.html");
        generateCsvReport(results, reportDir + "report.csv");
        generateJUnitXmlReport(results, reportDir + "junit-report.xml");
    }

    private void saveTestResultToDb(Map<String, String> m) {
        try {
            if (testResultService == null) return;
            TestResult tr = new TestResult();
            tr.setTestName(m.get("TestCaseId"));
            tr.setSuiteName(m.get("Suite"));
            tr.setStatus(m.get("Status"));
            tr.setArtifactLink(m.get("ArtifactLink"));
            tr.setErrorMessage(m.get("ErrorMessage"));
            // Convert start/end time from millis to LocalDateTime
            long startMillis = Long.parseLong(m.get("StartTime"));
            long endMillis = Long.parseLong(m.get("EndTime"));
            tr.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(startMillis), ZoneId.systemDefault()));
            tr.setEndTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(endMillis), ZoneId.systemDefault()));
            tr.setDurationMs(endMillis - startMillis);
            testResultService.saveTestResult(tr);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to save test result to DB: " + e.getMessage());
        }
    }

    private Map<String, String> mapResult(org.testng.ITestResult tr, String suiteName, String status) {
        Map<String, String> m = new HashMap<>();
        m.put("TestCaseId", tr.getMethod().getMethodName());
        m.put("Name", tr.getMethod().getDescription() != null ? tr.getMethod().getDescription() : tr.getMethod().getMethodName());
        m.put("Suite", suiteName);
        m.put("Status", status);
        
        // Keep original numeric timestamps for database
        m.put("StartTime", String.valueOf(tr.getStartMillis()));
        m.put("EndTime", String.valueOf(tr.getEndMillis()));
        m.put("Duration", String.valueOf(tr.getEndMillis() - tr.getStartMillis()));
        
        // Add formatted timestamps for HTML display only
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        m.put("StartTimeFormatted", sdf.format(new java.util.Date(tr.getStartMillis())));
        m.put("EndTimeFormatted", sdf.format(new java.util.Date(tr.getEndMillis())));
        m.put("DurationFormatted", String.valueOf(tr.getEndMillis() - tr.getStartMillis()) + "ms");
        
        Object artifactPath = tr.getAttribute("artifactPath");
        m.put("ArtifactLink", artifactPath != null ? artifactPath.toString() : "");
        Throwable t = tr.getThrowable();
        m.put("ErrorMessage", t != null ? t.getMessage() : "");
        return m;
    }

    private void generateHtmlReport(List<Map<String, String>> results, String filePath) {
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write("<html><head><title>Test Report</title></head><body>");
            fw.write("<h1>Test Report</h1><table border='1'><tr>");
            for (String col : new String[]{"TestCaseId","Name","Suite","Status","StartTime","EndTime","Duration","ArtifactLink","ErrorMessage"}) {
                fw.write("<th>" + col + "</th>");
            }
            fw.write("</tr>");
            for (Map<String, String> row : results) {
                fw.write("<tr>");
                for (String col : new String[]{"TestCaseId","Name","Suite","Status","StartTime","EndTime","Duration","ArtifactLink","ErrorMessage"}) {
                    if (col.equals("ArtifactLink") && !row.get(col).isEmpty()) {
                        fw.write("<td><a href='" + row.get(col) + "'>Download</a></td>");
                    } else if (col.equals("StartTime")) {
                        fw.write("<td>" + row.get("StartTimeFormatted") + "</td>");
                    } else if (col.equals("EndTime")) {
                        fw.write("<td>" + row.get("EndTimeFormatted") + "</td>");
                    } else if (col.equals("Duration")) {
                        fw.write("<td>" + row.get("DurationFormatted") + "</td>");
                    } else {
                        fw.write("<td>" + row.get(col) + "</td>");
                    }
                }
                fw.write("</tr>");
            }
            fw.write("</table></body></html>");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void generateCsvReport(List<Map<String, String>> results, String filePath) {
        try (FileWriter fw = new FileWriter(filePath)) {
            String[] cols = {"TestCaseId","Name","Suite","Status","StartTime","EndTime","Duration","ArtifactLink","ErrorMessage"};
            fw.write(String.join(",", cols) + "\n");
            for (Map<String, String> row : results) {
                for (int i = 0; i < cols.length; i++) {
                    fw.write(row.get(cols[i]).replaceAll(",", " "));
                    if (i < cols.length - 1) fw.write(",");
                }
                fw.write("\n");
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void generateJUnitXmlReport(List<Map<String, String>> results, String filePath) {
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write("<testsuite>");
            for (Map<String, String> row : results) {
                fw.write("<testcase classname='" + row.get("Suite") + "' name='" + row.get("TestCaseId") + "'>");
                if (!row.get("ErrorMessage").isEmpty()) {
                    fw.write("<failure message='" + row.get("ErrorMessage") + "'/>");
                }
                if (!row.get("ArtifactLink").isEmpty()) {
                    fw.write("<system-out>Artifact: " + row.get("ArtifactLink") + "</system-out>");
                }
                fw.write("</testcase>");
            }
            fw.write("</testsuite>");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
