# Vantage Portfolio Management System

[![Java Version](https://img.shields.io/badge/Java-JDK_8%2B-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Database](https://img.shields.io/badge/Database-MySQL_8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JDBC](https://img.shields.io/badge/Driver-MySQL_Connector%2FJ-00758F?style=flat-square)](https://dev.mysql.com/downloads/connector/j/)
[![UI Style](https://img.shields.io/badge/UI_Aesthetic-Slate_%26_Ice-38BDF8?style=flat-square)](https://github.com/cymriccc/Portfolio-Management-System)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)

**Vantage Portfolio Management System** is a professional desktop-based management application engineered for developers and creatives to organize, showcase, filter, and synchronize their project portfolios. Built with **Java Swing** and integrated with **MySQL** via JDBC for secure, multi-user data persistence.

> **Academic Note:** This application was developed and completed as part of the BSIT-MWA coursework at National University.

---

## 🚀 Core Features

- **Secure Authentication & Identity Management:** Full user registration and authentication flow with credential persistence, session handling, and role-based access in MySQL.
- **Dynamic User Dashboard:** Displays real-time personalized greetings, academic course/year level metadata, and aggregated portfolio statistics synced directly with the database.
- **Profile Synchronization Engine:** Instant cross-UI reflection when updating personal information (Full Name, Student ID, Email, and Bio).
- **Project Discovery & Gallery:** Modern, filterable gallery view supporting advanced search, tags, custom scrollbar styling, image rendering, and direct "Open/Download" actions for binary PDF assets.
- **Portfolio Upload Pipeline:** Streamlined submission interface allowing users to bundle metadata, preview images, and project documentation into the cloud/database store.
- **Slate & Ice Design System:** A clean, high-contrast interface featuring a modern "Ice" canvas background, slate typography, indigo accent highlights, and custom-designed UI dialogs (`CustomDialog`) with state-aware feedback.

---

## 🔒 Security & Data Integrity

- **Strict Input Validation:** Prevents common entry errors using regex-based email validation and enforce minimum length thresholds.
- **Credential Sanitization:** Blocks internal whitespace patterns in sensitive fields (`Username` and `Password`) to eliminate authentication edge cases.
- **Binary Data Management (BLOBs):** Utilizes direct database BLOB storage streams for project file attachments and PDF documentation to ensure payload integrity.

---

## 🛠️ Tech Stack

- **Language:** Java (JDK 8 or higher)
- **GUI Framework:** Java Swing & AWT
- **Database Engine:** MySQL 8.0+
- **Connectivity:** JDBC (MySQL Connector/J)

---

## 📂 Project Structure

```text
Portfolio-Management-System/
├── src/
│   ├── db/            # MySQL connection handlers & JDBC utilities
│   ├── gui/           # Swing frames, panels (LoginForm, ProfilePanel, etc.)
│   └── main/          # Application entry point (Main.java) & global theme constants
├── .gitignore         # IDE and compilation artifact exclusions
└── README.md          # System documentation
```

---

## 💻 Installation & Setup

### Prerequisites

- **Java Development Kit (JDK) 8+** installed.
- **MySQL Server 8.0+** running locally or remotely.
- **MySQL Connector/J (`mysql-connector-j-*.jar`)** added to project classpath.

### Setup Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/cymriccc/Portfolio-Management-System.git
   ```

2. **Database Configuration:**
   * Import the project's SQL schema into your local MySQL instance.
   * Update credentials in `src/db/Database.java` (Host, Port, DB Name, User, Password).

3. **Run the Application:**
   * Execute `Main.java` inside the `src/main/` package to launch the login interface.

---

## ✅ Completed Milestones

- [x] Implement Slate & Ice UI Theme
- [x] Fix CustomDialog `JPanel` compatibility layer
- [x] Build Portfolio Upload & BLOB Persistence Logic
- [x] Admin Login and Database Administration Control
- [x] Create Dynamic Project Gallery View
- [x] Finalize Settings & Profile Customization Page
- [x] Integrate Functional Dashboard Metrics Overview
- [x] Custom Scrollbars and Visual Image Rendering in Discovery View
- [x] Advanced Filtering and Search in Project Gallery
- [x] Direct "Open/Download" PDF asset pipeline

---

## 📄 License

Distributed under the **MIT License**. See `LICENSE` for details.

---

**Author**: [@cymriccc](https://github.com/cymriccc), [@tineetot](https://github.com/tineetot)
