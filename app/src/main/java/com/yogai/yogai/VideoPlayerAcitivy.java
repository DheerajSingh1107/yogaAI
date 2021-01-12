package com.yogai.yogai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class VideoPlayerAcitivy extends AppCompatActivity {

    MediaController mediaController;
    ProgressBar progressBar;
    Member member;
    UploadTask uploadTask;
    private Uri videoUri;
    private static final int PICK_VIDEO=1;
    VideoView videoView;
    TextView chooseVideo ,showVideo;
    Button uploadButton;
    EditText editText;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_acitivy);
        member= new Member();
        storageReference = FirebaseStorage.getInstance().getReference("Video");
        databaseReference = FirebaseDatabase.getInstance().getReference("video");


        videoView = findViewById(R.id.videoView_Upload);
        uploadButton = findViewById(R.id.upload_button);
        progressBar = findViewById(R.id.progress_bar);
        editText = findViewById(R.id.video_input_name);
        chooseVideo = findViewById(R.id.text_choseVideo);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();
        chooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent,PICK_VIDEO);
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadVideo();
            }
        });
    }

    private void uploadVideo() {
        String videoName  = editText.getText().toString();
        String searchName =videoName.toLowerCase();
        if (videoUri!=null && !TextUtils.isEmpty(videoName))
        {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference("Video")
                    .child(System.currentTimeMillis()+"."+getExt(videoUri));
            uploadTask = storageReference.putFile(videoUri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                   if (!task.isSuccessful())
                   {
                       throw task.getException();
                   }
                   return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(VideoPlayerAcitivy.this,"Data Saved",Toast.LENGTH_SHORT).show();
                        member.setName(videoName);
                        member.setVideoUrl(downloadUri.toString());
                        member.setSearch(searchName);
                        String i = databaseReference.push().getKey();
                        databaseReference.child(i).setValue(member);


                    }
                    else
                    {
                        Toast.makeText(VideoPlayerAcitivy.this,"Failid",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else
        {
            Toast.makeText(VideoPlayerAcitivy.this,"Please enter Name",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO && resultCode==RESULT_OK &&
              data.getData()!=null)
        {
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
        }

    }

    private  String getExt(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));


    }
}