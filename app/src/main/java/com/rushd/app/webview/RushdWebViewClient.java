package com.rushd.app.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rushd.app.AppConfig;
import com.rushd.app.MainActivity;
import com.rushd.app.databinding.ActivityMainBinding;

/**
 * Custom WebViewClient that:
 *  - Keeps allowed-domain links inside the WebView
 *  - Opens external URLs in the device browser
 *  - Shows/hides the loading indicator
 *  - Shows the offline layout on network errors
 */
public class RushdWebViewClient extends WebViewClient {

    private final Context context;
    private final ActivityMainBinding binding;

    public RushdWebViewClient(Context context, ActivityMainBinding binding) {
        this.context = context;
        this.binding = binding;
    }

    // ─── URL interception ──────────────────────────────────────────────────────

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        String host = uri.getHost();

        if (host == null) return false;

        // Keep allowed domains in WebView
        for (String domain : AppConfig.ALLOWED_DOMAINS) {
            if (host.equals(domain) || host.endsWith("." + domain)) {
                return false; // Let WebView handle it
            }
        }

        // Special schemes: tel, mailto, etc.
        String scheme = uri.getScheme();
        if ("tel".equals(scheme) || "mailto".equals(scheme) || "whatsapp".equals(scheme)) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (Exception ignored) {}
            return true;
        }

        // All other links → open in external browser
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(browserIntent);
        } catch (Exception ignored) {}
        return true;
    }

    // ─── Loading state ─────────────────────────────────────────────────────────

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutError.setVisibility(View.GONE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        binding.progressBar.setVisibility(View.GONE);

        if (context instanceof MainActivity) {
            ((MainActivity) context).onPageLoadFinished();
        }
    }

    // ─── Error handling ────────────────────────────────────────────────────────

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request,
                                WebResourceError error) {
        // Only show the error layout for the main frame
        if (request.isForMainFrame()) {
            binding.progressBar.setVisibility(View.GONE);
            binding.layoutError.setVisibility(View.VISIBLE);
            binding.swipeRefresh.setRefreshing(false);
            // Load a blank page to clear the broken content
            view.loadUrl("about:blank");
        }
    }
}
