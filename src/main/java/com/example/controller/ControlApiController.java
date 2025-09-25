package com.example.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api")
public class ControlApiController {
    private Map<String, String> executionStatus = new HashMap<>();

    @Autowired
    private com.example.service.SuiteExecutionService suiteExecutionService;

    /**
     * POST /schedule/run — start BlazeDemo or ReqRes suite.
     * JSON: { "suite": "blazedemo" | "reqres" }
     */
    @PostMapping("/schedule/run")
    public ResponseEntity<?> scheduleRun(@RequestBody Map<String, String> body) {
        String suite = body.getOrDefault("suite", "blazedemo");
        String executionId = String.valueOf(System.currentTimeMillis());
        executionStatus.put(executionId, "RUNNING");
        try {
            suiteExecutionService.runSuite(suite, executionId);
            executionStatus.put(executionId, "COMPLETED");
        } catch (Exception e) {
            executionStatus.put(executionId, "FAILED");
        }
        return ResponseEntity.ok(Map.of("executionId", executionId, "status", executionStatus.get(executionId)));
    }

    /**
     * GET /executions/{id}/status — QUEUED/RUNNING/COMPLETED/FAILED
     */
    @GetMapping("/executions/{id}/status")
    public ResponseEntity<?> getExecutionStatus(@PathVariable String id) {
        String status = executionStatus.getOrDefault(id, "NOT_FOUND");
        return ResponseEntity.ok(Map.of("executionId", id, "status", status));
    }

    /**
     * GET /reports/{executionId}/download?type=html|csv|junit — Download report file
     */
    @GetMapping("/reports/{executionId}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable String executionId, @RequestParam String type) {
        String filename = switch (type) {
            case "html" -> "report.html";
            case "csv" -> "report.csv";
            case "junit" -> "junit-report.xml";
            default -> null;
        };
        if (filename == null) return ResponseEntity.badRequest().build();
        File file = new File("reports/" + executionId + "/" + filename);
        if (!file.exists()) return ResponseEntity.notFound().build();
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(resource);
    }

    /**
     * GET /artifacts/{executionId}/{testCaseId}/screenshot.png — Download screenshots / API logs
     */
    @GetMapping("/artifacts/{executionId}/{testCaseId}/screenshot.png")
    public ResponseEntity<Resource> downloadArtifact(@PathVariable String executionId, @PathVariable String testCaseId) {
        File file = new File("artifacts/" + executionId + "/" + testCaseId + "/screenshot.png");
        if (!file.exists()) return ResponseEntity.notFound().build();
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=screenshot.png")
                .body(resource);
    }
    
    @GetMapping("/artifacts/{executionId}/{testCaseId}/request.json")
    public ResponseEntity<Resource> downloadRequestArtifact(@PathVariable String executionId, @PathVariable String testCaseId) {
        File file = new File("artifacts/" + executionId + "/" + testCaseId + "/request.json");
        if (!file.exists()) return ResponseEntity.notFound().build();
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=request.json")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(resource);
    }
    
    @GetMapping("/artifacts/{executionId}/{testCaseId}/response.json")
    public ResponseEntity<Resource> downloadResponseArtifact(@PathVariable String executionId, @PathVariable String testCaseId) {
        File file = new File("artifacts/" + executionId + "/" + testCaseId + "/response.json");
        if (!file.exists()) return ResponseEntity.notFound().build();
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=response.json")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(resource);
    }
}
