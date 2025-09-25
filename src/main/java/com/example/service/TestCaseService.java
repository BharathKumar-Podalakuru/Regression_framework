package com.example.service;

import com.example.model.TestCase;
import com.example.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestCaseService {
    @Autowired
    private TestCaseRepository testCaseRepository;

    public TestCase saveTestCase(TestCase testCase) {
        return testCaseRepository.save(testCase);
    }

    public Optional<TestCase> getTestCaseById(Long id) {
        return testCaseRepository.findById(id);
    }

    public List<TestCase> getAllTestCases() {
        return testCaseRepository.findAll();
    }
}
