package com.example.all4sport;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
// Import ML Kit barcode scanning
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.odml.image.MlImage;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
//Import CameraX
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;




public class app extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        FloatingActionButton floatingQrCodeButton = findViewById(R.id.qrCode);
        floatingQrCodeButton.setOnClickListener(view -> {
            // Pour la permission de la camera
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions((Activity) this,
                        new String[] { Manifest.permission.CAMERA },
                        100);
            } else {
                ListenableFuture<ProcessCameraProvider> cameraProvider = ProcessCameraProvider.getInstance(this);
                cameraProvider.addListener(() -> {
                    try {
                        ProcessCameraProvider provider = cameraProvider.get();
                        launchCamera(provider);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, ContextCompat.getMainExecutor(this));
            }
        });
    }


    @SuppressLint("RestrictedApi")
    private void launchCamera(ProcessCameraProvider cameraProvider) {
        CameraX.unbindAll();
        PreviewView previewView = new PreviewView(this);
        setContentView(previewView);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getPreviewSurfaceProvider());

        ImageCapture imageCapture = new ImageCapture.Builder().build();


        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(640, 640))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();


        // TODO : Afficher un carré sur l'écran pour scanner le QR Code

        SquareView squareView = new SquareView(this);
        squareView.setBackgroundColor(Color.TRANSPARENT);
        // Center the square with gravity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            squareView.setForegroundGravity(View.TEXT_ALIGNMENT_CENTER);
        }
        addContentView(squareView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), this::qrCode);


        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture, imageAnalysis);



    }


    private void qrCode(ImageProxy image) {
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE)
                        .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = image.getImage();
        if (mediaImage != null) {
            InputImage image1 = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());
            scanner.process(image1)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            System.out.println(rawValue);
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                    });
        }
    }
}

class SquareView extends View {
    private Paint paint = new Paint();

    public SquareView(Context context) {
        super(context);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, 640, 640, paint);
    }
}