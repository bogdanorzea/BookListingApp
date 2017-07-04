package com.bogdanorzea.booklistingapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.bogdanorzea.booklistingapp.Utilities.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String GOOGLE_BOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final String ADAPTER = "ADAPTER";
    private ListView mBookListView;
    private BookAdapter mBookAdaptor;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ADAPTER, mBookAdaptor);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the book list
        mBookListView = (ListView) findViewById(R.id.book_list);

        if (savedInstanceState != null) {
            // Restore the adapter
            mBookAdaptor = (BookAdapter) savedInstanceState.getSerializable(ADAPTER);
            mBookListView.setAdapter(mBookAdaptor);
        } else {
            // Construct request url
            Uri baseUri = Uri.parse(GOOGLE_BOOKS_REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            // TODO Add user queries and replace spaces with +
            uriBuilder.appendQueryParameter("q", "android");
            uriBuilder.appendQueryParameter("maxResults", "10");

            new JSONQueryTask().execute(uriBuilder.toString());
        }
    }

    private class JSONQueryTask extends AsyncTask<String, Void, ArrayList<Book>> {
        @Override
        protected ArrayList<Book> doInBackground(String... urls) {
            return Utils.getBookArrayList(urls[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            // Set the book adaptor
            mBookAdaptor = new BookAdapter(getApplicationContext(), books);
            mBookListView.setAdapter(mBookAdaptor);
        }
    }
}
