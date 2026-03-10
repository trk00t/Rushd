package com.rushd.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.rushd.app.databinding.ActivityMainBinding;
import com.rushd.app.webview.RushdWebChromeClient;
import com.rushd.app.webview.RushdWebViewClient;
import com.rushd.app.network.NetworkMonitor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Primary activity: hosts the WebView with pull-to-refresh,
 * file upload/download, network monitoring, and back-navigation.
 */
public class MainActivity extends AppCompatActivity
        implements NetworkMonitor.NetworkCallback {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private ActivityMainBinding binding;
    private NetworkMonitor networkMonitor;

    // File upload support
    private ValueCallback<Uri[]> filePathCallback;
    private Uri cameraImageUri;

    // ─── Activity result launchers ─────────────────────────────────────────────

    private final ActivityResultLauncher<Intent> fileChooserLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (filePathCallback == null) return;
                Uri[] results = null;
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String dataString = result.getData().getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                } else if (cameraImageUri != null) {
                    // Photo was taken with camera
                    results = new Uri[]{cameraImageUri};
                }
                filePathCallback.onReceiveValue(results);
                filePathCallback = null;
            });

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allGranted = !permissions.containsValue(false);
                if (!allGranted) {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
            });

    // ──────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupWebView();
        setupSwipeRefresh();
        setupNetworkMonitor();

        if (savedInstanceState != null) {
            binding.webView.restoreState(savedInstanceState);
        } else {
            binding.webView.loadUrl(AppConfig.WEBSITE_URL);
        }
    }

    // ─── WebView setup ─────────────────────────────────────────────────────────

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebView webView = binding.webView;

        // Security: disable debugging in production builds
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);

        // WebView settings
        var settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        // Caching
        settings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);

        // Allow mixed content for HTTPS sites that embed HTTP assets
        settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        // User-agent: add identifier for server-side mobile detection
        String defaultUa = settings.getUserAgentString();
        settings.setUserAgentString(defaultUa + " RushdApp/1.0");

        // Cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        // Clients
        webView.setWebViewClient(new RushdWebViewClient(this, binding));
        webView.setWebChromeClient(new RushdWebChromeClient(this) {
            @Override
            public boolean onShowFileChooser(WebView wv,
                                             ValueCallback<Uri[]> callback,
                                             FileChooserParams params) {
                if (filePathCallback != null) {
                    filePathCallback.onReceiveValue(null);
                }
                filePathCallback = callback;
                openFileChooser(params);
                return true;
            }
        });

        // Scroll listener – hide/show pull-to-refresh when at top
        webView.setOnScrollChangeListener((v, scrollX, scrollY, oldX, oldY) ->
                binding.swipeRefresh.setEnabled(scrollY == 0));

        // Retry button on the error screen
        binding.btnRetry.setOnClickListener(v -> {
            binding.layoutError.setVisibility(View.GONE);
            binding.swipeRefresh.setVisibility(View.VISIBLE);
            binding.webView.loadUrl(AppConfig.WEBSITE_URL);
        });
    }

    // ─── Pull-to-refresh ───────────────────────────────────────────────────────

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary, R.color.primary_dark);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.webView.reload();
        });
    }

    public void onPageLoadFinished() {
        binding.swipeRefresh.setRefreshing(false);
    }

    // ─── Network monitoring ────────────────────────────────────────────────────

    private void setupNetworkMonitor() {
        networkMonitor = new NetworkMonitor(this, this);
        networkMonitor.start();
    }

    @Override
    public void onNetworkAvailable() {
        runOnUiThread(() -> {
            binding.layoutOffline.setVisibility(View.GONE);
            binding.swipeRefresh.setVisibility(View.VISIBLE);
            // Only reload if the error page is showing
            String currentUrl = binding.webView.getUrl();
            if (currentUrl == null || currentUrl.equals("about:blank")) {
                binding.webView.loadUrl(AppConfig.WEBSITE_URL);
            }
        });
    }

    @Override
    public void onNetworkLost() {
        runOnUiThread(() -> {
            binding.layoutOffline.setVisibility(View.VISIBLE);
            binding.swipeRefresh.setVisibility(View.GONE);
        });
    }

    // ─── File upload ───────────────────────────────────────────────────────────

    private void openFileChooser(WebChromeClient.FileChooserParams params) {
        // Check & request needed permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionsIfNeeded(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
            );
        } else {
            requestPermissionsIfNeeded(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            );
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setType("*/*");

        // Camera intent
        Intent cameraIntent = null;
        try {
            cameraImageUri = createImageUri();
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        } catch (IOException e) {
            Log.e(TAG, "Failed to create image URI", e);
        }

        Intent chooser = Intent.createChooser(galleryIntent, getString(R.string.choose_file));
        if (cameraIntent != null) {
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        }

        try {
            fileChooserLauncher.launch(chooser);
        } catch (ActivityNotFoundException e) {
            filePathCallback.onReceiveValue(null);
            filePathCallback = null;
            Toast.makeText(this, R.string.no_file_app, Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createImageUri() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "RUSHD_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", imageFile);
    }

    private void requestPermissionsIfNeeded(String... permissions) {
        boolean needsRequest = false;
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                needsRequest = true;
                break;
            }
        }
        if (needsRequest) {
            permissionLauncher.launch(permissions);
        }
    }

    // ─── Back navigation ───────────────────────────────────────────────────────

    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // ─── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.webView.saveState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.webView.onResume();
    }

    @Override
    protected void onPause() {
        binding.webView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        networkMonitor.stop();
        // Proper WebView cleanup to avoid memory leaks
        binding.webView.stopLoading();
        binding.webView.clearHistory();
        binding.webView.removeAllViews();
        binding.webView.destroy();
        super.onDestroy();
    }
}
