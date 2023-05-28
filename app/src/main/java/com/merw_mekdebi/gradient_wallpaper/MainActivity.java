package com.merw_mekdebi.gradient_wallpaper;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.merw_mekdebi.gradient_wallpaper.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    Bitmap bitmap;
    int widthPixels;
    int heightPixels;
    int[] randomValues;
    int fixTopColor;
    int fixBottomColor;
    private ActivityMainBinding b;
    private LinearGradient gradient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(b.getRoot());

        widthPixels = getResources().getDisplayMetrics().widthPixels;
        heightPixels = getResources().getDisplayMetrics().heightPixels;

        refreshColor();
        refreshWallpaper();
        initListeners();

    }

    private void initListeners() {
        b.refreshButton.setOnClickListener(view -> {
            buttonAnimation(b.refreshButton);
            resetColorButton();
            refreshColor();
            refreshWallpaper();

        });

        b.wallpaperCard.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            i.putExtra("gradient", randomValues);
            startActivity(i);
        });

        b.topColorButton.setOnClickListener(v -> {
            fixTopColor = getRandomColor();
            b.topColorButton.setBackgroundColor(fixTopColor);
            b.extraTopColorView.setBackgroundColor(fixTopColor);
            refreshWallpaper();
        });

        b.bottomColorButton.setOnClickListener(v -> {
            fixBottomColor = getRandomColor();
            b.bottomColorButton.setBackgroundColor(fixBottomColor);
            b.extraBottomColorView.setBackgroundColor(fixBottomColor);
            refreshWallpaper();
        });

        b.angleButton.setOnClickListener(v -> {
            buttonAnimation(b.angleButton);
            refreshWallpaper();
        });

        b.toolbar.inflateMenu(R.menu.full_view_one_menu);

        b.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.set_wallpaper) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String[] options = {"Home screen", "Lock screen", "Both"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("SET WALLPAPER AS");
                    builder.setItems(options, (dialog, which) -> AsyncTask.execute(() -> setWallpaper(which + 1)));
                    builder.show();
                } else {
                    AsyncTask.execute(() -> setWallpaper(0));
                }
            }
            return false;
        });


    }

    private void setWallpaper(int where) {
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (where == 1) {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                } else if (where == 2) {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);//For Lock screen
                } else {
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);//For Lock screen
                }
            } else {
                manager.setBitmap(bitmap);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void resetColorButton() {
        b.topColorButton.setBackgroundResource(R.drawable.random);
        b.extraTopColorView.setBackgroundResource(R.drawable.random_top);
        b.bottomColorButton.setBackgroundResource(R.drawable.random);
        b.extraBottomColorView.setBackgroundResource(R.drawable.random_bottom);
    }

    private void buttonAnimation(ImageButton button) {
        button.animate().scaleX(1.3f).scaleY(1.3f).rotation(180).setDuration(250);

        button.setEnabled(false);
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            button.setEnabled(true);
            button.animate().scaleX(1).scaleY(1).rotation(0).setDuration(0);
        }, 250);

        button.clearAnimation();
    }

    private void refreshWallpaper() {
        randomValues = getRandomValues();
        gradient = createGradient(randomValues);
        bitmap = createDynamicGradient(gradient);
        b.randomImage.setImageBitmap(bitmap);
    }

    private Bitmap createDynamicGradient(LinearGradient gradient) {
        Paint p = new Paint();
        p.setDither(true);
        p.setShader(gradient);
        Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(new RectF(0, 0, widthPixels, heightPixels), p);
        return bitmap;
    }

    private int[] getRandomValues() {
        Random random = new Random();
        int[] randomValues = new int[5];
        randomValues[0] = random.nextInt(1500);
        randomValues[1] = random.nextInt(1500);
        randomValues[2] = random.nextInt(1000) + 1000;
        return randomValues;
    }

    public LinearGradient createGradient(int[] randomValues) {
        return new LinearGradient(randomValues[0], 0, randomValues[1], randomValues[2], fixTopColor, fixBottomColor, Shader.TileMode.CLAMP);
    }

    private void refreshColor() {
        fixTopColor = getRandomColor();
        fixBottomColor = getRandomColor();
    }

    private int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
