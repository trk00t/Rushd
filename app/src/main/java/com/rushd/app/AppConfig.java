package com.rushd.app;

/**
 * Central configuration for the Rushd app.
 * Update WEBSITE_URL to your actual website.
 */
public final class AppConfig {

    // ─── REQUIRED: Set your website URL here ──────────────────────────────────
    public static final String WEBSITE_URL = "https://www.rushd.com";

    // ─── App identity ──────────────────────────────────────────────────────────
    public static final String APP_NAME    = "Rushd";

    // ─── WebView behaviour ─────────────────────────────────────────────────────
    /** Open links from these domains inside the WebView */
    public static final String[] ALLOWED_DOMAINS = {
            "rushd.com",
            "www.rushd.com"
    };

    /** How long (ms) the splash screen is shown before proceeding */
    public static final long SPLASH_DURATION_MS = 2000L;

    // Prevent instantiation
    private AppConfig() {}
}
