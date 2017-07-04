package com.bogdanorzea.booklistingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

class BookAdapter extends ArrayAdapter<Book> implements Serializable {

    private static String LOG_TAG = BookAdapter.class.getSimpleName();
    private ArrayList<Book> mBooksArrayList;
    private LruCache<String, Bitmap> mMemoryCache;

    BookAdapter(Context context, ArrayList<Book> objects) {
        super(context, 0, objects);
        mBooksArrayList = objects;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
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

        // Set the authors
        TextView authorsTextView = (TextView) listItemView.findViewById(R.id.book_author);
        authorsTextView.setText(currentBook.getAuthors());

        // Set the title
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.book_title);
        titleTextView.setText(currentBook.getTitle());

        // Set the cover
        // TODO Try to implement a cache mechanism so that the image won't get downloaded every time
        String currentBookThumbnailLink = currentBook.getThumbnailLink();
        ImageView coverImageView = (ImageView) listItemView.findViewById(R.id.book_cover);

        if (!TextUtils.isEmpty(currentBookThumbnailLink)) {

            final Bitmap bitmap = getBitmapFromMemCache(currentBookThumbnailLink);
            if (bitmap != null) {
                coverImageView.setImageBitmap(bitmap);
            } else {
                coverImageView.setImageResource(R.drawable.loading_cover);
                new FetchImageTask(coverImageView).execute(currentBookThumbnailLink);
            }
        }

        // Return the view
        return listItemView;
    }

    class FetchImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView mCoverImageView;

        public FetchImageTask(ImageView imageView) {
            mCoverImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                addBitmapToMemoryCache(urls[0], bitmap);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Error in downloading cover image", e);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mCoverImageView.setImageBitmap(bitmap);
        }
    }
}
