package com.myprojects.firebasepdfupload;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myprojects.Upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    final static int PICK_PDF_CODE = 2342;


    EditText editTextFilename;
    ProgressBar progressBar;
    Button btn_upload, btn_choose;
    Uri uri;
    String userId,filename;

    ListView listView;
    List<Upload> uploadList;

    private FirebaseAuth fAuth;
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        editTextFilename = findViewById(R.id.editTextFileName);
        progressBar = findViewById(R.id.progressbar);
        btn_upload = findViewById(R.id.buttonUploadFile);
        btn_choose = findViewById(R.id.buttonchoosePdf);
        uploadList = new ArrayList<>();
        listView = findViewById(R.id.listView);

        getUploadedList();

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Upload upload = uploadList.get(i);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(upload.getUrl()));
            startActivity(intent);
        });

        btn_choose.setOnClickListener(v -> getPDF());
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filename = editTextFilename.getText().toString();
                if (filename.isEmpty()){
                    Toast.makeText(HomeActivity.this, "Please enter file name...", Toast.LENGTH_SHORT).show();
                }else{
                    uploadFile(uri);
                }
            }
        });

    }

    private void getUploadedList() {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploadList.add(upload);
                }

                String[] uploads = new String[uploadList.size()];

                for (int i = 0; i < uploads.length; i++) {
                    uploads[i] = uploadList.get(i).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, uploads);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                uri = data.getData();
                btn_choose.setVisibility(View.GONE);
                btn_upload.setVisibility(View.VISIBLE);
                Toast.makeText(this, "File Selected, Enter File name...", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile(Uri data) {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_UPLOADS + editTextFilename.getText() + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(taskSnapshot -> {

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();

                    sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Upload upload = new Upload(mDatabaseReference.push().getKey(),filename, uri.toString());
                            mDatabaseReference.child(Objects.requireNonNull(mDatabaseReference.push().getKey())).setValue(upload);
                        }
                    });
                })
                .addOnFailureListener(exception ->
                                Log.e("","uploadIssue" + exception.getMessage())

                )
                .addOnProgressListener(taskSnapshot -> Toast.makeText(HomeActivity.this, "Uploading...", Toast.LENGTH_SHORT).show());

    }

}