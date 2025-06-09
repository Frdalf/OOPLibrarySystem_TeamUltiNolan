package com.example.library.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;

public class Transaction {
    private final SimpleStringProperty transactionId;
    private final SimpleStringProperty memberId;
    private final SimpleStringProperty isbn;
    private final SimpleStringProperty bookTitle; // Untuk tampilan di tabel
    private final SimpleStringProperty memberName; // Untuk tampilan di tabel
    private final SimpleStringProperty borrowDate;
    private final SimpleStringProperty dueDate;
    private final SimpleStringProperty returnDate; // Tanggal aktual pengembalian
    private final SimpleStringProperty status; // BORROWED, RETURNED, OVERDUE
    private final SimpleDoubleProperty fine; // Denda

    public Transaction(String transactionId, String memberId, String isbn, String bookTitle, String memberName,
                       LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, String status, double fine) {
        this.transactionId = new SimpleStringProperty(transactionId);
        this.memberId = new SimpleStringProperty(memberId);
        this.isbn = new SimpleStringProperty(isbn);
        this.bookTitle = new SimpleStringProperty(bookTitle);
        this.memberName = new SimpleStringProperty(memberName);
        this.borrowDate = new SimpleStringProperty(borrowDate.toString());
        this.dueDate = new SimpleStringProperty(dueDate.toString());
        this.returnDate = new SimpleStringProperty(returnDate != null ? returnDate.toString() : "-");
        this.status = new SimpleStringProperty(status);
        this.fine = new SimpleDoubleProperty(fine);
    }

    // Getter dan Setter properties
    public String getTransactionId() { return transactionId.get(); }
    public SimpleStringProperty transactionIdProperty() { return transactionId; }

    public String getMemberId() { return memberId.get(); }
    public SimpleStringProperty memberIdProperty() { return memberId; }

    public String getIsbn() { return isbn.get(); }
    public SimpleStringProperty isbnProperty() { return isbn; }

    public String getBookTitle() { return bookTitle.get(); }
    public SimpleStringProperty bookTitleProperty() { return bookTitle; }

    public String getMemberName() { return memberName.get(); }
    public SimpleStringProperty memberNameProperty() { return memberName; }


    public String getBorrowDate() { return borrowDate.get(); }
    public SimpleStringProperty borrowDateProperty() { return borrowDate; }

    public String getDueDate() { return dueDate.get(); }
    public SimpleStringProperty dueDateProperty() { return dueDate; }

    public String getReturnDate() { return returnDate.get(); }
    public void setReturnDate(LocalDate returnDate) { this.returnDate.set(returnDate != null ? returnDate.toString() : "-"); }
    public SimpleStringProperty returnDateProperty() { return returnDate; }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    public SimpleStringProperty statusProperty() { return status; }

    public double getFine() { return fine.get(); }
    public void setFine(double fine) { this.fine.set(fine); }
    public SimpleDoubleProperty fineProperty() { return fine; }
}