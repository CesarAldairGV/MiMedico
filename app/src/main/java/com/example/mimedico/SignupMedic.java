package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mimedico.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SignupMedic extends AppCompatActivity {

    private int SELECT_PHOTO = 1;

    private EditText years, institution;

    private Button selectProof;
    private Button selectPhoto;
    private Button sendButton;
    private Uri proof;
    private Uri photo;
    private ImageView photoView;
    private ImageView proofView;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private static final String PHOTO = "PHOTO";
    private static final String PROOF = "PROOF";

    private String actualSelect;

    private User user;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_medic);

        years = findViewById(R.id.signupMedicAgeOfExperience);
        institution = findViewById(R.id.signupMedicSchool);

        sendButton = findViewById(R.id.signupMedicSendButton);
        selectPhoto = findViewById(R.id.signupMedicSelectPhoto);
        selectProof = findViewById(R.id.signupMedicSelectProof);
        proofView = findViewById(R.id.signupMedicProofView);
        photoView = findViewById(R.id.signupMedicPhotoView);
        progressBar = findViewById(R.id.signupMedicProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        selectPhoto.setOnClickListener(this::selectPhoto);
        selectProof.setOnClickListener(this::selectProof);
        sendButton.setOnClickListener(this::sendImages);

        firebaseDatabase.getReference("users")
                .orderByChild("email")
                .equalTo(firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getChildren().iterator().next().getValue(User.class);
                        userId = user.getId();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void selectProof(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        actualSelect = PROOF;
        startActivityForResult(intent, SELECT_PHOTO);
    }

    public void selectPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        actualSelect = PHOTO;
        startActivityForResult(intent, SELECT_PHOTO);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (actualSelect.equals(PROOF)) {
                proof = data.getData();
            } else {
                photo = data.getData();
            }
            try {
                if (actualSelect.equals(PROOF)) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), proof);
                    bitmap = checkOrientationImage(bitmap, proof);
                    proofView.setImageBitmap(bitmap);
                } else {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo);
                    bitmap = checkOrientationImage(bitmap, photo);
                    photoView.setImageBitmap(bitmap);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                if (actualSelect.equals(PROOF)) {
                    proof = null;
                } else {
                    photo = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (actualSelect.equals(PROOF)) {
                    proof = null;
                } else {
                    photo = null;
                }
            } finally {
                if (proof != null && photo != null) {
                    sendButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendImages(View view) {
        String yearsOfExp = years.getText().toString();
        String institutionName = institution.getText().toString();
        if(yearsOfExp.length() < 1){
            years.setError("Field must not be empty");
            return;
        }
        if(institutionName.length() < 1){
            institution.setError("Field must not be empty");
            return;
        }
        byte[] proofBytes = getData(proof);
        byte[] photoBytes = getData(photo);
        progressBar.setVisibility(View.VISIBLE);
        firebaseStorage.getReference("medic")
                .child(userId)
                .child("medicalProof")
                .putBytes(proofBytes)
                .addOnSuccessListener(command -> {
                    firebaseStorage.getReference("medic")
                            .child(userId)
                            .child("userPhoto")
                            .putBytes(photoBytes)
                            .addOnSuccessListener(command1 -> {
                                firebaseStorage.getReference("medic")
                                        .child(userId)
                                        .child("medicalProof")
                                        .getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            String proofUrl = uri.toString();
                                            firebaseStorage.getReference("medic")
                                                    .child(userId)
                                                    .child("userPhoto")
                                                    .getDownloadUrl()
                                                    .addOnSuccessListener(uri1 -> {
                                                        String photoUrl = uri1.toString();
                                                        user.setMedicProofUrl(proofUrl);
                                                        user.setUserPhotoUrl(photoUrl);
                                                        user.setMedicSignUpPetition(true);
                                                        user.setYearsOfExperience(yearsOfExp);
                                                        user.setInstitution(institutionName);
                                                        firebaseDatabase.getReference("users")
                                                                .child(userId)
                                                                .setValue(user)
                                                                .addOnSuccessListener(command2 -> {
                                                                    Toast.makeText(SignupMedic.this, "Petition Sended correctly!",Toast.LENGTH_LONG).show();
                                                                })
                                                                .addOnFailureListener(command2 -> {
                                                                    Toast.makeText(SignupMedic.this, "Error!",Toast.LENGTH_LONG).show();
                                                                })
                                                                .addOnCompleteListener(command2 -> {
                                                                    progressBar.setVisibility(View.GONE);
                                                                });
                                                    });
                                        });
                            });
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Bitmap checkOrientationImage(Bitmap bitmap, Uri photoPath){
        Bitmap rotatedBitmap = null;
        try{
            InputStream in = getContentResolver().openInputStream(photoPath);
            ExifInterface ei = new ExifInterface(in);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            return rotatedBitmap;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public byte[] getData(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            bitmap = checkOrientationImage(bitmap, uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}