package com.example.shopfinder2;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Initialize WebView
        WebView webView = findViewById(R.id.webview);

        // Set WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Set WebViewClient to open links inside the WebView
        webView.setWebViewClient(new WebViewClient());

        // Load a URL into the WebView (can be an external URL)
        String url = "https://www.google.com/aclk?sa=l&ai=DChcSEwia4P_f-qOLAxXlqmYCHdESGIYYABABGgJzbQ&ae=2&aspm=1&co=1&ase=5&gclid=CjwKCAiAqfe8BhBwEiwAsne6geRcvNXTWOD9b46bKt5H39N4M4GstVeuOiJS_nnSkBEbruVe4VHzNhoCUDoQAvD_BwE&sig=AOD64_0HgJvvEZDxb7875oBZdqIsTyp_hg&q&adurl&ved=2ahUKEwixzvrf-qOLAxVmdmwGHfNuOvwQ0Qx6BAgKEAE"; // Replace with the URL you want to show
        webView.loadUrl(url);
    }
}
