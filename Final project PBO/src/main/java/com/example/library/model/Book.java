package com.example.library.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Book {
    private final SimpleStringProperty isbn;
    private final SimpleStringProperty title;
    private final SimpleStringProperty author;
    private final SimpleIntegerProperty quantity; // Jumlah total
    private final SimpleIntegerProperty availableQuantity; // Jumlah yang tersedia untuk dipinjam
    private final SimpleStringProperty status; // "Tersedia" atau "Dipinjam" (lebih baik dihitung dari availableQuantity)

    public Book(String isbn, String title, String author, int quantity, int availableQuantity) {
        this.isbn = new SimpleStringProperty(isbn);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.availableQuantity = new SimpleIntegerProperty(availableQuantity);
        this.status = new SimpleStringProperty(availableQuantity > 0 ? "Tersedia" : "Habis Dipinjam");
    }

    // Getter dan Setter (Penting untuk TableView)
    public String getIsbn() { return isbn.get(); }
    public void setIsbn(String isbn) { this.isbn.set(isbn); }
    public SimpleStringProperty isbnProperty() { return isbn; }

    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public SimpleStringProperty titleProperty() { return title; }

    public String getAuthor() { return author.get(); }
    public void setAuthor(String author) { this.author.set(author); }
    public SimpleStringProperty authorProperty() { return author; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public SimpleIntegerProperty quantityProperty() { return quantity; }

    public int getAvailableQuantity() { return availableQuantity.get(); }
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity.set(availableQuantity);
        this.status.set(availableQuantity > 0 ? "Tersedia" : "Habis Dipinjam");
    }
    public SimpleIntegerProperty availableQuantityProperty() { return availableQuantity; }

    public String getStatus() { return status.get(); }
    public SimpleStringProperty statusProperty() { return status; }

    @Override
    public String toString() {
        return title.get() + " oleh " + author.get();
    }
}