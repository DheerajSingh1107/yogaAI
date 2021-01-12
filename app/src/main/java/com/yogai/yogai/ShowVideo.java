package com.yogai.yogai;

import androidx.annotation.NonNull;
import  androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ShowVideo extends AppCompatActivity {
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);


            recyclerView = findViewById(R.id.recyclerView_showVideo);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;
            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference("test");


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Member> options =
                new FirebaseRecyclerOptions.Builder<Member>()
                .setQuery(databaseReference,Member.class)
                .build();
        FirebaseRecyclerAdapter<Member,VideoHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Member, VideoHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull VideoHolder videoHolder, int i, @NonNull Member member) {

                        videoHolder.setExoPlayer(getApplication(),member.getName(),member.getVideoUrl());

                    }

                    @NonNull
                    @Override
                    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item,parent,false);
                        return new VideoHolder(view);

                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}