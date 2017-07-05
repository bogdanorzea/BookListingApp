package com.bogdanorzea.booklistingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bogdanorzea.booklistingapp.Utilities.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String GOOGLE_BOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final String ADAPTER = "ADAPTER";
    ProgressBar loadingProgress;
    private ListView mBookListView;
    private CharSequence savedQuery = "";
    private BookAdapter mBookAdaptor;
    private LinearLayout emptyView;
    private ImageView emptyImage;
    private TextView emptyText;

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

        // Set the book adaptor
        mBookAdaptor = new BookAdapter(getApplicationContext(), new ArrayList<Book>());
        mBookListView.setAdapter(mBookAdaptor);

        // Set the EmptyView
        emptyView = (LinearLayout) findViewById(R.id.empty_results);
        mBookListView.setEmptyView(findViewById(R.id.empty_results));
        loadingProgress = (ProgressBar) findViewById(R.id.progressBar);

        // Customize the empty view when no results are displayed
        emptyImage = (ImageView) emptyView.findViewById(R.id.search_result_image);
        emptyText = (TextView) emptyView.findViewById(R.id.search_result_message);

        if (savedInstanceState != null) {
            // Restore the adapter
            mBookAdaptor = (BookAdapter) savedInstanceState.getSerializable(ADAPTER);
            mBookListView.setAdapter(mBookAdaptor);
        } else {
            emptyText.setText(R.string.use_search);
            emptyImage.setImageResource(R.drawable.ic_info_black_48dp);
        }

        checkConnection();
    }

    private boolean checkConnection() {
        // Check for the internet connection status
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // Start the LoaderManager to retrieve Earthquake information
        if (isConnected) {
            return true;
        } else {
            emptyView.setVisibility(View.VISIBLE);
            emptyText.setText(R.string.no_internet);
            emptyImage.setImageResource(R.drawable.ic_signal_wifi_off_black_48dp);
            return false;
        }
    }

    private void parseInput(String query) {
        // Construct request url
        Uri baseUri = Uri.parse(GOOGLE_BOOKS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", query.replace(" ", "%20"));
        uriBuilder.appendQueryParameter("maxResults", "10");

        if (checkConnection()) {
            new JSONQueryTask().execute(uriBuilder.toString());
        }
    }

    private class JSONQueryTask extends AsyncTask<String, Void, ArrayList<Book>> {
        @Override
        protected ArrayList<Book> doInBackground(String... urls) {
            return Utils.getBookArrayList(urls[0]);
        }

        @Override
        protected void onPreExecute() {
            mBookAdaptor.clear();
            emptyView.setVisibility(View.GONE);
            loadingProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            loadingProgress.setVisibility(View.GONE);
            if (books == null) {

            } else if (books.size() > 0) {
                mBookAdaptor.clear();
                mBookAdaptor.addAll(books);
            } else {
                emptyView.setVisibility(View.VISIBLE);
                emptyText.setText(R.string.no_books);
                emptyImage.setImageResource(R.drawable.ic_warning_black_48dp);
            }
        }
    }
}
