package com.bogdanorzea.booklistingapp;

import java.util.ArrayList;

class Book {
    String mDescription;
    String mThumbnailLink;
    private ArrayList<String> mAuthors;
    private String mTitle;
    private String mId;
    private long mIsbn;

    public Book(ArrayList<String> authors, String title, String id, long isbn) {
        this.mAuthors = authors;
        this.mTitle = title;
        this.mIsbn = isbn;
    }

    private Book() {
    }

    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();

        temp.append(mAuthors.toString() + " -- ");
        temp.append(mTitle + " -- ");
        temp.append(mIsbn);

        return temp.toString();
    }
}
