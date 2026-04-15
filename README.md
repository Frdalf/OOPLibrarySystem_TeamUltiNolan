# OOPLibrarySystem_TeamUltiNolan
> Final Project – Object-Oriented Programming (PBO)  
> 📌 University of Muhammadiyah Malang • 2025  

# Team Members:
| Name                       | ID (NIM)           | Role                               |
|----------------------------|--------------------|------------------------------------|
| Syahrial Nur Faturrahman   | 202410370110009    | 🧠 Lead Developer, Testing & Docs |
| Naufal Muammar             | 202410370110027    | 🔧 Backend Specialist (Data Logic)|
| Farid Al Farizi            | 202410370110017    | 🎨 Frontend Specialist (JavaFX UI)|

## Campus Library 📖
## Description 📜
OOPLibrarySystem_TeamUltiNolan is a Java-based desktop application designed to assist in digital campus library management. The system is built using Java and JavaFX technologies and implements the Model-View-Controller (MVC) architecture pattern to maintain the separation of logic and presentation.

## Key Functionalities:
- 📚 **Book and Member Data Management (CRUD)**: Add, edit, delete, and search for book and library member data.
- 📥 **Book Borrowing**: Members can borrow books. Transactions are automatically recorded in the system.
- 📤 **Book Returning & Fine Calculation**: The system automatically calculates fines based on late returns.
- 🔎 **Book Search**: Interactive search by ISBN, title, or author name.
- 🔐 **User Login and Registration**: Supports login for both admins and members, as well as a new user registration form.
- ⚠️ **Input Validation and GUI Notifications**: Uses JavaFX dialogs (Alerts) to provide feedback during errors or successful actions.
- 📄 **Data Storage Using CSV**: All data is stored locally in .csv files and read back when the application is restarted.
- This project was developed to complete the **"Final Project for the Object-Oriented Programming (PBO) Course."**

## 🧠 System Architecture
This application implements the Model–View–Controller (MVC) architecture to separate business logic, user interface, and application flow control. This facilitates modular development and code maintenance.
- **Model**: Contains logic and data structures, such as Book, Member, and Transaction entities.
- **View**: Graphical interface using JavaFX and .fxml files.
- **Controller**: Handles user input and connects the View with the Model.

## 🧩 Folder Structure
- `/src` → Source code (Java).
- `/data` → CSV data.
- `/docs` → Technical documentation.
- `/presentation` → Presentation files.

## 🛠 Technologies Used
- 🧑‍💻 **Java 11**
- 🎨 **JavaFX** (GUI, Alert, TableView, SceneBuilder)
- 📦 **Maven** (Project Management)
- 📂 **File I/O** (BufferedReader / Writer) for CSV
- 🧪 **JUnit / Manual Testing**
- 💻 **IntelliJ IDEA**

## ⚙️ How to Run the Application
The application can be run in a Java 11+ environment with the help of IntelliJ IDEA and the JavaFX SDK.
1. Ensure Java JDK 11 and JavaFX SDK are installed.
2. Clone the repository:
   ```bash
   git clone https://github.com/Frdalf/OOPLibrarySystem_TeamUltiNolan.git
   ```
3. Open the `Final project PBO` folder in IntelliJ IDEA.
4. Run the `MainApp.java` file.

## 📄 License
This project was created as part of an academic assignment for the Object-Oriented Programming course.
University of Muhammadiyah Malang – 2025
📚 Used for educational purposes, not to be commercialized without permission from the development team.

## 🙌 Acknowledgments
We would like to thank our supervisor and the entire team for their cooperation and enthusiasm in completing this project. This project has been a valuable experience in collaboration, debugging, and real-world application development.
