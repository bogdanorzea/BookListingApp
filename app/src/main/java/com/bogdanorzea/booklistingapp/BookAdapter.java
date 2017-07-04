package com.bogdanorzea.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {
    private ArrayList<Book> mBooksArrayList;

    public BookAdapter(Context context, ArrayList<Book> objects) {
        super(context, 0, objects);
        mBooksArrayList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check to see if the view was re-used or not
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_entry, parent, false);
        }

        // Get the book from the list
        final Book currentBook = mBooksArrayList.get(position);

        // Set the Book authors
        TextView authorsTextView = (TextView) listItemView.findViewById(R.id.book_author);
        authorsTextView.setText(currentBook.getAuthors());

        // Set the Book title
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.book_title);
        titleTextView.setText(currentBook.getTitle());

        // Return the view
        return listItemView;
    }
}
