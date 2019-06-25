package com.recorder.demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 0x001;
    private int mDensityDpi, mWidthPixels, mHeightPixels;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 10);
        }

        initData();
        Button button = new Button(this);
        setContentView(button);
        button.setText("启动录屏服务");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                Intent intent = new Intent(manager.createScreenCaptureIntent());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void initData() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensityDpi = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
        mWidthPixels = dm.widthPixels;
        mHeightPixels = dm.heightPixels;
    }


    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(intent);
                    Toast.makeText(this, "勾选允许后,再次点击", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Intent service = new Intent(this, ScreenRecordService.class);
            service.putExtra("resultCode", resultCode);
            service.putExtra("data", data);
            service.putExtra("mWidthPixels", mWidthPixels);
            service.putExtra("mHeightPixels", mHeightPixels);
            service.putExtra("mDensityDpi", mDensityDpi);
            startService(service);

        }
    }
}
