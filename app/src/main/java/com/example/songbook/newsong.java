package com.example.songbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.songbook.db.DbConstant;
import com.example.songbook.db.DbHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class newsong extends AppCompatActivity {
    private ToggleButton tog, changeFont;
    private EditText name, text, singer;
    private ImageView back, ok, plus, minus, picture;
    private Integer textSize;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private List<Song> listTemp;
    private DatabaseReference mDatabase;
    private String SONG_KEY = "Songs", songaccess = "public", download;
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsong);
        init();
        getIntentMain();

        if (download.equals("yes")) {
            ok.setOnClickListener(oclokSQL);
            Drawable draw = ResourcesCompat.getDrawable(getResources(), R.drawable.download, null);
            picture.setImageDrawable(draw);
        } else {
            getDataFirebase();
            ok.setOnClickListener(oclokFirebase);
        }

        back.setOnClickListener(oclback);
        plus.setOnClickListener(oclPlusTextSize);
        minus.setOnClickListener(oclMinTextSize);
        changeFont.setOnClickListener(oclFont);
    }

    private void init(){
        dbHelper = new DbHelper(this);
        textSize = 24;
        changeFont = findViewById(R.id.chooseFont);
        plus = findViewById(R.id.plusbut);
        minus = findViewById(R.id.minus);
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
        db = dbHelper.getReadableDatabase();
    }

    private void getDataFirebase () {
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
        mDatabase.child(songaccess).addValueEventListener(vListener);
    }

    View.OnClickListener oclFont = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (changeFont.getText().toString() == "Daray"){
                changeFont.setTextOff("Auto");
                Typeface font = ResourcesCompat.getFont(newsong.this, R.font.daray);
                text.setTypeface(font);
            } else {
                changeFont.setTextOff("Daray");
                Typeface auto = Typeface.SANS_SERIF;
                text.setTypeface(auto);
            }
        }
    };
    View.OnClickListener oclPlusTextSize = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            textSize = textSize + 1;
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
    };

    View.OnClickListener oclMinTextSize = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (text.getTextSize()-1 > 0) {
                textSize = textSize - 1;
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            }
        }
    };
    View.OnClickListener oclokFirebase = new View.OnClickListener() {
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
                Song newSong = new Song (songsinger, songname, songtext);
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
                i.putExtra(Constant.DOWNLOAD, "no");
                startActivity(i);
            }
        }
    };

    View.OnClickListener oclokSQL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String songsinger = singer.getText().toString();
            String songname = name.getText().toString();
            String songtext = text.getText().toString();

            db.delete(DbConstant.TABLE_NAME, DbConstant.FeedEntry.NAME + "=?", new String[]{name.getText().toString()});

            ContentValues values = new ContentValues();
            values.put(DbConstant.FeedEntry.NAME, songname);
            values.put(DbConstant.FeedEntry.SINGER, songsinger);
            values.put(DbConstant.FeedEntry.TEXT, songtext);

            db.insert(DbConstant.FeedEntry.TABLE_NAME, null, values);

            Intent i = new Intent(newsong.this, opensong.class);
            i.putExtra(Constant.SONG_NAME, songname);
            i.putExtra(Constant.SONG_TEXT, songtext);
            i.putExtra(Constant.SONG_SINGER, songsinger);
            i.putExtra(Constant.DOWNLOAD, "yes");
            startActivity(i);
            db.close();
        }
    };

    View.OnClickListener oclback = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
            db.close();
        }
    };
    private void getIntentMain(){
        Intent i = getIntent();
        if (i != null){
            name.setText(i.getStringExtra(Constant.SONG_NAME));
            text.setText(i.getStringExtra(Constant.SONG_TEXT));
            singer.setText(i.getStringExtra(Constant.SONG_SINGER));
            download = i.getStringExtra(Constant.DOWNLOAD);
        }
    }
}