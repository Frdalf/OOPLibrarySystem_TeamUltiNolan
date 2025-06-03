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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
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
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, Integer> quantityColumn;
    @FXML private TableColumn<Book, Integer> availableQuantityColumn;
    @FXML private TextField searchBookField;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    // Komponen Tab Proses Pengembalian
    @FXML private TableView<Transaction> borrowedBooksTableView; // Untuk menampilkan buku yang sedang dipinjam
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

    @FXML private Label totalBooksLabel;
    @FXML private Label totalBorrowedLabel;
    @FXML private Label totalReturnedLabel;

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
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        availableQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));
        loadBookData();
        bookTableView.setItems(bookList);

        // Setup Tabel Proses Pengembalian
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
        memberIdCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        memberNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        memberMajorCol.setCellValueFactory(new PropertyValueFactory<>("major"));
        memberEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        memberTableView.setItems(memberList);

        // Inisialisasi tampilan awal ke dashboard
        dashboardPane.setVisible(true);
        masterDataPane.setVisible(false);
        bookManagementPane.setVisible(false);
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
        dialog.setHeaderText("Masukkan detail buku baru:");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(MainApp.getPrimaryStage());


        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        TextField titleField = new TextField();
        titleField.setPromptText("Judul");
        TextField authorField = new TextField();
        authorField.setPromptText("Penulis");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Jumlah Total");
        // Kuantitas tersedia akan sama dengan kuantitas total saat buku baru ditambahkan

        grid.add(new Label("ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Judul:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Penulis:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("Jumlah Total:"), 0, 3);
        grid.add(quantityField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Validasi input sebelum tombol Simpan diaktifkan
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        Runnable validateInput = () -> {
            boolean disabled = isbnField.getText().trim().isEmpty() ||
                    titleField.getText().trim().isEmpty() ||
                    authorField.getText().trim().isEmpty() ||
                    quantityField.getText().trim().isEmpty();
            if (!disabled) {
                try {
                    Integer.parseInt(quantityField.getText().trim()); // Cek apakah angka
                    saveButton.setDisable(false);
                } catch (NumberFormatException e) {
                    saveButton.setDisable(true);
                }
            } else {
                saveButton.setDisable(true);
            }
        };
        isbnField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        titleField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        authorField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        quantityField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int qty = Integer.parseInt(quantityField.getText());
                    if (qty <=0) {
                        showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Jumlah buku harus lebih dari 0.");
                        return null;
                    }
                    // Cek duplikasi ISBN
                    if (DataManager.findBookByIsbn(isbnField.getText().trim()).isPresent()) {
                        showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "ISBN sudah ada di sistem.");
                        return null;
                    }
                    return new Book(isbnField.getText(), titleField.getText(), authorField.getText(), qty, qty);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Jumlah harus berupa angka.");
                    return null;
                }
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            DataManager.addBook(book);
            loadBookData(); // Muat ulang data di tabel
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Buku berhasil ditambahkan.");
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

        ButtonType saveButtonType = new ButtonType("Simpan Perubahan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField isbnField = new TextField(selectedBook.getIsbn());
        isbnField.setEditable(false); // ISBN tidak boleh diubah karena sebagai primary key
        TextField titleField = new TextField(selectedBook.getTitle());
        TextField authorField = new TextField(selectedBook.getAuthor());
        TextField quantityField = new TextField(String.valueOf(selectedBook.getQuantity()));
        TextField availableQuantityField = new TextField(String.valueOf(selectedBook.getAvailableQuantity()));


        grid.add(new Label("ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Judul:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Penulis:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("Jumlah Total:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Jumlah Tersedia:"), 0, 4);
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
        titleField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        authorField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        quantityField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        availableQuantityField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        validateInput.run(); // Panggil sekali di awal


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int qty = Integer.parseInt(quantityField.getText());
                    int availableQty = Integer.parseInt(availableQuantityField.getText());

                    if (qty < 0 || availableQty < 0) {
                        showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Jumlah tidak boleh negatif.");
                        return null;
                    }
                    if (availableQty > qty) {
                        showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Jumlah tersedia tidak boleh melebihi jumlah total.");
                        return null;
                    }
                    // Cek apakah ada buku yang sedang dipinjam melebihi jumlah tersedia yang baru
                    int borrowedCount = selectedBook.getQuantity() - selectedBook.getAvailableQuantity();
                    if (qty - borrowedCount < 0) {
                        showAlert(Alert.AlertType.ERROR, "Input Tidak Valid",
                                "Jumlah total baru (" + qty + ") tidak mencukupi untuk buku yang sedang dipinjam (" + borrowedCount + "). " +
                                        "Setidaknya harus ada " + borrowedCount + " buku.");
                        return null;
                    }


                    return new Book(selectedBook.getIsbn(), titleField.getText(), authorField.getText(), qty, availableQty);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Jumlah harus berupa angka.");
                    return null;
                }
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(updatedBook -> {
            DataManager.updateBook(updatedBook);
            loadBookData();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Buku berhasil diperbarui.");
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
        if (selectedBook.getAvailableQuantity() < selectedBook.getQuantity()) {
            showAlert(Alert.AlertType.ERROR, "Gagal Menghapus", "Buku '" + selectedBook.getTitle() + "' sedang dipinjam dan tidak dapat dihapus.");
            return;
        }


        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText("Hapus Buku: " + selectedBook.getTitle());
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus buku ini?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DataManager.deleteBook(selectedBook.getIsbn());
            loadBookData();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Buku berhasil dihapus.");
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

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Pengembalian");
        confirmAlert.setHeaderText("Proses Pengembalian Buku: " + selectedTransaction.getBookTitle());
        confirmAlert.setContentText("Apakah Anda yakin ingin memproses pengembalian buku ini?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
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
        loadBookData(); // Selalu muat data terbaru saat tab dipilih
    }

    @FXML
    private void showReturnProcessing() {
        mainTabPane.getSelectionModel().select(returnProcessingTab);
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
        // Update statistik dashboard
        totalBooksLabel.setText(String.valueOf(DataManager.loadBooks().size()));
        totalBorrowedLabel.setText(String.valueOf(DataManager.getTotalBooksBorrowedThisMonth()));
        totalReturnedLabel.setText(String.valueOf(DataManager.getTotalBooksReturnedThisMonth()));
        // Update data transaksi terkini (default: bulan & tahun sekarang)
        filterTransaksiDashboard();
    }

    @FXML
    private void showTransaksi() {
        // Untuk demo, tampilkan dashboard saja atau tambahkan pane transaksi jika ada
        dashboardPane.setVisible(true);
        masterDataPane.setVisible(false);
    }

    @FXML
    private void showMasterData() {
        dashboardPane.setVisible(false);
        masterDataPane.setVisible(true);
        bookManagementPane.setVisible(false);
        // Load data member ke tabel
        memberList.setAll(com.example.library.data.DataManager.loadMembers());
    }

    @FXML
    private void handleAddMember() {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Tambah Member Baru");
        dialog.setHeaderText("Masukkan data member baru:");
        dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
        dialog.initOwner(com.example.library.MainApp.getPrimaryStage());

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        idField.setPromptText("ID Member");
        TextField nameField = new TextField();
        nameField.setPromptText("Nama Lengkap");
        TextField majorField = new TextField();
        majorField.setPromptText("Jurusan");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        grid.add(new Label("ID Member:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Nama Lengkap:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Jurusan:"), 0, 2);
        grid.add(majorField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(passwordField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        Runnable validateInput = () -> {
            boolean disabled = idField.getText().trim().isEmpty() ||
                    nameField.getText().trim().isEmpty() ||
                    majorField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty() ||
                    passwordField.getText().trim().isEmpty();
            saveButton.setDisable(disabled);
        };
        idField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        nameField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        majorField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        emailField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        passwordField.textProperty().addListener((obs, oldV, newV) -> validateInput.run());
        validateInput.run();

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String major = majorField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();
                // Validasi format email
                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Format email tidak valid.");
                    return null;
                }
                // Validasi ID unik
                if (com.example.library.data.DataManager.isMemberIdExists(id)) {
                    showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "ID Member sudah terdaftar.");
                    return null;
                }
                // Validasi email unik
                if (com.example.library.data.DataManager.isEmailExists(email)) {
                    showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Email sudah terdaftar.");
                    return null;
                }
                return new com.example.library.model.Member(id, name, major, email, password);
            }
            return null;
        });

        Optional<com.example.library.model.Member> result = dialog.showAndWait();
        result.ifPresent(member -> {
            com.example.library.data.DataManager.registerMember(member);
            memberList.setAll(com.example.library.data.DataManager.loadMembers());
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Member baru berhasil ditambahkan.");
        });
    }

    @FXML
    private void handleEditMember() {
        Member selected = memberTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih member yang ingin diedit.");
            return;
        }
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Edit data member:");
        dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
        dialog.initOwner(com.example.library.MainApp.getPrimaryStage());

        ButtonType saveButtonType = new ButtonType("Simpan Perubahan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField idField = new TextField(selected.getMemberId());
        idField.setEditable(false);
        TextField nameField = new TextField(selected.getName());
        TextField majorField = new TextField(selected.getMajor());
        TextField emailField = new TextField(selected.getEmail());
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password baru (opsional)");

        grid.add(new Label("ID Member:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Nama Lengkap:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Jurusan:"), 0, 2);
        grid.add(majorField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(passwordField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String major = majorField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();
                if (name.isEmpty() || major.isEmpty() || email.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Semua field (kecuali password) harus diisi.");
                    return null;
                }
                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Format email tidak valid.");
                    return null;
                }
                // Validasi email unik jika diubah
                if (!email.equals(selected.getEmail()) && com.example.library.data.DataManager.isEmailExists(email)) {
                    showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Email sudah terdaftar.");
                    return null;
                }
                String newPassword = password.isEmpty() ? selected.getPassword() : password;
                return new com.example.library.model.Member(selected.getMemberId(), name, major, email, newPassword);
            }
            return null;
        });

        Optional<com.example.library.model.Member> result = dialog.showAndWait();
        result.ifPresent(member -> {
            // Update member di data
            java.util.List<Member> all = com.example.library.data.DataManager.loadMembers();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getMemberId().equals(member.getMemberId())) {
                    all.set(i, member);
                    break;
                }
            }
            com.example.library.data.DataManager.saveMembers(all);
            memberList.setAll(com.example.library.data.DataManager.loadMembers());
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data member berhasil diperbarui.");
        });
    }

    @FXML
    private void handleDeleteMember() {
        Member selected = memberTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih member yang ingin dihapus.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus Member: " + selected.getName());
        confirm.setContentText("Apakah Anda yakin ingin menghapus member ini?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            java.util.List<Member> all = com.example.library.data.DataManager.loadMembers();
            all.removeIf(m -> m.getMemberId().equals(selected.getMemberId()));
            com.example.library.data.DataManager.saveMembers(all);
            memberList.setAll(com.example.library.data.DataManager.loadMembers());
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Member berhasil dihapus.");
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}