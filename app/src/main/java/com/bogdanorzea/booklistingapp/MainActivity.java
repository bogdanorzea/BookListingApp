package com.bogdanorzea.booklistingapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.bogdanorzea.booklistingapp.Utilities.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String GOOGLE_BOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final String ADAPTER = "ADAPTER";
    private ListView mBookListView;
    private CharSequence savedQuery = "";
    private BookAdapter mBookAdaptor;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ADAPTER, mBookAdaptor);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.search_book);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set previous query when expanded
                if (!TextUtils.isEmpty(savedQuery)) {
                    searchView.setQuery(savedQuery, false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (item != null) {
                    parseInput(query);
                    if (!TextUtils.isEmpty(query)) {
                        savedQuery = query;
                    }
                    item.collapseActionView();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });

        return true;
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
            // TODO Add empty view to display
        }
    }

    private void parseInput(String query) {
        // Construct request url
        Uri baseUri = Uri.parse(GOOGLE_BOOKS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", query.replace(" ", "%20"));
        uriBuilder.appendQueryParameter("maxResults", "10");

        new JSONQueryTask().execute(uriBuilder.toString());
    }

    private class JSONQueryTask extends AsyncTask<String, Void, ArrayList<Book>> {
        @Override
        protected ArrayList<Book> doInBackground(String... urls) {
            return Utils.getBookArrayList(urls[0]);
        }

        @Override
        protected void onPreExecute() {
            mBookListView.setAdapter(null);
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            if (books != null) {
                // Set the book adaptor
                mBookAdaptor = new BookAdapter(getApplicationContext(), books);
                mBookListView.setAdapter(mBookAdaptor);
            }
        }
    }
}
