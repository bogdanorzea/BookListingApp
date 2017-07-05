package com.bogdanorzea.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.Toast;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

class BookAdapter extends ArrayAdapter<Book> implements Serializable {

    private static String LOG_TAG = BookAdapter.class.getSimpleName();
    private static LruCache<String, Bitmap> mMemoryCache;
    private ArrayList<Book> mBooksArrayList;

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

    @Override
    public int getCount() {
        return mBooksArrayList.size();
    }

    @Override
    public Book getItem(int position) {
        return mBooksArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        // Set the description
        TextView descriptionTextView = (TextView) listItemView.findViewById(R.id.book_description);
        descriptionTextView.setText(currentBook.getDescription());

        // Set the cover
        ImageView coverImageView = (ImageView) listItemView.findViewById(R.id.book_cover);
        String currentBookThumbnailLink = currentBook.getThumbnailLink();
        if (!TextUtils.isEmpty(currentBookThumbnailLink)) {
            coverImageView.setImageResource(R.drawable.loading_cover);
            new FetchImageTask(coverImageView).execute(currentBookThumbnailLink);
        } else {
            coverImageView.setImageResource(R.drawable.no_cover);
        }

        // Set on click listener to open website
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String previewLink = currentBook.getPreviewLink();
                if (!TextUtils.isEmpty(previewLink)) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentBook.getPreviewLink())));
                } else {
                    Toast.makeText(v.getContext(), R.string.no_preview_link, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
            String thumbnailLink = urls[0];
            Bitmap bitmap = getBitmapFromMemCache(thumbnailLink);

            if (bitmap == null) {
                try {
                    InputStream in = new java.net.URL(urls[0]).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    addBitmapToMemoryCache(urls[0], bitmap);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Error in downloading cover image", e);
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) {
                mCoverImageView.setImageResource(R.drawable.no_cover);
            } else {
                mCoverImageView.setImageBitmap(bitmap);
            }
        }
    }
}
