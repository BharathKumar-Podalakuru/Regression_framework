package com.example.controller;

import com.example.model.TestResult;
import com.example.service.TestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/results")
public class TestResultController {
    /**
     * Download evidence artifact by executionId, testCaseId, and filename.
     * Example: /results/artifact/{executionId}/{testCaseId}/{filename}
     */
    @GetMapping("/artifact/{executionId}/{testCaseId}/{filename}")
    public ResponseEntity<?> downloadArtifact(
            @PathVariable String executionId,
            @PathVariable String testCaseId,
            @PathVariable String filename) {
        java.io.File file = new java.io.File("artifacts/" + executionId + "/" + testCaseId + "/" + filename);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }
        org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(file);
        String contentType = "application/octet-stream";
        if (filename.endsWith(".png")) contentType = "image/png";
        else if (filename.endsWith(".json")) contentType = "application/json";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .body(resource);
    }
    @Autowired
    private TestResultService testResultService;

    @PostMapping
    public ResponseEntity<TestResult> createTestResult(@RequestBody TestResult testResult) {
        TestResult saved = testResultService.saveTestResult(testResult);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<TestResult>> getAllTestResults() {
        List<TestResult> results = testResultService.getAllTestResults();
        return ResponseEntity.ok(results);
    }
}
