package com.bogdanorzea.booklistingapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bogdanorzea.booklistingapp.Utilities.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String GOOGLE_BOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.text);

        new JSONQueryTask().execute();
    }

    private class JSONQueryTask extends AsyncTask<String, Void, ArrayList<Book>> {
        private static final String json = "{ \"kind\": \"books#volumes\", \"totalItems\": 796, \"items\": [  {   \"kind\": \"books#volume\",   \"id\": \"KPjmuogFmU0C\",   \"etag\": \"VSIpp1kEQjk\",   \"selfLink\": \"https://www.googleapis.com/books/v1/volumes/KPjmuogFmU0C\",   \"volumeInfo\": {    \"title\": \"Android Apps Entwicklung f\\u00FCr Dummies\",    \"authors\": [     \"Donn Felker\"    ],    \"publisher\": \"John Wiley & Sons\",    \"publishedDate\": \"2011-09-06\",    \"description\": \"Welcher Smartphone-Besitzer hatte nicht schon einmal eine kreative Idee f\\u00FCr eine eigene App? In diesem Buch erfahren Sie, wie Sie Ihre Ideen umsetzen und eigene Apps f\\u00FCr Ihr Android-Smartphone programmieren k\\u00F6nnen. Schritt f\\u00FCr Schritt erkl\\u00E4rt der Autor, wie Sie das kostenlos verf\\u00FCgbare SDK (Self Development Kit) herunterladen, mit der Programmiersoftware Eclipse arbeiten, mit der Programmiersprache Java Android Applikationen programmieren und wie Sie Ihre eigenen Apps sogar auf dem Android Markt verkaufen k\\u00F6nnen. Legen Sie los und entwickeln Sie Ihre ganz pers\\u00F6nlichen Apps!\",    \"industryIdentifiers\": [     {      \"type\": \"ISBN_13\",      \"identifier\": \"9783527707324\"     },     {      \"type\": \"ISBN_10\",      \"identifier\": \"3527707328\"     }    ],    \"readingModes\": {     \"text\": false,     \"image\": true    },    \"pageCount\": 344,    \"printType\": \"BOOK\",    \"categories\": [     \"Computers\"    ],    \"maturityRating\": \"NOT_MATURE\",    \"allowAnonLogging\": false,    \"contentVersion\": \"0.10.10.0.preview.1\",    \"imageLinks\": {     \"smallThumbnail\": \"http://books.google.com/books/content?id=KPjmuogFmU0C&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api\",     \"thumbnail\": \"http://books.google.com/books/content?id=KPjmuogFmU0C&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api\"    },    \"language\": \"de\",    \"previewLink\": \"http://books.google.ch/books?id=KPjmuogFmU0C&printsec=frontcover&dq=android&hl=&cd=1&source=gbs_api\",    \"infoLink\": \"http://books.google.ch/books?id=KPjmuogFmU0C&dq=android&hl=&source=gbs_api\",    \"canonicalVolumeLink\": \"https://books.google.com/books/about/Android_Apps_Entwicklung_f%C3%BCr_Dummies.html?hl=&id=KPjmuogFmU0C\"   },   \"saleInfo\": {    \"country\": \"CH\",    \"saleability\": \"NOT_FOR_SALE\",    \"isEbook\": false   },   \"accessInfo\": {    \"country\": \"CH\",    \"viewability\": \"PARTIAL\",    \"embeddable\": true,    \"publicDomain\": false,    \"textToSpeechPermission\": \"ALLOWED\",    \"epub\": {     \"isAvailable\": false    },    \"pdf\": {     \"isAvailable\": false    },    \"webReaderLink\": \"http://play.google.com/books/reader?id=KPjmuogFmU0C&hl=&printsec=frontcover&source=gbs_api\",    \"accessViewStatus\": \"SAMPLE\",    \"quoteSharingAllowed\": false   },   \"searchInfo\": {    \"textSnippet\": \"Welcher Smartphone-Besitzer hatte nicht schon einmal eine kreative Idee f\\u00FCr eine eigene App?\"   }  } ]}";

        @Override
        protected ArrayList<Book> doInBackground(String... urls) {
            Uri baseUri = Uri.parse(GOOGLE_BOOKS_REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();

            // TODO Add user queries and replace spaces with +
            uriBuilder.appendQueryParameter("q", "android");
            uriBuilder.appendQueryParameter("maxResults", "10");

            return Utils.getBookArrayList(uriBuilder.toString());
        }


        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            // TODO add items to a ListView
            for (int i = 0; i < books.size(); i++) {
                tv.append("\n" + books.get(i).toString());
            }
        }
    }
}
