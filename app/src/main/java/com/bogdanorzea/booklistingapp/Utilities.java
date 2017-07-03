package com.bogdanorzea.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

class Utilities {
    private static final String LOG_TAG = Utilities.class.getSimpleName();

    public static class Utils {
        private static String makeHttpRequest(String requestUrl) throws IOException {
            String result = null;

            URL url;
            try {
                url = new URL(requestUrl);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error while creating URL", e);
                return null;
            }

            HttpURLConnection urlConnection = null;
            InputStream in = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    in = urlConnection.getInputStream();
                    result = Utils.readStream(in);
                } else {
                    Log.e(LOG_TAG, "HTTP response code was: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Cannot open the connection to the URL.");
            } finally {
                // Clean-up in case of exception
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (in != null) {
                    // Function signature must include the IOException because of this call to close
                    in.close();
                }
            }

            return result;
        }

        private static String readStream(InputStream in) {
            StringBuilder output = new StringBuilder();

            if (in != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = null;
                try {
                    line = reader.readLine();
                    while (line != null) {
                        output.append(line);
                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error reading the response stream.");
                }
            }

            return output.toString();
        }

        static ArrayList<Book> getBookArrayList(String requestUrl) {
            // TODO construct query link
            String jsonUrl = null;
            try {
                jsonUrl = makeHttpRequest(requestUrl);
            } catch (IOException e) {
                Log.e(LOG_TAG, "There was a problem freeing up resources", e);
            }

            if (TextUtils.isEmpty(jsonUrl)) {
                return null;
            }

            ArrayList<Book> bookArray = new ArrayList<>();
            JSONObject bookJson;
            try {
                bookJson = new JSONObject(jsonUrl);
                JSONArray items = bookJson.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject currentItem = items.getJSONObject(i);

                    // Google Books ID
                    String currentId = currentItem.getString("id");

                    JSONObject currentVolumeInfo = currentItem.getJSONObject("volumeInfo");

                    // Title
                    String currentTitle = currentVolumeInfo.getString("title");

                    // Authors
                    ArrayList<String> currentAuthors = new ArrayList<String>();
                    JSONArray currentAuthorsJsonArray = currentVolumeInfo.getJSONArray("authors");
                    for (int j = 0; j < currentAuthorsJsonArray.length(); j++) {
                        currentAuthors.add(currentAuthorsJsonArray.getString(j));
                    }

                    // Description may be missing from the object
                    if (currentVolumeInfo.has("description")) {
                        String currentDescription = currentVolumeInfo.getString("description");
                    }

                    // ISBN 13
                    long currentISBN = 0L;
                    JSONArray industryIdentifiersJsonArray = currentVolumeInfo.getJSONArray("industryIdentifiers");
                    for (int j = 0; j < industryIdentifiersJsonArray.length(); j++) {
                        JSONObject currentIdentifier = industryIdentifiersJsonArray.getJSONObject(j);
                        if (0 == currentIdentifier.getString("type").compareTo("ISBN_13")) {
                            currentISBN = currentIdentifier.getLong("identifier");
                            break;
                        }
                    }

                    bookArray.add(new Book(currentAuthors, currentTitle, currentId, currentISBN));
                }
            } catch (JSONException e) {
                Log.d(LOG_TAG, "Error parsing the JSON response");
                e.printStackTrace();
            }

            return bookArray;
        }
    }
}
