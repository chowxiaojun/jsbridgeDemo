package com.xiroid.jsbridgedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        webView = (WebView) findViewById(R.id.webview);
        webView.addJavascriptInterface(new WebAppInterface(getApplicationContext()), "Android");
        webView.setWebViewClient(new WebViewClientImp());
        webView.setWebChromeClient(new WebChromeClientImp());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/jsdemo.html");
    }

    void javaCallJs() {
        if (webView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:javaCallJs()");
                }
            });
        }
    }

    void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

   class WebAppInterface {
        Context context;
        public WebAppInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void toast(String message) {
            if (message != null) {
                showToast(message);
            }
        }
    }

    class WebViewClientImp extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null && url.contains("jsbridge://")) {
                javaCallJs();
                showToast(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    class WebChromeClientImp extends WebChromeClient {

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            if (message != null && defaultValue != null && defaultValue.contains("jsbridge://")) {
                showToast(url + "-" + message + "-" + defaultValue);
                result.confirm();// 给js回调，否则会有问题
                return true;
            }
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            String message = consoleMessage.message();
            if (message.contains("jsbridge://")) {
                showToast(message);
                return true;
            }
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            if (message.contains("jsbridge://")) {
                showToast(message);
                result.confirm();// 给js回调，否则会有问题
                return true;
            }
            return super.onJsAlert(view, url, message, result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
