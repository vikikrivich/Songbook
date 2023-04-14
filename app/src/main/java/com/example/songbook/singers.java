package com.example.songbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.songbook.db.DbConstant;
import com.example.songbook.db.DbHelper;
import com.example.songbook.db.DbManager;
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
    private ImageView menu, addsong, picture;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private List<Song> listTemp;
    private String SONG_KEY = "Songs", download;
    private DatabaseReference mDatabase;
    private DbManager dbManager;
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singers);
        init();
        getIntentMain();
        dbManager.openDb();

        if (download.equals("no")) {
            getDataFirebase();
            setOnClickItemFirebase();
        }else{
            getDataSQL();
            setOnClickItemSQL();
            Drawable draw = ResourcesCompat.getDrawable(getResources(), R.drawable.download, null);
            picture.setImageDrawable(draw);
        }

        addsong.setOnClickListener(oclnewsong);
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
        picture = findViewById(R.id.picture);
        dbHelper = new DbHelper(this);
        dbManager = new DbManager(this);
        finder = findViewById(R.id.findsinger);
        menu = findViewById(R.id.menu);
        addsong = findViewById(R.id.plus);
        listView = findViewById(R.id.scroller);
        listData = new ArrayList<>();
        listTemp = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);
        mDatabase = FirebaseDatabase.getInstance().getReference(SONG_KEY);
        db = dbHelper.getReadableDatabase();
    }
    private void getIntentMain(){
        Intent i = getIntent();
        if (i != null){
            download = i.getStringExtra(Constant.DOWNLOAD);
        }
    }
    private void getDataFirebase () {
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

        ValueEventListener lListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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
        mDatabase.child("local").addValueEventListener(lListener);

    }

    private void setOnClickItemFirebase() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Song songItem = listTemp.get(i);
                Intent intent = new Intent(singers.this, songs.class);
                intent.putExtra(Constant.SINGER, songItem.singer);
                intent.putExtra(Constant.DOWNLOAD, "no");
                startActivity(intent);
                db.close();
            }
        });
    }
    private void getDataSQL () {
        Cursor cursor = db.query(
                DbConstant.FeedEntry.TABLE_NAME,
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        if (listData.size() > 0) listData.clear();
        while(cursor.moveToNext()) {
            String itemSinger = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbConstant.FeedEntry.SINGER));
            listData.add(itemSinger);
        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }
    private void setOnClickItemSQL() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(singers.this, songs.class);
                intent.putExtra(Constant.SINGER, listData.get(i));
                intent.putExtra(Constant.DOWNLOAD, "yes");
                startActivity(intent);
                db.close();
            }
        });
    }
    View.OnClickListener oclmenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(singers.this, MainActivity.class);
            startActivity(intent);
            db.close();
        }
    };

    View.OnClickListener oclnewsong = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(singers.this, newsong.class);
            intent.putExtra(Constant.SONG_NAME, "");
            intent.putExtra(Constant.SONG_TEXT, "");
            intent.putExtra(Constant.SONG_SINGER, "");
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
                intent.putExtra(Constant.DOWNLOAD, "no");
            } else {
                intent.putExtra(Constant.DOWNLOAD, "yes");
            }
            startActivity(intent);
            db.close();
        }
    };

}