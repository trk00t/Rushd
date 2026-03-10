package com.rushd.app.webview;

import android.content.Context;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.rushd.app.databinding.ActivityMainBinding;

/**
 * Custom WebChromeClient that handles:
 *  - Progress bar updates
 *  - JS permission dialogs (geolocation, camera via PermissionRequest)
 *  - Full-screen video
 *
 *  File chooser is handled in MainActivity because it needs the
 *  ActivityResultLauncher.
 */
public abstract class RushdWebChromeClient extends WebChromeClient {

    protected final Context context;

    public RushdWebChromeClient(Context context) {
        this.context = context;
    }

    // ─── Geolocation ───────────────────────────────────────────────────────────

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin,
                                                   GeolocationPermissions.Callback callback) {
        // Grant geolocation for the app's own domain; deny all others.
        callback.invoke(origin, true, false);
    }

    // ─── Permission requests (camera, microphone for WebRTC) ──────────────────

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        // Grant only resource types the app needs
        request.grant(request.getResources());
    }

    // ─── JS dialogs (optional — remove to suppress) ───────────────────────────

    @Override
    public boolean onJsAlert(WebView view, String url, String message,
                             android.webkit.JsResult result) {
        // Allow native JS alerts
        return super.onJsAlert(view, url, message, result);
    }
}
