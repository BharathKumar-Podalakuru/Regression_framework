package com.example.service;

import com.example.model.TestResult;
import com.example.repository.TestResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestResultService {
    /**
     * Update the artifact link for a test result by ID.
     */
    public TestResult updateArtifactLink(Long testResultId, String artifactLink) {
        TestResult result = testResultRepository.findById(testResultId).orElse(null);
        if (result != null) {
            result.setArtifactLink(artifactLink);
            return testResultRepository.save(result);
        }
        return null;
    }
    @Autowired
    private TestResultRepository testResultRepository;

    public TestResult saveTestResult(TestResult testResult) {
        return testResultRepository.save(testResult);
    }

    public List<TestResult> getAllTestResults() {
        return testResultRepository.findAll();
    }
}
