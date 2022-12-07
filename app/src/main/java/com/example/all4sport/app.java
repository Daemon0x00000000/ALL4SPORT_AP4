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
import android.widget.LinearLayout;
import android.widget.TextView;
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
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.camera.core.CameraSelector.Builder;

import org.w3c.dom.Text;

import java.util.Objects;


public class app extends AppCompatActivity {
    private String referenceProduit;
    private LinearLayout layout;
    private TextView referenceView;
    private FloatingActionButton floatingQrCodeButton;

    private void setActivityView() {
        setContentView(R.layout.activity_app);
        layout = findViewById(R.id.content);
        if (referenceProduit != null) {
            referenceView = new TextView(this);
            referenceView.setText(referenceProduit);
            layout.addView(referenceView);
        } else {
            referenceView = new TextView(this);
            referenceView.setText("Aucun produit scanné");
            layout.addView(referenceView);
        }
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityView();


    }


    @SuppressLint({"RestrictedApi", "UnsafeOptInUsageError"})
    private void launchCamera(ProcessCameraProvider cameraProvider) {

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

        SquareView squareView = new SquareView(this);
        squareView.setBackgroundColor(Color.TRANSPARENT);
        addContentView(squareView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), this::qrCode);

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);


    }


    @SuppressLint("RestrictedApi")
    private void qrCode(ImageProxy imageProxy) {
        // Analyse de l'image, ne pas oublier de fermer l'image
        @SuppressLint("UnsafeOptInUsageError") InputImage image = InputImage.fromMediaImage(Objects.requireNonNull(imageProxy.getImage()), imageProxy.getImageInfo().getRotationDegrees());
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE)
                        .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    // Check for a string
                    for (Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        if (rawValue != null) {
                            referenceProduit = rawValue;
                            // Stop the camera
                            CameraX.unbindAll();
                            // Go back to the main activity
                            // Call onCreate safely
                            setActivityView();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error");
                })
                .addOnCompleteListener(task -> {
                    imageProxy.close();
                });
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

        canvas.drawRect(
                (float) (getWidth() / 2) - 320,
                (float) (getHeight() / 2) - 320,
                (float) (getWidth() / 2) + 320,
                (float) (getHeight() / 2) + 320,
                paint);
    }
}