package com.example.miniprojet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojet.model.Review;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReviewCreateActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 1001;


    private EditText reviewComment;
    private RatingBar reviewRating;
    private Button takePhotoButton;
    private Button submitReviewButton;

    private TextureView mTextureView;

    private Uri photoUri;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CameraManager mCameraManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_create);
        mTextureView = findViewById(R.id.review_texture_view);
        takePhotoButton = findViewById(R.id.take_photo_button);
        submitReviewButton = findViewById(R.id.submit_review_button);
        reviewComment = findViewById(R.id.review_text);
        Button retourButton = findViewById(R.id.exitButton);
        reviewRating = findViewById(R.id.review_rating);
        retourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                    return;
                }
                openCamera();
            }
        });

        Button savePhotoButton = findViewById(R.id.save_photo_button);
        savePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhoto();
            }
        });


        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = reviewComment.getText().toString();
                float rating = reviewRating.getRating();

                // Vérifier si le commentaire et la note ne sont pas vides
                if (!comment.isEmpty() && rating > 0) {
                    // Upload review data to Firebase
                    uploadReviewData(comment, rating);
                } else {
                    Toast.makeText(ReviewCreateActivity.this, "Veuillez remplir le commentaire et la note.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void openCamera() {
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = mCameraManager.getCameraIdList()[0]; // Choix de la caméra arrière par défaut, vous pouvez modifier cela selon vos besoins
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                return;
            }
            mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraDevice = camera;
                    createCameraPreview();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    mCameraDevice = null;
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    private void createCameraPreview() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            if (texture == null) {
                // Gestion de l'erreur
                return;
            }
            texture.setDefaultBufferSize(640, 480); // Taille par défaut de la surface de prévisualisation
            Surface surface = new Surface(texture);
            final CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (mCameraDevice == null) {
                        return;
                    }

                    mCaptureSession = session;
                    try {
                        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        mCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(ReviewCreateActivity.this, "Failed to configure camera preview.", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void uploadReviewData(String comment, float rating) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String restaurantId = intent.getStringExtra("restaurantId");

        Review review = new Review(comment, rating);

        if (photoUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference photoRef = storageRef.child("restaurants/" + restaurantId + "/reviews/" + review.getId() + ".jpg");

            UploadTask uploadTask = photoRef.putFile(photoUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        review.setPhotoUrl(downloadUri.toString());
                        uploadReviewDataToFirestore(review, restaurantId);
                    } else {
                        Toast.makeText(ReviewCreateActivity.this, "Échec du téléchargement de l'image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            uploadReviewDataToFirestore(review, restaurantId);
        }
    }

    private void uploadReviewDataToFirestore(Review review, String restaurantId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference restaurantRef = db.collection("restaurant").document(restaurantId);

        review.setTimestamp(new Timestamp(new Date()));

        restaurantRef.collection("avis").document(review.getId()).set(review)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ReviewCreateActivity.this, "Avis soumis avec succès !", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReviewCreateActivity.this, "Échec de l'envoi de l'avis", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void savePhoto() {
        if (mTextureView.isAvailable()) {
            closeCamera();
            Bitmap bitmap = mTextureView.getBitmap();
            if (bitmap != null) {
                photoUri = saveBitmapAndGetUri(bitmap);
                if (photoUri != null) {
                    try {
                        mTextureView.setSurfaceTexture(new SurfaceTexture(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(this, "Image sauvegardée", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Uri saveBitmapAndGetUri(Bitmap bitmap) {
        Uri imageUri = null;
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (directory != null) {
            String fileName = "preview_image_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);

            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageUri;
    }
    private void closeCamera() {
        if (mCaptureSession != null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }



}