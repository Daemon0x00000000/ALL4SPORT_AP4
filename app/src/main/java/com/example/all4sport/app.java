package com.example.all4sport;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
// Import ML Kit barcode scanning
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.odml.image.MlImage;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
//Import CameraX
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.camera.core.CameraSelector.Builder;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Objects;


public class app extends AppCompatActivity {
    private String referenceProduit;
    private LinearLayout layout;
    private TextView referenceView;
    private FloatingActionButton floatingQrCodeButton;



    private void setActivityView() {
        setContentView(R.layout.activity_app);
        this.layout = findViewById(R.id.content);
        this.referenceView = new TextView(this);
        this.referenceView.setText("Aucun produit scanné");
        this.layout.addView(this.referenceView);
        qrCodeListener();
    }

    private void qrCodeListener() {
        floatingQrCodeButton = findViewById(R.id.qrCode);
        floatingQrCodeButton.setOnClickListener(view -> {
            // Pour la permission de la camera
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions((Activity) this,
                        new String[] { Manifest.permission.CAMERA },
                        100);
            } else {
                Intent intent = new Intent(this, Scan.class);
                startActivityForResult(intent, 1);

            }


        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                // Faire quelque chose avec le résultat
                referenceProduit = result;
                this.referenceView.setText(referenceProduit);
            }
        }
    }


    private BottomNavigationView bottomNavatigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityView();
        bottomNavatigationView=findViewById(R.id.bottomNavatigationView);
        bottomNavatigationView.setBackground(null);


    }
}

