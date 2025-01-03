package com.example.schroll;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class courseViewForStudentsActivity extends AppCompatActivity {

    FirebaseStorage fStorage;
    StorageReference fStorageRef;
    FirebaseAuth fAuth;
    PDFView lessonPDFView;
    String title, userID, courseName, classCode;
    int lesson_index;
    Handler mHandler, handler;
    EditText gradeHW;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view_for_students);
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        gradeHW = findViewById(R.id.hwGrade);
        textView = findViewById(R.id.textView17);
        lessonPDFView = (PDFView) findViewById(R.id.pdfView2);

        lesson_index = getIntent().getIntExtra("pos", 0);
        mHandler = new Handler();
        handler = new Handler();
        title = coursesViewForStudentsActivity.documentsArrayList.get(lesson_index).getData();

        Intent intent = getIntent();
        courseName = intent.getStringExtra(coursesViewForStudentsActivity.EXTRA_COURSE2);
        classCode = intent.getStringExtra(coursesViewForStudentsActivity.EXTRA_CLASSCODE2);

        fStorage = FirebaseStorage.getInstance();
        fStorageRef = fStorage.getReference().child("Course Uploads/" + classCode + "/" + courseName + "/");
        final long ONE_MEGABYTE = 1024 * 1024;

        fStorageRef.child(coursesViewForStudentsActivity.documentsArrayList
                .get(lesson_index).getData()).getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                lessonPDFView.fromBytes(bytes).load();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "download unsuccessful", Toast.LENGTH_LONG).show();
            }
        });
    }

}