package com.example.newsaggregator;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetData {

    private static final String TAG = "GetData";
    private static RequestQueue queue;
    private static String getSourcesString = "https://newsapi.org/v2/sources";
    private static String getNewsString = "https://newsapi.org/v2/top-headlines";
    private static String key = "0be5a5abe8df42498673afd2ce635832";
    public static MainActivity mainActivity;

    public static void getSources(MainActivity main){
        mainActivity = main;

        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(getSourcesString).buildUpon();
        buildURL.appendQueryParameter("apiKey", key);
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> parseJSONSources(response.toString());

        Response.ErrorListener error =
                error1 -> mainActivity.onGetResource(null);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "News-App");
                        return headers;
                    }

        };

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }

    public static void getNews(String source, MainActivity main){

        mainActivity = main;
        queue = Volley.newRequestQueue(mainActivity);
        Uri.Builder buildURL = Uri.parse(getNewsString).buildUpon();
        buildURL.appendQueryParameter("sources", source);
        buildURL.appendQueryParameter("apiKey", key);
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> parseJSONNewsSource(response.toString());

        Response.ErrorListener error =
                error1 -> mainActivity.onGetNewsData(null);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "News-App");
                        return headers;
                    }

                };

        queue.add(jsonObjectRequest);
    }

    public static void parseJSONSources(String response){
        try {
            JSONObject jobj = new JSONObject(response);
            JSONArray sources = jobj.getJSONArray("sources");
            HashMap<String,SourceClass> hm = new HashMap<>();
            for(int i=0;i<sources.length();i++){
                JSONObject srcObj = (JSONObject) sources.get(i);
                hm.put(srcObj.getString("name"),
                        new SourceClass(srcObj.getString("id"), srcObj.getString("name"), srcObj.getString("category")));
            }
            Log.d(TAG, "parseJSONSources: Data Downloaded Successfully");
            mainActivity.onGetResource(hm);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void parseJSONNewsSource(String response){
        try{
            JSONObject jobj = new JSONObject(response);
            JSONArray jarray = jobj.getJSONArray("articles");
            ArrayList<ArticleClass> alist = new ArrayList<>();
            for(int i=0;i<jarray.length();i++){
                JSONObject jsonObject = jarray.getJSONObject(i);

                String author = jsonObject.has("author") ? jsonObject.getString("author") : "";
                String title = jsonObject.has("title") ? jsonObject.getString("title") : "";
                String description = jsonObject.has("description") ? jsonObject.getString("description") : "";
                String url = jsonObject.has("url") ? jsonObject.getString("url") : "";
                String urlToImage = jsonObject.has("urlToImage") ? jsonObject.getString("urlToImage") : "";
                String publishedAt = jsonObject.has("publishedAt") ? jsonObject.getString("publishedAt") : "";

                alist.add(new ArticleClass(author, title, description, url, urlToImage, publishedAt));
            }

            Log.d(TAG, "parseJSONNewsSource: Data Downloaded Successfully");
            mainActivity.onGetNewsData(alist);

        } catch(JSONException e){
            e.printStackTrace();
        }
    }
}
