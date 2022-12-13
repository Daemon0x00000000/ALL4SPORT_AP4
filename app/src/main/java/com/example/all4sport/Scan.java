package com.example.all4sport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.util.Rational;
import android.util.Size;
import android.view.Display;
import android.widget.ImageView;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class Scan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        launchCamera();
    }

    private void launchCamera()
    {
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
                    bindView(provider);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, ContextCompat.getMainExecutor(this));
        }
    }


    @SuppressLint({"RestrictedApi", "UnsafeOptInUsageError"})
    private void bindView(ProcessCameraProvider cameraProvider) {

        PreviewView previewView = findViewById(R.id.previewView);

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Size res = new Size(previewView.getWidth(), previewView.getHeight());
        Preview preview = new Preview.Builder().setTargetResolution(res).setMaxResolution(res).build();
        preview.setSurfaceProvider(previewView.getPreviewSurfaceProvider());



        ImageCapture imageCapture = new ImageCapture.Builder().build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();


        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), this::qrCode);

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);


    }

    private Bitmap toBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public Bitmap cropBitmap(Bitmap originalBitmap,Rect rect) {
        System.out.println(originalBitmap.getWidth());
        System.out.println(originalBitmap.getHeight());
        System.out.println(rect.left);
        System.out.println(rect.top);
        System.out.println(rect.right);
        System.out.println(rect.bottom);
        return Bitmap.createBitmap(originalBitmap, rect.left, rect.top, rect.right, rect.bottom);
    }

    @SuppressLint("RestrictedApi")
    private void qrCode(ImageProxy imageProxy) {

        @SuppressLint("UnsafeOptInUsageError") Image image = imageProxy.getImage();

        /**
        System.out.println("CROP IMAGE");
        Bitmap imageBitmap = toBitmap(image);
        int x = square.getLeft();
        int y = square.getTop();
        System.out.println(square.getWidth());
        System.out.println(square.getHeight());
        System.out.println(x);
        System.out.println(y);
        System.out.println(image.getWidth());
        System.out.println(image.getHeight());


        PreviewView preview = findViewById(R.id.previewView);
        // Create ratios to crop image
        float ratio = (preview.getWidth() / (float) preview.getHeight()) / (image.getWidth() / (float) image.getHeight());

        float realX = x * ratio;
        float realY = y * ratio;
        float realWidth = (square.getWidth() * ratio);
        float realHeight = (square.getHeight() * ratio);
        // Calculate x and y to crop image
        System.out.println(preview.getWidth()+" "+preview.getHeight());
        System.out.println(ratio);
        System.out.println(realX+" "+realY+" "+realWidth+" "+realHeight);
        Bitmap realImage = Bitmap.createBitmap(imageBitmap, (int) image.getWidth()/2, (int) image.getHeight()/2, (int) (image.getWidth()-realWidth), (int) (image.getHeight()-realHeight));

        // Crop image

        // DIsplay the image
        square.setImageBitmap(realImage);
        */
        @SuppressLint("UnsafeOptInUsageError") InputImage inputImage = InputImage.fromMediaImage(image, imageProxy.getImageInfo().getRotationDegrees());

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_ALL_FORMATS)
                        .build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {

                    for (Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        System.out.println(rawValue);
                        if (rawValue != null) {
                            CameraX.unbindAll();
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