# Vantage Portfolio Management System

A professional desktop-based management system designed for developers and creatives to organize, showcase, and sync their project portfolios. Built with **Java Swing** and integrated with **MySQL** for robust data persistence.

> **Note:** This project is currently in **Active Development**. Features are being added and refined as part of my BSIT-MWA coursework at National University.

## 🚀 Features
* **Secure Authentication**: Login and Registration system with credentials stored in MySQL.
* **Dynamic Dashboard**: Real-time greeting and course/year level display synced with user data.
* **Profile Synchronization**: Ability to update personal information (Full Name, Student ID, Email, Bio) and reflect changes instantly across the UI.
* **Slate & Ice UI**: A modern, professional interface using a light "Ice" background with "Slate" text and indigo accents.
* **Intelligent Custom Dialogs**: Custom-built `CustomDialog` system with dynamic Success (Indigo) and Error (Red) states.

## 🔒 Security & Validation
This version implements strict data integrity checks to ensure high-quality database entries:
* **Credential Formatting**: Critical fields like `Username` and `Password` block internal spaces to prevent common login errors.
* **Validation Engines**: Implements Regex-based email verification and minimum password length requirements.
* **Binary Data Management**: Securely handles raw file data (BLOBs) for project uploads to ensure files remain accessible and uncorrupted.

## 🛠️ Tech Stack
* **Language**: Java
* **GUI Toolkit**: Java Swing
* **Database**: MySQL
* **Database Connectivity**: JDBC (MySQL Connector/J)

## 📂 Project Structure
```text
src/
 ├── main/          # Application entry point and global theme constants
 ├── db/            # Database connection and configuration
 └── gui/           # UI Components (LoginForm, MainContent, ProfilePanel, etc.)
```
## 🏗️ Installation & Setup
1. Clone the repo:

```text
git clone https://github.com/cymriccc/Student-Portfolio-System.git
```
2. Database Configuration:

* Import the provided SQL schema into your MySQL instance.

* Update `db/Database.java` with your local credentials.

3. Run the Application:

* Run `Main.java` inside the `main` package to start the application.

## 📝 Roadmap (Planned Features)
- [x] Implement Slate & Ice UI Theme
- [x] Fix CustomDialog JPanel compatibility
- [x] Build Portfolio Upload Logic
- [x] Admin Login for managing database
- [x] Create Project Gallery View
- [x] Finalize Settings & Profile Page
- [ ] Add function to Dashboard Overview
- [ ] Customize scroll bar and view image in discovery portfolio
- [ ] Add advanced filtering and search for the Project Gallery
- [ ] Direct "Open/Download" functionality for PDF assets

## ⚖️ License
This project is licensed under the MIT License — see the LICENSE file for details.
