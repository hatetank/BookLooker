package com.example.android.booklooker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_RESULTS = 40;
    private TextView tv;
    // format string for URL

    ArrayList<Book> booksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.directions_and_status_textview);

        booksList = new ArrayList<>();

        BookAdapter itemsAdapter = new BookAdapter(this, booksList, R.color.colorPrimary);

        ListView listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(itemsAdapter);
    }

    public void lookUpBooks(View view) {
        // clear the list in case it's populated from prior search
        booksList.clear();
        EditText et = (EditText) findViewById(R.id.book_keyword_edit_text);
        String bookKeyword = et.getText().toString();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadWebpageTask().execute(buildSearchURL(bookKeyword, MAX_RESULTS));
            tv.setText(R.string.connection_successful);

        } else {
            // display error
            tv.setText(R.string.connection_failed);
        }
    }
    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return callApiFromUrl(urls[0]);
            } catch (IOException e) {
                return getString(R.string.non_200_response);
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            parseReply(result);
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private String callApiFromUrl(String myurl) throws IOException {
        InputStream responseStream = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(getLocalClassName(), "The response is: " + response);
            responseStream = conn.getInputStream();

            // Convert the InputStream into a string
            return buildJSONResponseString(responseStream);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (responseStream != null) {
                responseStream.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String buildJSONResponseString(InputStream stream) throws IOException, UnsupportedEncodingException {

        StringBuilder sb=new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String read;

        while((read=br.readLine()) != null) {
            sb.append(read);
        }

        br.close();
        return sb.toString();
    }

    // private helper method to build quick queries to Google Books API
    private String buildSearchURL(String keyword, int maxResults) {
        // only handle one keyword
        String query = keyword.split(" ")[0];
        return String.format("https://www.googleapis.com/books/v1/volumes?q=%s&projection=lite&maxResults=%d",
                query,
                maxResults);
    }

    private void parseReply(String reply) {
        String data = "";
        try {
            JSONObject jsonResponseObject = new JSONObject(reply);
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray booksArray = jsonResponseObject.getJSONArray("items");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < booksArray.length(); i++){

                JSONObject volumeInfoObject = booksArray.getJSONObject(i).getJSONObject("volumeInfo");
                String title = volumeInfoObject.getString("title");
                String author = "by: ";
                JSONArray authors = volumeInfoObject.getJSONArray("authors");
                if (!authors.isNull(i)) {
                for(int j =0; j< authors.length(); j++){
                    author += authors.getString(j) + " ";
                    }
                } else {
                    author = "No author listed.";
                }
                booksList.add(buildBook(title, author));
            }
            tv.setText(String.format(Locale.getDefault(), "Here are your results!\n%d books found", booksArray.length()));
        } catch (JSONException e) {
            tv.setText(R.string.no_results_found_search);
        }
    }

    private Book buildBook(String title, String author) {
        return new Book(author, title);
    }
}
