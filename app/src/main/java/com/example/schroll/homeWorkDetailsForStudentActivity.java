package com.example.schroll;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class homeWorkDetailsForStudentActivity extends AppCompatActivity {

    ImageView fileIcon;
    TextView courseViewText,homeWorkGrade, deadLine, homeWorkDescription;
    Button button;
    StorageReference storageReference;
    String userID, Surname, C, Year, course_number_but_on_string, pdfLocationName2, pdfLocationName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DocumentReference documentReference,courseRef,courseRef2 , reference, homeworkRef, studentRef;
    int courseNumber, Y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work_details_for_student);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        documentReference = fStore.collection("Pdf Uploads").document(userID);
        reference = fStore.collection("Grades").document(userID);

        courseViewText = findViewById(R.id.courseNameView);
        fileIcon = findViewById(R.id.imageView4);
        button = findViewById(R.id.uploadPDF);
        homeWorkGrade = findViewById(R.id.noteView);
        deadLine = findViewById(R.id.delaiView);
        homeWorkDescription = findViewById(R.id.descriptionView);

        button.setEnabled(false);
        fileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });


        Intent intent = getIntent();
        courseNumber = intent.getIntExtra(String.valueOf(homeWorksForStudentActivity.EXTRA_NUMBER2), -1);
        Year = intent.getStringExtra(homeWorksForStudentActivity.EXTRA_YEAR);

        if (Year == null) {
            Intent intent1 = getIntent();
            Y = intent1.getIntExtra(String.valueOf(courseDetailsActivity.EXTRA_NUMBER), -1);
            courseNumber = Y;
            Year = intent1.getStringExtra(courseDetailsActivity.EXTRA_YEARS);
        }


        course_number_but_on_string = String.valueOf(courseNumber);

        courseRef = fStore.collection("Year" + Year + " Courses").document("Matiere 0" + course_number_but_on_string);
        courseRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                pdfLocationName = documentSnapshot.getString("Name");
                courseViewText.setText(pdfLocationName);
                pdfLocationName = pdfLocationName.substring(0, pdfLocationName.length() - 4);
                setHomeWorkGrade(pdfLocationName);
            }
        });

        getClassStudent();
        getStudentSurname();
    }

    private void getClassStudent() {
        studentRef = fStore.collection("Students").document(userID);
        studentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

             C = documentSnapshot.getString("Classroom");
                sethomeWorkDetails( C );
            }
        });
    }

    private void sethomeWorkDetails(String C) {
        homeworkRef = fStore.collection("Home Works").document("Class " +Year +"_" +C);

        homeworkRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable  DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null){
                deadLine.setText(documentSnapshot.getString("HW 0" +courseNumber +" END"));
                homeWorkDescription.setText(documentSnapshot.getString("HW 0" +courseNumber));
            }}
        });
    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"PDF FILE SELECT"), 12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null){
            button.setEnabled(true);
            button.setBackgroundColor(getResources().getColor(R.color.green_1));
            fileIcon.setImageResource(R.drawable.file_selected_icon_1);
            Toast.makeText(this, " PDF file selected", Toast.LENGTH_SHORT).show();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPDF(data.getData());
                }
            });
        }
    }

    private void uploadPDF(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("File is loading...");
        progressDialog.show();

        courseRef2 =fStore.collection("Year" + Year + " Courses").document("Matiere 0" + course_number_but_on_string);
        courseRef2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    pdfLocationName2 = documentSnapshot.getString("Code name");
                    StorageReference pdfRef = storageReference.child("Pdf Uploads/" +"Class " +Year +"_" +C +"/" +pdfLocationName2 +"/" +Surname +".pdf");
                    pdfRef.putFile(data)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isComplete());
                                    Toast.makeText(homeWorkDetailsForStudentActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                            progressDialog.setMessage("File Uploading... " +(int) progress +"%");
                        }
                    });
                }
            }
        });
        }

    public void setHomeWorkGrade(String pdfLocationName) {

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            homeWorkGrade.setText(documentSnapshot.getString("Grade " +pdfLocationName));
            }
        });
    }

    public void getStudentSurname(){
        DocumentReference documentReference = fStore.collection("Students").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    Surname = documentSnapshot.getString("Surname");
                }
            }
        });
    }
}