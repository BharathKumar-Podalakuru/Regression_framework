package com.example.ui;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScreenshotUtil {
    public static String takeScreenshot(WebDriver driver, String executionId, String testCaseId) {
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String dirPath = "artifacts/" + executionId + "/" + testCaseId + "/";
            java.nio.file.Path absPath = Paths.get(dirPath).toAbsolutePath();
            Files.createDirectories(absPath);
            System.out.println("[DEBUG] Artifact folder: " + absPath);
            String fileName = dirPath + "screenshot.png";
            File destFile = new File(fileName);
            // Overwrite if file exists
            Files.copy(srcFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
