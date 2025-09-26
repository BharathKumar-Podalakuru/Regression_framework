package com.example.ui;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FailTestDemo {
    
    @Test(description = "This test will intentionally fail to demonstrate screenshot capture")
    public void testThatWillFail() {
        System.out.println("This test is designed to fail...");
        Assert.assertTrue(false, "This test intentionally fails to demonstrate screenshot capture for failed tests");
    }
    
    @Test(description = "This test will pass to demonstrate no screenshot capture")
    public void testThatWillPass() {
        System.out.println("This test will pass...");
        Assert.assertTrue(true, "This test passes successfully");
    }
}