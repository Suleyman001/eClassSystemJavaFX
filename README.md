# 🎓 Electronic Class Record System


## Requirements to test this project
- Java 17 or higher
- Minimum 512MB RAM
- Screen Resolution: 1024x768 or higher

## Installation
1. Ensure Java 17 is installed
2. Download the JAR file
3. Run using the provided start script or:
## 📋 Project Overview

## 🚀 Project Purpose
An advanced JavaFX desktop application for managing electronic class records, designed to streamline academic record-keeping and provide an intuitive interface for educators and administrators.

## ✨ Key Features

### 👥 Student Management
- Comprehensive student record tracking
- Advanced filtering and search capabilities
- CRUD operations for student profiles
- Support for multiple classes and demographics

### 📚 Subject Management
- Dynamic subject catalog
- Category-based subject organization
- Export capabilities (CSV, Excel, PDF)
- Detailed subject information tracking

### 📊 Grading System
- Flexible mark recording
- Multiple mark types (oral, written, project)
- Real-time grade calculation
- Comprehensive mark analysis

### 💱 Exchange Rate Integration
- Real-time MNB exchange rate retrieval
- Dynamic currency rate visualization
- Historical rate tracking

### 🔀 Parallel Processing Demo
- Concurrent thread execution demonstration
- Independent counter updates
- Thread-safe implementation

## 🛠 Technology Stack

### Backend
- Java
- JavaFX
- SQLite

### Development Tools
- Maven
- Git

## 🔧 Setup and Installation

### Prerequisites
- Java Development Kit 17
- Maven
- Git

### 🚀 Installation Steps

```bash
# Clone the repository
git clone https://github.com/yourusername/eclass-system.git
cd eclass-system

# Build the project
mvn clean package

# Run the application
java -jar target/eclass-system.jar

```
### 📦 Project Structure
```
plaintext
Copy code
eclass-system/
├── src/
│   ├── main/
│   │   ├── java/        # Java source code
│   │   └── resources/   # Configuration files
│   └── test/            # Test cases
├── pom.xml              # Maven configuration
└── README.md            # Project documentation
```
### 🧪 Testing
- Test Coverage
- Unit Tests
- Integration Tests
- UI Component Tests
 
# Run tests
```mvn test```
### 📝 Planned Enhancements
- User Authentication System
- Advanced Reporting Module
- Mobile Companion App
- Enhanced Data Visualization
### 🤝 Contributing
- Fork the repository
- Create a feature branch
``` 
Copy code
git checkout -b feature/AmazingFeature
Commit your changes
```
```
Copy code
git commit -m 'Add some AmazingFeature'
Push to the branch
```
 
```git push origin feature/AmazingFeature```
- Open a Pull Request
### 📊 Project Statistics
- GitHub Stars
- GitHub Forks
- GitHub Issues

### Build the JAR:
```bash
mvn clean package
```

### Standard JAR (eClassSystemJavaFX-1.0-SNAPSHOT.jar):
- Contains only your project's compiled classes
- Does NOT include dependencies
- Requires all dependencies to be in the classpath
- Will NOT run independently
- Smaller file size
### JAR with Dependencies (eClassSystemJavaFX-1.0-SNAPSHOT-jar-with-dependencies.jar):
- Contains your project's classes
- Includes ALL required dependencies
- Can run independently
- Larger file size
- Recommended for distribution

## Troubleshooting
- Ensure you have Java 17 installed
- Check system logs for any errors

