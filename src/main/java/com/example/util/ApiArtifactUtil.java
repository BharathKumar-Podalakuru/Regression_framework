package com.example.util;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApiArtifactUtil {
    
    public static String saveRequestResponseArtifacts(String executionId, String testCaseId, String requestBody, Object response) {
        try {
            String dirPath = "artifacts/" + executionId + "/" + testCaseId + "/";
            Files.createDirectories(Paths.get(dirPath));
            
            // Save request.json
            if (requestBody != null && !requestBody.isEmpty()) {
                try (FileWriter fw = new FileWriter(dirPath + "request.json")) {
                    fw.write(formatJson(requestBody));
                }
                System.out.println("[DEBUG] Saved request.json for " + testCaseId);
            }
            
            // Save response.json
            if (response != null) {
                String responseBody = null;
                try {
                    // Use reflection to avoid compile-time dependency on Rest-Assured
                    Class<?> responseClass = Class.forName("io.restassured.response.Response");
                    if (responseClass.isInstance(response)) {
                        Object body = response.getClass().getMethod("getBody").invoke(response);
                        responseBody = (String) body.getClass().getMethod("asString").invoke(body);
                    } else {
                        responseBody = response.toString();
                    }
                } catch (Exception e) {
                    // Fallback to toString if reflection fails
                    responseBody = response.toString();
                }
                
                try (FileWriter fw = new FileWriter(dirPath + "response.json")) {
                    fw.write(formatJson(responseBody));
                }
                System.out.println("[DEBUG] Saved response.json for " + testCaseId);
            }
            
            return dirPath;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to save API artifacts: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private static String formatJson(String json) {
        // Simple JSON formatting - add proper indentation if needed
        if (json == null || json.isEmpty()) {
            return "{}";
        }
        return json;
    }
}