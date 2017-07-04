package com.bogdanorzea.booklistingapp;

import java.util.ArrayList;

class Book {
    private String mDescription;
    private String mThumbnailLink;
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

    public String getAuthors() {
        String result = "";

        int len = mAuthors.size();
        for (int i = 0; i < len - 1; i++) {
            result += mAuthors.get(i) + ", ";
        }
        result += mAuthors.get(len - 1);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();

        temp.append(getAuthors() + " -- ");
        temp.append(mTitle + " -- ");
        temp.append(mIsbn);

        return temp.toString();
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getThumbnailLink() {
        return mThumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.mThumbnailLink = thumbnailLink;
    }
}
