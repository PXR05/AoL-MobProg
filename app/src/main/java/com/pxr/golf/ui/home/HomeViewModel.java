package com.pxr.golf.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pxr.golf.db.DBManager;
import com.pxr.golf.models.Post;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private final DBManager db;
    private final MutableLiveData<List<Post>> posts;
    private final ExecutorService postExecutor;
    private boolean isLoadingPosts;

    public HomeViewModel(Application app) {
        db = new DBManager(app);
        posts = new MutableLiveData<>();
        postExecutor = Executors.newSingleThreadExecutor();
    }

    public MutableLiveData<List<Post>> getPosts() {
        loadPosts();
        return posts;
    }

    private void loadPosts() {
        if (isLoadingPosts) return;
        isLoadingPosts = true;
        postExecutor.execute(() -> {
            try {
                String username = "sensegolf";
                String apiUrl = "https://instagram.com/api/v1/users/web_profile_info/?username=" + username;
                StringBuilder response = fetchData(apiUrl);
                List<Post> fetchedPosts = parseInstagramPosts(response.toString());
                posts.postValue(fetchedPosts);
            } catch (Exception e) {
                Log.e("Instagram", e.toString());
            }
            isLoadingPosts = false;
        });
    }

    private StringBuilder fetchData(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("x-ig-app-id", "936619743392459");
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response;
    }

    private List<Post> parseInstagramPosts(String response) throws Exception {
        List<Post> posts = new ArrayList<>();
        JSONArray data = new JSONObject(response)
                .getJSONObject("data")
                .getJSONObject("user")
                .getJSONObject("edge_owner_to_timeline_media")
                .getJSONArray("edges");

        for (int i = 0; i < data.length(); i++) {
            JSONObject edge = data.getJSONObject(i).getJSONObject("node");
            String image = edge.getString("display_url");
            String id = edge.getString("shortcode");
            posts.add(new Post("https://instagram.com/p/" + id, image));
        }
        return posts;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        postExecutor.shutdown();
        db.close();
    }
}
