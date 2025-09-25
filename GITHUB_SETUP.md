# GitHub Setup Instructions

## Preparing Your Project for GitHub

This document explains how to prepare and push your automated test framework to GitHub.

## Files Created/Modified

### 1. `.gitignore` File
A comprehensive `.gitignore` file has been created to exclude:
- Build outputs (`target/`, compiled `.class` files)
- Generated test reports (`reports/`, `test-output/`)
- Screenshots and artifacts (`screenshots/`, `artifacts/`)
- Log files (`logs/`, `*.log`)
- IDE files (`.vscode/`, `.idea/`, etc.)
- OS-specific files (`.DS_Store`, `Thumbs.db`)
- Browser drivers (`chromedriver*`, `geckodriver*`)

### 2. Configuration Security
- Created `application.properties.template` with environment variable placeholders
- **Important**: Your current `application.properties` contains hardcoded database credentials

## Steps to Push to GitHub

### 1. Initialize Git Repository (if not already done)
```bash
cd "d:\regression framework\regression framework"
git init
```

### 2. Configure Git (first time setup)
```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### 3. Add Files to Git
```bash
git add .
git status  # Check what files will be committed
```

### 4. Create Initial Commit
```bash
git commit -m "Initial commit: Automated Test Execution Framework"
```

### 5. Create GitHub Repository
1. Go to [GitHub.com](https://github.com)
2. Click "New" to create a new repository
3. Name it something like `automated-test-framework` or `regression-framework`
4. Don't initialize with README (since you already have one)
5. Copy the repository URL

### 6. Connect to GitHub and Push
```bash
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPOSITORY_NAME.git
git push -u origin main
```

## Security Recommendations

### Database Configuration
Your `application.properties` currently contains:
```properties
spring.datasource.username=root
spring.datasource.password=root
```

**Recommended approaches:**

#### Option 1: Environment Variables (Recommended)
Update your `application.properties` to use environment variables:
```properties
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}
```

Then set environment variables locally:
```bash
set DB_USERNAME=root
set DB_PASSWORD=your_actual_password
```

#### Option 2: Separate Configuration Files
Create different property files for different environments:
- `application-dev.properties` (for development)
- `application-prod.properties` (for production)

Add these to `.gitignore` if they contain sensitive data.

#### Option 3: External Configuration
Move sensitive configuration to external files not tracked by git:
- Create `config/application-local.properties`
- Add `config/` to `.gitignore`
- Reference it in your application

## What Will Be Excluded from GitHub

The `.gitignore` file will exclude these directories/files from your repository:
- `target/` - Maven build outputs
- `reports/` - Generated test reports
- `artifacts/` - Test artifacts and evidence
- `screenshots/` - UI test screenshots
- `logs/` - Application and test logs
- `test-output/` - TestNG outputs
- `.vscode/` - VS Code settings
- Various temporary and cache files

## What Will Be Included

Your repository will include:
- Source code (`src/`)
- Configuration files (`pom.xml`, `application.properties.template`)
- Documentation (`README.md`, `milestone2_summary.md`)
- TestNG configuration files
- Database migration scripts (if any)
- This setup guide

## Verification

After pushing, verify your GitHub repository contains:
- [x] Source code is present
- [x] No build artifacts (`target/` folder should not be visible)
- [x] No sensitive data (database passwords)
- [x] README.md is displayed properly
- [x] Project structure is clean and organized

## Collaboration Setup

If working with a team:
1. Add collaborators in GitHub repository settings
2. Set up branch protection rules for `main` branch
3. Create development branches for features
4. Use pull requests for code reviews

## CI/CD Integration (Optional)

Consider setting up GitHub Actions for:
- Automated testing on pull requests
- Building and testing the application
- Generating test reports
- Deploying to different environments

Create `.github/workflows/ci.yml` for automated builds and testing.