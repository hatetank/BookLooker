package com.example.android.booklooker;

public class Book {
    public static final int NO_IMAGE_PROVIDED = -1;
    private String mBookTitle;
    private String mBookAuthor;
    private int mImageResourceID = NO_IMAGE_PROVIDED;


    /**
     * Create a new book object.
     *
     * @param bookAuthor is the author of the book
     * @param bookTitle   is the book title
     */
    public Book(String bookAuthor, String bookTitle) {
        mBookTitle = bookTitle;
        mBookAuthor = bookAuthor;
    }

    /**
     * Create a new book object with an associated image
     *
     * @param bookAuthor is the author of the book
     * @param bookTitle   is the book title
     * @param imageID     is the image resource ID associated with the book.
     */
    public Book(String bookAuthor, String bookTitle, int imageID) {
        mBookTitle = bookTitle;
        mBookAuthor = bookAuthor;
        mImageResourceID = imageID;
    }

    public String getBookTitle() {
        return mBookTitle;
    }

    /**
     * @return id for the image resource for this word
     */
    public String getBookAuthor() {
        return mBookAuthor;
    }

    public boolean hasImage() {
        return mImageResourceID != NO_IMAGE_PROVIDED;
    }

    public int getImageResourceID() {
        return mImageResourceID;
    }



    @Override
    public String toString() {
        return "Book{" +
                "mBookTitle='" + mBookTitle + '\'' +
                ", mBookAuthor='" + mBookAuthor + '\'' +
                ", mImageResourceID=" + mImageResourceID +
                '}';
    }
}