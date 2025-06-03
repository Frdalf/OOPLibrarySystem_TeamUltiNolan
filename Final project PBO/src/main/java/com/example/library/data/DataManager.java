package com.example.library.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.library.model.Book;
import com.example.library.model.Member;
import com.example.library.model.Transaction;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

public class DataManager {
    private static final String BOOKS_FILE = "data/books.csv";
    private static final String MEMBERS_FILE = "data/members.csv";
    private static final String TRANSACTIONS_FILE = "data/transactions.csv";
    private static final double FINE_PER_DAY = 1000; // Denda per hari keterlambatan

    // Inisialisasi file CSV jika belum ada
    public static void initializeDataFiles() {
        new File("data").mkdirs(); // Membuat direktori 'data' jika belum ada
        createFileIfNotExists(BOOKS_FILE, new String[]{"ISBN", "Title", "Author", "Quantity", "AvailableQuantity"});
        createFileIfNotExists(MEMBERS_FILE, new String[]{"MemberID", "Name", "Major", "Email", "Password"});
        createFileIfNotExists(TRANSACTIONS_FILE, new String[]{"TransactionID", "MemberID", "ISBN", "BorrowDate", "DueDate", "ReturnDate", "Status", "Fine"});

        // Tambahkan data sample jika file buku kosong (hanya untuk demo awal)
        List<Book> books = loadBooks();
        if (books.isEmpty()) {
            addBook(new Book("978-602-73156-1-0", "Pemrograman Java Dasar", "Agus Salim", 5, 5));
            addBook(new Book("978-032-17657-2-3", "Effective Java", "Joshua Bloch", 3, 3));
            addBook(new Book("978-013-23508-8-4", "Clean Code", "Robert C. Martin", 4, 4));
        }
        List<Member> members = loadMembers();
        if (members.isEmpty()) {
            registerMember(new Member("M001", "Budi Santoso", "Informatics", "budi@umm.ac.id", "password123"));
        }
    }

    private static void createFileIfNotExists(String filePath, String[] header) {
        File file = new File(filePath);
        if (!file.exists()) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
                writer.writeNext(header);
            } catch (IOException e) {
                System.err.println("Error creating file " + filePath + ": " + e.getMessage());
            }
        }
    }

    // --- Operasi CRUD Buku ---
    public static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(BOOKS_FILE))) {
            List<String[]> records = reader.readAll();
            records.remove(0); // Hapus header
            for (String[] record : records) {
                books.add(new Book(record[0], record[1], record[2], Integer.parseInt(record[3]), Integer.parseInt(record[4])));
            }
        } catch (IOException | CsvException | NumberFormatException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
        return books;
    }

    public static void saveBooks(List<Book> books) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(BOOKS_FILE))) {
            writer.writeNext(new String[]{"ISBN", "Title", "Author", "Quantity", "AvailableQuantity"}); // Header
            for (Book book : books) {
                writer.writeNext(new String[]{book.getIsbn(), book.getTitle(), book.getAuthor(), String.valueOf(book.getQuantity()), String.valueOf(book.getAvailableQuantity())});
            }
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }

    public static void addBook(Book book) {
        List<Book> books = loadBooks();
        if (books.stream().noneMatch(b -> b.getIsbn().equals(book.getIsbn()))) {
            books.add(book);
            saveBooks(books);
        } else {
            // Handle duplikat ISBN jika perlu
            System.err.println("Buku dengan ISBN " + book.getIsbn() + " sudah ada.");
        }
    }

    public static void updateBook(Book updatedBook) {
        List<Book> books = loadBooks();
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn().equals(updatedBook.getIsbn())) {
                books.set(i, updatedBook);
                saveBooks(books);
                return;
            }
        }
    }

    public static void deleteBook(String isbn) {
        List<Book> books = loadBooks();
        books.removeIf(book -> book.getIsbn().equals(isbn));
        saveBooks(books);
    }

    public static Optional<Book> findBookByIsbn(String isbn) {
        return loadBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase(isbn)).findFirst();
    }

    // --- Operasi CRUD Member ---
    public static List<Member> loadMembers() {
        List<Member> members = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(MEMBERS_FILE))) {
            List<String[]> records = reader.readAll();
            records.remove(0); // Hapus header
            for (String[] record : records) {
                members.add(new Member(record[0], record[1], record[2], record[3], record[4]));
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading members: " + e.getMessage());
        }
        return members;
    }

    public static void saveMembers(List<Member> members) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(MEMBERS_FILE))) {
            writer.writeNext(new String[]{"MemberID", "Name", "Major", "Email", "Password"}); // Header
            for (Member member : members) {
                writer.writeNext(new String[]{member.getMemberId(), member.getName(), member.getMajor(), member.getEmail(), member.getPassword()});
            }
        } catch (IOException e) {
            System.err.println("Error saving members: " + e.getMessage());
        }
    }

    public static boolean isMemberIdExists(String memberId) {
        return loadMembers().stream().anyMatch(m -> m.getMemberId().equalsIgnoreCase(memberId));
    }

    public static boolean isEmailExists(String email) {
        return loadMembers().stream().anyMatch(m -> m.getEmail().equalsIgnoreCase(email));
    }

    public static void registerMember(Member member) {
        List<Member> members = loadMembers();
        members.add(member);
        saveMembers(members);
    }

    public static Optional<Member> findMemberById(String memberId) {
        return loadMembers().stream().filter(m -> m.getMemberId().equalsIgnoreCase(memberId)).findFirst();
    }

    // --- Operasi Transaksi ---
    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(TRANSACTIONS_FILE))) {
            List<String[]> records = reader.readAll();
            records.remove(0); // Hapus header
            for (String[] record : records) {
                // Ambil judul buku dan nama member berdasarkan ID
                String bookTitle = findBookByIsbn(record[2]).map(Book::getTitle).orElse("N/A");
                String memberName = findMemberById(record[1]).map(Member::getName).orElse("N/A");

                transactions.add(new Transaction(
                        record[0], record[1], record[2], bookTitle, memberName,
                        LocalDate.parse(record[3]), LocalDate.parse(record[4]),
                        record[5].equals("-") ? null : LocalDate.parse(record[5]),
                        record[6], Double.parseDouble(record[7])
                ));
            }
        } catch (IOException | CsvException | NumberFormatException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
        return transactions;
    }

    public static void saveTransactions(List<Transaction> transactions) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(TRANSACTIONS_FILE))) {
            writer.writeNext(new String[]{"TransactionID", "MemberID", "ISBN", "BorrowDate", "DueDate", "ReturnDate", "Status", "Fine"});
            for (Transaction t : transactions) {
                writer.writeNext(new String[]{
                        t.getTransactionId(), t.getMemberId(), t.getIsbn(),
                        t.getBorrowDate(), t.getDueDate(),
                        t.getReturnDate().equals("-") ? "-" : t.getReturnDate(), // Simpan tanggal kembali atau "-"
                        t.getStatus(), String.valueOf(t.getFine())
                });
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    public static String generateTransactionId() {
        return "T" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public static boolean borrowBook(Member member, Book book, int borrowDurationDays) {
        if (book.getAvailableQuantity() > 0) {
            List<Transaction> transactions = loadTransactions();
            String transactionId = generateTransactionId();
            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowDate.plusDays(borrowDurationDays);

            Transaction newTransaction = new Transaction(transactionId, member.getMemberId(), book.getIsbn(),
                    book.getTitle(), member.getName(),
                    borrowDate, dueDate, null, "BORROWED", 0.0);
            transactions.add(newTransaction);
            saveTransactions(transactions);

            // Update kuantitas buku
            book.setAvailableQuantity(book.getAvailableQuantity() - 1);
            updateBook(book);
            return true;
        }
        return false; // Buku tidak tersedia
    }

    public static Transaction returnBook(String transactionId) {
        List<Transaction> transactions = loadTransactions();
        Optional<Transaction> optTransaction = transactions.stream()
                .filter(t -> t.getTransactionId().equals(transactionId) && t.getStatus().equals("BORROWED"))
                .findFirst();

        if (optTransaction.isPresent()) {
            Transaction transaction = optTransaction.get();
            LocalDate returnDate = LocalDate.now();
            LocalDate dueDate = LocalDate.parse(transaction.getDueDate());
            double fine = 0;

            if (returnDate.isAfter(dueDate)) {
                long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
                fine = daysLate * FINE_PER_DAY;
            }

            transaction.setReturnDate(returnDate);
            transaction.setStatus("RETURNED");
            transaction.setFine(fine);

            saveTransactions(transactions);

            // Update kuantitas buku
            Optional<Book> optBook = findBookByIsbn(transaction.getIsbn());
            if (optBook.isPresent()) {
                Book book = optBook.get();
                book.setAvailableQuantity(book.getAvailableQuantity() + 1);
                updateBook(book);
            }
            return transaction;
        }
        return null; // Transaksi tidak ditemukan atau sudah dikembalikan
    }

    public static List<Transaction> getBorrowedBooksByMember(String memberId) {
        return loadTransactions().stream()
                .filter(t -> t.getMemberId().equals(memberId) && t.getStatus().equals("BORROWED"))
                .collect(Collectors.toList());
    }

    public static List<Transaction> getAllBorrowedBooks() {
        return loadTransactions().stream()
                .filter(t -> t.getStatus().equals("BORROWED"))
                .collect(Collectors.toList());
    }

    // --- Laporan dan Statistik (Contoh Sederhana) ---
    public static int getTotalBooksBorrowedThisMonth() {
        return (int) loadTransactions().stream()
                .filter(t -> t.getStatus().equals("BORROWED"))
                .count();
    }

    public static int getTotalBooksReturnedThisMonth() {
        LocalDate today = LocalDate.now();
        return (int) loadTransactions().stream()
                .filter(t -> t.getReturnDate() != null && !t.getReturnDate().equals("-") &&
                        LocalDate.parse(t.getReturnDate()).getMonth() == today.getMonth() &&
                        LocalDate.parse(t.getReturnDate()).getYear() == today.getYear() &&
                        t.getStatus().equals("RETURNED"))
                .count();
    }

    public static double getTotalFinesCollectedThisMonth() {
        LocalDate today = LocalDate.now();
        return loadTransactions().stream()
                .filter(t -> t.getReturnDate() != null && !t.getReturnDate().equals("-") &&
                        LocalDate.parse(t.getReturnDate()).getMonth() == today.getMonth() &&
                        LocalDate.parse(t.getReturnDate()).getYear() == today.getYear() &&
                        t.getFine() > 0)
                .mapToDouble(Transaction::getFine)
                .sum();
    }
}