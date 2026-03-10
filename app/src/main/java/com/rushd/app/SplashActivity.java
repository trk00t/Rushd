package com.rushd.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import androidx.appcompat.app.AppCompatActivity;

import com.rushd.app.databinding.ActivitySplashBinding;

/**
 * Splash screen shown at app launch.
 * Displays the Rushd logo with a smooth fade-in/scale animation,
 * then transitions to MainActivity after a brief delay.
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        animateLogo();
        scheduleTransition();
    }

    private void animateLogo() {
        // Fade in
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(700);

        // Gentle scale up
        ScaleAnimation scaleUp = new ScaleAnimation(
                0.85f, 1f, 0.85f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleUp.setDuration(700);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(fadeIn);
        set.addAnimation(scaleUp);
        set.setFillAfter(true);

        binding.ivLogo.startAnimation(set);
        binding.tvAppName.startAnimation(set);
    }

    private void scheduleTransition() {
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, AppConfig.SPLASH_DURATION_MS);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
