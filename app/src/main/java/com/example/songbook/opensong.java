package com.example.songbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class opensong extends AppCompatActivity {
    private TextView name, text;
    private ImageView back, edit;
    private String SONG_KEY = "Songs", singer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensong);
        init();
        getIntentMain();

        back.setOnClickListener(oclback);
        edit.setOnClickListener(ocledit);
    }


    private void init(){
        text = findViewById(R.id.text);
        name = findViewById(R.id.songname);
        back = findViewById(R.id.back);
        edit = findViewById(R.id.edit);

    }

    private void getIntentMain(){
        Intent i = getIntent();
        if (i != null){
            name.setText(i.getStringExtra(Constant.SONG_NAME));
            text.setText(i.getStringExtra(Constant.SONG_TEXT));
            singer = i.getStringExtra(Constant.SONG_SINGER);
        }
    }

    View.OnClickListener oclback = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(opensong.this, songs.class);
            intent.putExtra(Constant.SINGER, singer);
            startActivity(intent);
        }
    };

    View.OnClickListener ocledit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(opensong.this, newsong.class);
            intent.putExtra(Constant.SONG_NAME, name.getText().toString());
            intent.putExtra(Constant.SONG_TEXT, text.getText().toString());
            intent.putExtra(Constant.SONG_SINGER, singer);
            startActivity(intent);
        }
    };
}