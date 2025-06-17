// src/main/java/com/example/library/controller/MemberController.java
package com.example.library.controller;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.library.MainApp;
import com.example.library.data.DataManager;
import com.example.library.model.Book;
import com.example.library.model.Member;
import com.example.library.model.Transaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MemberController {

    private Member currentMember;

    @FXML private Label welcomeLabel;
    @FXML private Label memberIdDisplayLabel;
    @FXML private TabPane memberTabPane;
    @FXML private Tab borrowBooksTab;
    @FXML private Tab myBooksTab;

    // Komponen Tab Cari & Pinjam Buku
    @FXML private TableView<Book> availableBooksTableView;
    @FXML private TableColumn<Book, String> availIsbnColumn;
    @FXML private TableColumn<Book, String> availTitleColumn;
    @FXML private TableColumn<Book, String> availAuthorColumn;
    @FXML private TableColumn<Book, String> availStatusColumn; // Menggunakan status dari Book model
    @FXML private TextField searchAvailableBookField;
    @FXML private Spinner<Integer> borrowDurationSpinner;

    private ObservableList<Book> availableBookList = FXCollections.observableArrayList();

    // Komponen Tab Buku Saya
    @FXML private TableView<Transaction> myBorrowedBooksTableView;
    @FXML private TableColumn<Transaction, String> myTransIdColumn;
    @FXML private TableColumn<Transaction, String> myBookTitleColumn;
    @FXML private TableColumn<Transaction, String> myBorrowDateColumn;
    @FXML private TableColumn<Transaction, String> myDueDateColumn;

    private ObservableList<Transaction> myBorrowedList = FXCollections.observableArrayList();

    public void setCurrentMember(Member member) {
        this.currentMember = member;
        welcomeLabel.setText("Halo, " + currentMember.getName() + "!");
        memberIdDisplayLabel.setText("ID: " + currentMember.getMemberId());
        loadMyBorrowedBooks(); // Muat buku yang dipinjam member saat ini
    }

    @FXML
    public void initialize() {
        // Setup Tabel Buku Tersedia
        availIsbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        availTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        availAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        availStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status")); // Dari Book model
        loadAvailableBookData();
        availableBooksTableView.setItems(availableBookList);

        // Setup Tabel Buku Saya
        myTransIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        myBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        myBorrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        myDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        myBorrowedBooksTableView.setItems(myBorrowedList);

        // Default durasi peminjaman
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 7);
        borrowDurationSpinner.setValueFactory(valueFactory);

        showBorrowBooksTab(); // Tampilkan tab pinjam buku secara default
    }

    private void loadAvailableBookData() {
        // Hanya tampilkan buku yang availableQuantity > 0
        availableBookList.setAll(DataManager.loadBooks().stream()
                .filter(book -> book.getAvailableQuantity() > 0)
                .collect(Collectors.toList()));
    }

    private void loadMyBorrowedBooks() {
        if (currentMember != null) {
            myBorrowedList.setAll(DataManager.getBorrowedBooksByMember(currentMember.getMemberId()));
        }
    }

    @FXML
    private void handleSearchAvailableBook() {
        String keyword = searchAvailableBookField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            availableBooksTableView.setItems(availableBookList);
        } else {
            ObservableList<Book> filteredList = availableBookList.stream()
                    .filter(book -> book.getTitle().toLowerCase().contains(keyword) ||
                            book.getIsbn().toLowerCase().contains(keyword))
                    .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
            availableBooksTableView.setItems(filteredList);
        }
    }
    @FXML
    private void resetAvailableBookSearch() {
        searchAvailableBookField.clear();
        availableBooksTableView.setItems(availableBookList);
    }


    @FXML
    private void handleBorrowBook() {
        Book selectedBook = availableBooksTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih buku yang ingin dipinjam.");
            return;
        }

        if (currentMember == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Data member tidak ditemukan. Silakan login ulang.");
            return;
        }

        if (selectedBook.getAvailableQuantity() <= 0) {
            showAlert(Alert.AlertType.INFORMATION, "Info", "Maaf, buku '" + selectedBook.getTitle() + "' sedang tidak tersedia.");
            loadAvailableBookData(); // Refresh list
            return;
        }

        int duration = borrowDurationSpinner.getValue();

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Peminjaman");
        confirmAlert.setHeaderText("Pinjam Buku: " + selectedBook.getTitle());
        confirmAlert.setContentText("Anda akan meminjam buku ini selama " + duration + " hari. Lanjutkan?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = DataManager.borrowBook(currentMember, selectedBook, duration);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Buku '" + selectedBook.getTitle() + "' berhasil dipinjam.");
                loadAvailableBookData(); // Refresh daftar buku tersedia
                loadMyBorrowedBooks();   // Refresh daftar buku saya
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal meminjam buku. Stok mungkin habis.");
                loadAvailableBookData();
            }
        }
    }

    @FXML
    private void refreshMyBorrowedBooks() {
        loadMyBorrowedBooks();
        showAlert(Alert.AlertType.INFORMATION, "Refresh", "Daftar buku pinjaman Anda telah diperbarui.");
    }


    @FXML
    private void showBorrowBooksTab() {
        memberTabPane.getSelectionModel().select(borrowBooksTab);
        loadAvailableBookData(); // Selalu muat data terbaru
    }

    @FXML
    private void showMyBooksTab() {
        memberTabPane.getSelectionModel().select(myBooksTab);
        loadMyBorrowedBooks(); // Selalu muat data terbaru
    }


    @FXML
    private void handleLogout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Logout");
        confirmAlert.setHeaderText("Anda akan keluar dari sesi member.");
        confirmAlert.setContentText("Apakah Anda yakin ingin logout?");

        // Styling untuk dialog
        DialogPane dialogPane = confirmAlert.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/css/alert.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("custom-dialog");
        dialogPane.getStyleClass().add("alert-confirmation");

        // Styling untuk buttons
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        okButton.getStyleClass().add("custom-button");
        cancelButton.getStyleClass().add("custom-button");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                currentMember = null; 
                MainApp.showLoginView();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal kembali ke halaman login.");
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleButtonHover(javafx.scene.input.MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(button.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2); -fx-translate-y: -2;");
    }

    @FXML
    private void handleButtonExit(javafx.scene.input.MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(button.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2); -fx-translate-y: -2;", ""));
    }
}