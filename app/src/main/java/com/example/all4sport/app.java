package com.example.all4sport;

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
            Intent intent = new Intent(this, Scan.class);
            startActivity(intent);
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityView();


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

