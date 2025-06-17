package com.example.library.controller;

import java.io.IOException;
import java.time.LocalDate;
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
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class AdminController {

    @FXML private TabPane mainTabPane;
    @FXML private Tab bookManagementTab;
    @FXML private Tab returnProcessingTab;
    @FXML private Tab reportsTab;

    // Komponen Tab Manajemen Buku
    @FXML private TableView<Book> bookTableView;
    @FXML private TableColumn<Book, String> bookNoCol;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, Integer> quantityColumn;
    @FXML private TableColumn<Book, Integer> availableQuantityColumn;
    @FXML private TextField searchBookField;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    // Komponen Tab Proses Pengembalian
    @FXML private TableView<Transaction> borrowedBooksTableView; // Untuk menampilkan buku yang sedang dipinjam
    @FXML private TableColumn<Transaction, String> transNoColReturn;
    @FXML private TableColumn<Transaction, String> transIdReturnColumn;
    @FXML private TableColumn<Transaction, String> memberIdReturnColumn;
    @FXML private TableColumn<Transaction, String> bookIsbnReturnColumn;
    @FXML private TableColumn<Transaction, String> bookTitleReturnColumn;
    @FXML private TableColumn<Transaction, String> borrowDateReturnColumn;
    @FXML private TableColumn<Transaction, String> dueDateReturnColumn;
    @FXML private TextField searchTransactionField;
    private ObservableList<Transaction> borrowedTransactionsList = FXCollections.observableArrayList();

    @FXML private StackPane mainContentPane;
    @FXML private VBox dashboardPane;
    @FXML private VBox masterDataPane;
    @FXML private VBox bookManagementPane;
    @FXML private VBox returnProcessingPane;

    @FXML private Label totalBooksLabel;
    @FXML private Label totalBorrowedLabel;
    @FXML private Label totalReturnedLabel;
    @FXML private Label totalFinesLabel;

    // Dashboard: Tabel Transaksi Terkini
    @FXML private TableView<Transaction> recentTransactionsTable;
    @FXML private TableColumn<Transaction, String> transNoCol;
    @FXML private TableColumn<Transaction, String> transRegNoCol;
    @FXML private TableColumn<Transaction, String> transMemberNameCol;
    @FXML private TableColumn<Transaction, String> transBookTitleCol;
    @FXML private TableColumn<Transaction, String> transBorrowDateCol;
    @FXML private TableColumn<Transaction, String> transReturnDateCol;
    @FXML private TableColumn<Transaction, String> transStatusCol;
    @FXML private TableColumn<Transaction, String> transFineCol;

    @FXML private TableView<Member> memberTableView;
    @FXML private TableColumn<Member, String> memberNoCol;
    @FXML private TableColumn<Member, String> memberIdCol;
    @FXML private TableColumn<Member, String> memberNameCol;
    @FXML private TableColumn<Member, String> memberMajorCol;
    @FXML private TableColumn<Member, String> memberEmailCol;
    private ObservableList<Member> memberList = FXCollections.observableArrayList();

    @FXML private ComboBox<String> bulanComboBox;
    @FXML private ComboBox<String> tahunComboBox;

    @FXML
    public void initialize() {
        // Setup Tabel Buku
        bookNoCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.valueOf(bookTableView.getItems().indexOf(cellData.getValue()) + 1)));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        availableQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));
        loadBookData();
        bookTableView.setItems(bookList);

        // Setup Tabel Proses Pengembalian
        transNoColReturn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.valueOf(borrowedBooksTableView.getItems().indexOf(cellData.getValue()) + 1)));
        transIdReturnColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        memberIdReturnColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        bookIsbnReturnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        bookTitleReturnColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowDateReturnColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        dueDateReturnColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        loadBorrowedTransactionsData(); // Muat data awal
        borrowedBooksTableView.setItems(borrowedTransactionsList);

        // Setup tabel transaksi terkini (dashboard)
        transNoCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.valueOf(recentTransactionsTable.getItems().indexOf(cellData.getValue()) + 1)));
        transRegNoCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        transMemberNameCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        transBookTitleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        transBorrowDateCol.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        transReturnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        transStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        transFineCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.format("Rp %,.0f", cellData.getValue().getFine())));

        // Setup tabel member (Manage User)
        memberNoCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.valueOf(memberTableView.getItems().indexOf(cellData.getValue()) + 1)));
        memberIdCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        memberNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        memberMajorCol.setCellValueFactory(new PropertyValueFactory<>("major"));
        memberEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        memberTableView.setItems(memberList);

        // Inisialisasi tampilan awal ke dashboard
        dashboardPane.setVisible(true);
        masterDataPane.setVisible(false);
        bookManagementPane.setVisible(false);
        returnProcessingPane.setVisible(false);
        showDashboard(); // update dashboard saat pertama kali masuk

        // Setup filter bulan & tahun
        bulanComboBox.getItems().setAll(java.util.Arrays.asList(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        ));
        // Isi tahun dari data transaksi
        java.util.Set<String> tahunSet = new java.util.TreeSet<>();
        for (Transaction t : com.example.library.data.DataManager.loadTransactions()) {
            tahunSet.add(String.valueOf(LocalDate.parse(t.getBorrowDate()).getYear()));
        }
        tahunComboBox.getItems().setAll(tahunSet);
        // Default: bulan & tahun sekarang
        int bulanNow = LocalDate.now().getMonthValue();
        bulanComboBox.getSelectionModel().select(bulanNow-1);
        String tahunNow = String.valueOf(LocalDate.now().getYear());
        tahunComboBox.getSelectionModel().select(tahunNow);
    }

    private void loadBookData() {
        bookList.setAll(DataManager.loadBooks());
    }

    private void loadBorrowedTransactionsData() {
        borrowedTransactionsList.setAll(DataManager.loadTransactions().stream()
                .filter(t -> "BORROWED".equals(t.getStatus()))
                .collect(Collectors.toList()));
    }

    @FXML
    private void handleSearchBook() {
        String keyword = searchBookField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            bookTableView.setItems(bookList);
        } else {
            ObservableList<Book> filteredList = bookList.stream()
                    .filter(book -> book.getTitle().toLowerCase().contains(keyword) ||
                            book.getIsbn().toLowerCase().contains(keyword))
                    .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
            bookTableView.setItems(filteredList);
        }
    }
    @FXML
    private void resetBookSearch() {
        searchBookField.clear();
        bookTableView.setItems(bookList);
    }


    @FXML
    private void handleAddBook() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Tambah Buku Baru");
        dialog.setHeaderText("Masukkan detail buku baru");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(MainApp.getPrimaryStage());

        // Add stylesheet
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/admin_styles.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Style the cancel button
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("cancel-button");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-grid");

        TextField isbnField = new TextField();
        isbnField.setPromptText("Masukkan ISBN");
        TextField titleField = new TextField();
        titleField.setPromptText("Masukkan judul buku");
        TextField authorField = new TextField();
        authorField.setPromptText("Masukkan nama penulis");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Masukkan jumlah buku");

        // Add icons to labels
        grid.add(new Label("📚 ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("📖 Judul:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("✍ Penulis:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("📦 Jumlah:"), 0, 3);
        grid.add(quantityField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Validasi input sebelum tombol Simpan diaktifkan
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        Runnable validateInput = () -> {
            boolean disabled = isbnField.getText().trim().isEmpty() ||
                    titleField.getText().trim().isEmpty() ||
                    authorField.getText().trim().isEmpty() ||
                    quantityField.getText().trim().isEmpty();
            if (!disabled) {
                try {
                    int qty = Integer.parseInt(quantityField.getText().trim());
                    saveButton.setDisable(qty <= 0);
                } catch (NumberFormatException e) {
                    saveButton.setDisable(true);
                }
            } else {
                saveButton.setDisable(true);
            }
        };

        // Add listeners to all text fields
        isbnField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        titleField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        authorField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());

        // Initial validation
        validateInput.run();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String isbn = isbnField.getText().trim();
                    String title = titleField.getText().trim();
                    String author = authorField.getText().trim();
                    int quantity = Integer.parseInt(quantityField.getText().trim());

                    // Validasi ISBN unik
                    if (DataManager.findBookByIsbn(isbn).isPresent()) {
                        showAlert(Alert.AlertType.ERROR, "Error", "ISBN sudah terdaftar!");
                        return null;
                    }

                    return new Book(isbn, title, author, quantity, quantity);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Format angka tidak valid!");
                    return null;
                }
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            DataManager.addBook(book);
            loadBookData();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Buku berhasil ditambahkan!");
        });
    }

    @FXML
    private void handleEditBook() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih buku yang ingin diedit.");
            return;
        }

        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Edit Buku");
        dialog.setHeaderText("Edit detail buku: " + selectedBook.getTitle());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(MainApp.getPrimaryStage());

        // Add stylesheet
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/admin_styles.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Style the cancel button
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("cancel-button");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-grid");

        TextField isbnField = new TextField(selectedBook.getIsbn());
        isbnField.setEditable(false); // ISBN tidak boleh diubah karena sebagai primary key
        TextField titleField = new TextField(selectedBook.getTitle());
        TextField authorField = new TextField(selectedBook.getAuthor());
        TextField quantityField = new TextField(String.valueOf(selectedBook.getQuantity()));
        TextField availableQuantityField = new TextField(String.valueOf(selectedBook.getAvailableQuantity()));

        // Add icons to labels
        grid.add(new Label("📚 ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("📖 Judul:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("✍ Penulis:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("📦 Jumlah Total:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("📥 Jumlah Tersedia:"), 0, 4);
        grid.add(availableQuantityField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Validasi input sebelum tombol Simpan diaktifkan
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        Runnable validateInput = () -> {
            boolean disabled = titleField.getText().trim().isEmpty() ||
                    authorField.getText().trim().isEmpty() ||
                    quantityField.getText().trim().isEmpty() ||
                    availableQuantityField.getText().trim().isEmpty();
            if (!disabled) {
                try {
                    int totalQty = Integer.parseInt(quantityField.getText().trim());
                    int availQty = Integer.parseInt(availableQuantityField.getText().trim());
                    saveButton.setDisable(totalQty < 0 || availQty < 0 || availQty > totalQty);
                } catch (NumberFormatException e) {
                    saveButton.setDisable(true);
                }
            } else {
                saveButton.setDisable(true);
            }
        };

        // Add listeners to all text fields
        titleField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        authorField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        availableQuantityField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());

        // Initial validation
        validateInput.run();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int totalQty = Integer.parseInt(quantityField.getText().trim());
                    int availQty = Integer.parseInt(availableQuantityField.getText().trim());
                    
                    selectedBook.setTitle(titleField.getText().trim());
                    selectedBook.setAuthor(authorField.getText().trim());
                    selectedBook.setQuantity(totalQty);
                    selectedBook.setAvailableQuantity(availQty);
                    
                    return selectedBook;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Format angka tidak valid!");
                    return null;
                }
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            // Update the book in the database
            DataManager.updateBook(book);
            // Refresh the table
            loadBookData();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data buku berhasil diperbarui!");
        });
    }

    @FXML
    private void handleDeleteBook() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih buku yang ingin dihapus.");
            return;
        }

        // Cek apakah buku sedang dipinjam
        if (selectedBook.getQuantity() != selectedBook.getAvailableQuantity()) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Buku tidak dapat dihapus karena sedang dipinjam.\n" +
                "Jumlah dipinjam: " + (selectedBook.getQuantity() - selectedBook.getAvailableQuantity()) + " buku");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Konfirmasi Hapus");
        dialog.setHeaderText("Hapus Buku: " + selectedBook.getTitle());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(MainApp.getPrimaryStage());

        // Add stylesheet
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/admin_styles.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        dialog.getDialogPane().getStyleClass().add("delete-book-dialog");

        // Tambahkan konten konfirmasi
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("dialog-content");

        Label messageLabel = new Label("Apakah Anda yakin ingin menghapus buku ini?");
        messageLabel.getStyleClass().add("dialog-message");
        messageLabel.setWrapText(true);

        Label detailLabel = new Label(
            "ISBN: " + selectedBook.getIsbn() + "\n" +
            "Judul: " + selectedBook.getTitle() + "\n" +
            "Penulis: " + selectedBook.getAuthor() + "\n" +
            "Jumlah: " + selectedBook.getQuantity() + " buku"
        );
        detailLabel.getStyleClass().add("dialog-details");
        detailLabel.setWrapText(true);

        content.getChildren().addAll(messageLabel, detailLabel);
        dialog.getDialogPane().setContent(content);

        // Tambahkan tombol
        ButtonType confirmButtonType = new ButtonType("Hapus", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);

        // Style the buttons
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.getStyleClass().add("delete-button");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("cancel-button");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            DataManager.deleteBook(selectedBook.getIsbn());
            loadBookData();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Buku berhasil dihapus!");
        }
    }

    @FXML
    private void handleSearchTransactionToReturn() {
        String keyword = searchTransactionField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            borrowedBooksTableView.setItems(borrowedTransactionsList); // Tampilkan semua yang sedang dipinjam
        } else {
            ObservableList<Transaction> filteredList = borrowedTransactionsList.stream()
                    .filter(transaction -> transaction.getTransactionId().toLowerCase().contains(keyword) ||
                            transaction.getMemberId().toLowerCase().contains(keyword) ||
                            transaction.getIsbn().toLowerCase().contains(keyword) ||
                            transaction.getBookTitle().toLowerCase().contains(keyword))
                    .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
            borrowedBooksTableView.setItems(filteredList);
        }
    }


    @FXML
    private void handleProcessReturn() {
        Transaction selectedTransaction = borrowedBooksTableView.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih transaksi peminjaman yang ingin diproses.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Konfirmasi Pengembalian");
        dialog.setHeaderText("Proses Pengembalian Buku: " + selectedTransaction.getBookTitle());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(MainApp.getPrimaryStage());

        // Add stylesheet
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/admin_styles.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Tambahkan konten konfirmasi
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("dialog-content");

        Label messageLabel = new Label("Apakah Anda yakin ingin memproses pengembalian buku ini?");
        messageLabel.getStyleClass().add("dialog-message");
        messageLabel.setWrapText(true);

        Label detailLabel = new Label(
            "Judul Buku: " + selectedTransaction.getBookTitle() + "\n" +
            "Peminjam: " + selectedTransaction.getMemberName() + "\n" +
            "Tanggal Pinjam: " + selectedTransaction.getBorrowDate() + "\n" +
            "Tanggal Kembali: " + selectedTransaction.getReturnDate()
        );
        detailLabel.getStyleClass().add("dialog-details");
        detailLabel.setWrapText(true);

        content.getChildren().addAll(messageLabel, detailLabel);
        dialog.getDialogPane().setContent(content);

        // Tambahkan tombol
        ButtonType confirmButtonType = new ButtonType("Proses", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);

        // Style the buttons
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.getStyleClass().add("confirm-button");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("cancel-button");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            Transaction returnedTransaction = DataManager.returnBook(selectedTransaction.getTransactionId());
            if (returnedTransaction != null) {
                loadBorrowedTransactionsData(); // Muat ulang data transaksi yang sedang dipinjam
                loadBookData(); // Muat ulang data buku (kuantitas tersedia berubah)
                refreshReports(); // Muat ulang data laporan

                String fineMessage = returnedTransaction.getFine() > 0 ?
                        String.format("Buku dikembalikan terlambat. Denda: Rp %,.0f", returnedTransaction.getFine()) :
                        "Buku dikembalikan tepat waktu.";
                showAlert(Alert.AlertType.INFORMATION, "Pengembalian Berhasil",
                        "Buku '" + returnedTransaction.getBookTitle() + "' berhasil dikembalikan.\n" + fineMessage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memproses pengembalian. Transaksi mungkin sudah diproses atau tidak ditemukan.");
            }
        }
    }

    @FXML
    private void refreshReports() {
        // Kosongkan, tidak ada laporan/statistik
    }

    @FXML
    private void showBookManagement() {
        dashboardPane.setVisible(false);
        masterDataPane.setVisible(false);
        bookManagementPane.setVisible(true);
        returnProcessingPane.setVisible(false);
        loadBookData(); // Selalu muat data terbaru saat tab dipilih
    }

    @FXML
    private void showReturnProcessing() {
        dashboardPane.setVisible(false);
        masterDataPane.setVisible(false);
        bookManagementPane.setVisible(false);
        returnProcessingPane.setVisible(true);
        loadBorrowedTransactionsData(); // Selalu muat data terbaru
    }

    @FXML
    private void showReports() {
        mainTabPane.getSelectionModel().select(reportsTab);
        refreshReports(); // Selalu muat data terbaru
    }

    @FXML
    private void handleLogout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Logout");
        confirmAlert.setHeaderText("Anda akan keluar dari sesi admin.");
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
                MainApp.showLoginView();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal kembali ke halaman login.");
            }
        }
    }

    @FXML
    private void showDashboard() {
        dashboardPane.setVisible(true);
        masterDataPane.setVisible(false);
        bookManagementPane.setVisible(false);
        returnProcessingPane.setVisible(false);
        // Update statistik dashboard
        totalBooksLabel.setText(String.valueOf(DataManager.loadBooks().size()));
        totalBorrowedLabel.setText(String.valueOf(DataManager.getTotalBooksBorrowedThisMonth()));
        totalReturnedLabel.setText(String.valueOf(DataManager.getTotalBooksReturnedThisMonth()));
        totalFinesLabel.setText(String.format("Rp %,.0f", DataManager.getTotalAllFines()));
        // Update data transaksi terkini (default: bulan & tahun sekarang)
        filterTransaksiDashboard();
    }

    @FXML
    private void showTransaksi() {
        // Untuk demo, tampilkan dashboard saja atau tambahkan pane transaksi jika ada
        dashboardPane.setVisible(true);
        masterDataPane.setVisible(false);
        bookManagementPane.setVisible(false);
        returnProcessingPane.setVisible(false);
    }

    @FXML
    private void showMasterData() {
        dashboardPane.setVisible(false);
        masterDataPane.setVisible(true);
        bookManagementPane.setVisible(false);
        returnProcessingPane.setVisible(false);
        // Load data member ke tabel
        memberList.setAll(com.example.library.data.DataManager.loadMembers());
    }

    @FXML
    private void handleAddMember() {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Tambah Member Baru");
        dialog.setHeaderText("Masukkan detail member baru");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(MainApp.getPrimaryStage());

        // Add stylesheet
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/admin_styles.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Style the cancel button
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("cancel-button");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-grid");

        TextField idField = new TextField();
        idField.setPromptText("Masukkan ID Member");
        TextField nameField = new TextField();
        nameField.setPromptText("Masukkan nama lengkap");
        TextField majorField = new TextField();
        majorField.setPromptText("Masukkan jurusan");
        TextField emailField = new TextField();
        emailField.setPromptText("Masukkan email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan password");

        // Add icons to labels
        grid.add(new Label("👤 ID Member:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("👤 Nama:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("🎓 Jurusan:"), 0, 2);
        grid.add(majorField, 1, 2);
        grid.add(new Label("✉ Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("🔒 Password:"), 0, 4);
        grid.add(passwordField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Validasi input sebelum tombol Simpan diaktifkan
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        Runnable validateInput = () -> {
            boolean disabled = idField.getText().trim().isEmpty() ||
                    nameField.getText().trim().isEmpty() ||
                    majorField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty() ||
                    passwordField.getText().trim().isEmpty();
            saveButton.setDisable(disabled);
        };

        // Add listeners to all text fields
        idField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        majorField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());

        // Initial validation
        validateInput.run();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String major = majorField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();

                // Validasi ID unik
                if (DataManager.findMemberById(id).isPresent()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "ID Member sudah terdaftar!");
                    return null;
                }

                // Validasi email unik
                if (DataManager.findMemberByEmail(email).isPresent()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Email sudah terdaftar!");
                    return null;
                }

                return new Member(id, name, major, email, password);
            }
            return null;
        });

        Optional<Member> result = dialog.showAndWait();
        result.ifPresent(member -> {
            DataManager.registerMember(member);
            memberList.setAll(DataManager.loadMembers());
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Member berhasil ditambahkan!");
        });
    }

    @FXML
    private void handleEditMember() {
        Member selectedMember = memberTableView.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih member yang ingin diedit.");
            return;
        }

        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Edit detail member: " + selectedMember.getName());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(MainApp.getPrimaryStage());

        // Add stylesheet
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/admin_styles.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Style the cancel button
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("cancel-button");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-grid");

        TextField idField = new TextField(selectedMember.getMemberId());
        idField.setEditable(false); // ID tidak boleh diubah karena sebagai primary key
        TextField nameField = new TextField(selectedMember.getName());
        TextField majorField = new TextField(selectedMember.getMajor());
        TextField emailField = new TextField(selectedMember.getEmail());
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Kosongkan jika tidak ingin mengubah password");

        // Add icons to labels
        grid.add(new Label("👤 ID Member:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("👤 Nama:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("🎓 Jurusan:"), 0, 2);
        grid.add(majorField, 1, 2);
        grid.add(new Label("✉ Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("🔒 Password:"), 0, 4);
        grid.add(passwordField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Validasi input sebelum tombol Simpan diaktifkan
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        Runnable validateInput = () -> {
            boolean disabled = nameField.getText().trim().isEmpty() ||
                    majorField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty();
            saveButton.setDisable(disabled);
        };

        // Add listeners to all text fields
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        majorField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateInput.run());

        // Initial validation
        validateInput.run();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String major = majorField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();

                // Validasi email unik (kecuali email member yang sedang diedit)
                if (!email.equals(selectedMember.getEmail()) && DataManager.isEmailExists(email)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Email sudah terdaftar!");
                    return null;
                }

                selectedMember.setName(name);
                selectedMember.setMajor(major);
                selectedMember.setEmail(email);
                if (!password.isEmpty()) {
                    selectedMember.setPassword(password);
                }

                return selectedMember;
            }
            return null;
        });

        Optional<Member> result = dialog.showAndWait();
        result.ifPresent(member -> {
            // Update member di data
            java.util.List<Member> all = DataManager.loadMembers();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getMemberId().equals(member.getMemberId())) {
                    all.set(i, member);
                    break;
                }
            }
            DataManager.saveMembers(all);
            memberList.setAll(DataManager.loadMembers());
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data member berhasil diperbarui!");
        });
    }

    @FXML
    private void handleDeleteMember() {
        Member selectedMember = memberTableView.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih member yang ingin dihapus.");
            return;
        }

        // Cek apakah member sedang meminjam buku
        if (!DataManager.getBorrowedBooksByMember(selectedMember.getMemberId()).isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Member tidak dapat dihapus karena masih memiliki buku yang dipinjam.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Konfirmasi Hapus");
        dialog.setHeaderText("Hapus Member: " + selectedMember.getName());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(MainApp.getPrimaryStage());

        // Add stylesheet
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/admin_styles.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Tambahkan konten konfirmasi
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("dialog-content");

        Label messageLabel = new Label("Apakah Anda yakin ingin menghapus member ini?");
        messageLabel.getStyleClass().add("dialog-message");
        messageLabel.setWrapText(true);

        Label detailLabel = new Label(
            "ID: " + selectedMember.getMemberId() + "\n" +
            "Nama: " + selectedMember.getName() + "\n" +
            "Jurusan: " + selectedMember.getMajor() + "\n" +
            "Email: " + selectedMember.getEmail()
        );
        detailLabel.getStyleClass().add("dialog-details");
        detailLabel.setWrapText(true);

        content.getChildren().addAll(messageLabel, detailLabel);
        dialog.getDialogPane().setContent(content);

        // Tambahkan tombol
        ButtonType confirmButtonType = new ButtonType("Hapus", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);

        // Style the buttons
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.getStyleClass().add("delete-button");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("cancel-button");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            java.util.List<Member> all = DataManager.loadMembers();
            all.removeIf(m -> m.getMemberId().equals(selectedMember.getMemberId()));
            DataManager.saveMembers(all);
            memberList.setAll(DataManager.loadMembers());
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Member berhasil dihapus!");
        }
    }

    @FXML
    private void handleAddTransaction() {
        showAlert(Alert.AlertType.INFORMATION, "Tambah Transaksi", "Fitur tambah transaksi belum diimplementasikan.");
    }

    @FXML
    private void filterTransaksiDashboard() {
        int bulanIdx = bulanComboBox.getSelectionModel().getSelectedIndex() + 1;
        String tahun = tahunComboBox.getSelectionModel().getSelectedItem();
        if (bulanIdx < 1 || tahun == null) return;
        java.util.List<Transaction> all = com.example.library.data.DataManager.loadTransactions();
        java.util.Collections.reverse(all);
        javafx.collections.ObservableList<Transaction> filtered = javafx.collections.FXCollections.observableArrayList();
        for (Transaction t : all) {
            LocalDate date = LocalDate.parse(t.getBorrowDate());
            if (date.getMonthValue() == bulanIdx && String.valueOf(date.getYear()).equals(tahun)) {
                filtered.add(t);
            }
        }
        recentTransactionsTable.setItems(filtered);
    }

    @FXML
    private void resetTransactionSearch() {
        searchTransactionField.clear();
        loadBorrowedTransactionsData();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Styling untuk dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/css/alert.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("custom-dialog");

        // Tambahkan class sesuai tipe alert
        switch (alertType) {
            case ERROR:
                dialogPane.getStyleClass().add("alert-error");
                break;
            case WARNING:
                dialogPane.getStyleClass().add("alert-warning");
                break;
            case INFORMATION:
                dialogPane.getStyleClass().add("alert-information");
                break;
            case CONFIRMATION:
                dialogPane.getStyleClass().add("alert-confirmation");
                break;
        }

        // Styling untuk buttons
        for (ButtonType buttonType : dialogPane.getButtonTypes()) {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            button.getStyleClass().add("custom-button");
        }

        alert.showAndWait();
    }
}