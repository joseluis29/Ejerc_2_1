package com.example.ejerc_2_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    VideoView video;
    Button btnGuardar;
    Button btnTomar;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private Uri videoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        btnGuardar.setOnClickListener(this::onClickSave);
        btnTomar.setOnClickListener(this::onClickRecord);
    }

    private void onClickRecord(View view) {
        assignPermissions();
    }

    private void assignPermissions() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA }, REQUEST_VIDEO_CAPTURE);
        }else takeVideo();
    }

    private void onClickSave(View view) {
        if (videoData != null){
            saveVideo();
        }else message("No a tomado un video aun");
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void takeVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoData = data.getData();
            video.setVideoURI(videoData);
            video.start();
        }
    }

    private void saveVideo(){
        try {
            AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(videoData, "r");
            FileInputStream fis = videoAsset.createInputStream();
            FileOutputStream fos = openFileOutput(createVideoName(), Context.MODE_PRIVATE);
            byte[] data = new byte[1024];
            int length;

            while ((length = fis.read(data)) > 0){
                fos.write(data, 0, length);
            }
            message("Video guardado exitosamente");
        }catch (IOException exception) {
            message(exception.getMessage());
        }
    }

    private String createVideoName() {
        @SuppressLint("SimpleDateFormat") String fecha;
        fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return fecha + ".mp4";
    }

    protected void init() {
        video = findViewById(R.id.txtVideo);
        btnGuardar = findViewById(R.id.btnGuardarVideo);
        btnTomar = findViewById(R.id.btnGrabarVideo);
    }

    private void message(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}