package com.example.songbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class singers extends AppCompatActivity {
    private EditText finder;
    private ImageView back, menu, addsong;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private List<Song> listTemp;
    private String SONG_KEY = "Songs";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singers);
        init();
        getData();
        setOnClickItem();

        addsong.setOnClickListener(oclnewsong);
        back.setOnClickListener(oclback);
        menu.setOnClickListener(oclmenu);

        finder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init(){
        finder = findViewById(R.id.findsinger);
        back = findViewById(R.id.back);
        menu = findViewById(R.id.menu);
        addsong = findViewById(R.id.plus);
        listView = findViewById(R.id.scroller);
        listData = new ArrayList<>();
        listTemp = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);
        mDatabase = FirebaseDatabase.getInstance().getReference(SONG_KEY);
    }

    private void getData () {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listData.size() > 0) listData.clear();
                if (listTemp.size() > 0) listTemp.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Song song = ds.getValue(Song.class);
                    assert song != null;
                    if (!listData.contains(song.singer)){
                        listData.add(song.singer);
                        listTemp.add(song);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabase.child("public").addValueEventListener(vListener);
    }

    private void setOnClickItem() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Song songItem = listTemp.get(i);
                Intent intent = new Intent(singers.this, songs.class);
                intent.putExtra(Constant.SINGER, songItem.singer);
                startActivity(intent);
            }
        });
    }

    View.OnClickListener oclback = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };
    View.OnClickListener oclmenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(singers.this, MainActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener oclnewsong = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(singers.this, newsong.class);
            intent.putExtra(Constant.SONG_NAME, "");
            intent.putExtra(Constant.SONG_TEXT, "");
            intent.putExtra(Constant.SONG_SINGER, "");
            startActivity(intent);
        }
    };

}