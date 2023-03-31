package com.example.songbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class newsong extends AppCompatActivity {
    private ToggleButton tog;
    private EditText name, text, singer;
    private ImageView back, ok;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private List<Song> listTemp;
    private DatabaseReference mDatabase;
    private String SONG_KEY = "Songs", songaccess = "public";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsong);
        init();
        getIntentMain();
        getData();

        ok.setOnClickListener(oclok);
        back.setOnClickListener(oclback);
    }

    private void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference(SONG_KEY);
        singer = findViewById(R.id.singerinput);
        tog = findViewById(R.id.togglebutton);
        back = findViewById(R.id.back);
        ok = findViewById(R.id.ok);
        name = findViewById(R.id.songname);
        text = findViewById(R.id.text);
        listData = new ArrayList<>();
        listTemp = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
    }

    private void getData () {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listData.size() > 0) listData.clear();
                if (listTemp.size() > 0) listTemp.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Song song = ds.getValue(Song.class);
                    assert song != null;
                    listData.add(song.songname);
                    listTemp.add(song);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabase.addValueEventListener(vListener);
    }

    View.OnClickListener oclok = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (tog.getText().toString().equals("Пользовательская")){
                songaccess = "local";
            } else {
                songaccess = "public";
            }
            String songsinger = singer.getText().toString();
            String songname = name.getText().toString();
            String songtext = text.getText().toString();

            if (!TextUtils.isEmpty(songname) && !TextUtils.isEmpty(songsinger) && !TextUtils.isEmpty(songtext)) {
                Song newSong = new Song (songaccess ,songsinger, songname, songtext);

                if (listData.contains(songname)) {
                    Query songQuery = mDatabase.child(songaccess).orderByChild("songname").equalTo(songname);
                    songQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot songSnapshot: snapshot.getChildren()) {
                                songSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }

                mDatabase.child(songaccess).push().setValue(newSong);
                Intent i = new Intent(newsong.this, opensong.class);
                i.putExtra(Constant.SONG_NAME, songname);
                i.putExtra(Constant.SONG_TEXT, songtext);
                i.putExtra(Constant.SONG_SINGER, songsinger);
                startActivity(i);
            }
        }
    };

    View.OnClickListener oclback = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };
    private void getIntentMain(){
        Intent i = getIntent();
        if (i != null){
            name.setText(i.getStringExtra(Constant.SONG_NAME));
            text.setText(i.getStringExtra(Constant.SONG_TEXT));
            singer.setText(i.getStringExtra(Constant.SONG_SINGER));
        }
    }
}