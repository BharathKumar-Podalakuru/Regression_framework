package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.TestNG;
import com.example.report.SuiteReportGenerator;
import java.util.Collections;

@Service
public class SuiteExecutionService {
    @Autowired
    private SuiteReportGenerator suiteReportGenerator;
    public void runSuite(String suite, String executionId) {
    System.out.println("[DEBUG] Starting suite: " + suite + ", executionId: " + executionId);
    String suiteXml = switch (suite.toLowerCase()) {
            case "blazedemo" -> "src/test/resources/blaze_smoke.xml";
            case "reqres" -> "src/test/resources/reqres_smoke.xml";
            default -> "src/test/resources/testng.xml";
        };
        System.setProperty("executionId", executionId);
        java.util.List<org.testng.xml.XmlSuite> xmlSuites;
        try {
            java.util.Collection<org.testng.xml.XmlSuite> parsedSuites = new org.testng.xml.Parser(suiteXml).parse();
            xmlSuites = new java.util.ArrayList<>(parsedSuites);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to parse suite XML: " + e.getMessage());
            return;
        }
        TestNG testng = new TestNG();
        testng.setXmlSuites(xmlSuites);
        testng.addListener(suiteReportGenerator);
        if (!xmlSuites.isEmpty()) {
            System.out.println("[DEBUG] Loaded suite: " + xmlSuites.get(0).getName() + ", tests: " + xmlSuites.get(0).getTests().size());
        }
        testng.run();
        System.out.println("[DEBUG] TestNG run completed for executionId: " + executionId);
    }
}
