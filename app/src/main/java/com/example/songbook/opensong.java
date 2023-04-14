package com.example.songbook;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.songbook.db.DbConstant;
import com.example.songbook.db.DbHelper;
import com.example.songbook.db.DbManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class opensong extends AppCompatActivity {
    private TextView name, text, singertext, ok;
    private ToggleButton tog;
    private ImageView back, edit, plus, minus, delete, download, picture;
    private String singer;
    private EditText autoscroller;
    private Integer textSize;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private List<Song> listTemp;
    private DatabaseReference mDatabase;
    private String SONG_KEY = "Songs", downloadstr;
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensong);
        init();
        getIntentMain();
        getDataSQL();

        if (listData.contains(name.getText().toString())) {
            download.setVisibility(GONE);
        }

        if (downloadstr.equals("yes")) {
            getDataSQL();
            download.setVisibility(GONE);
            Drawable draw = ResourcesCompat.getDrawable(getResources(), R.drawable.download, null);
            picture.setImageDrawable(draw);
            delete.setOnClickListener(oclDeleteSQL);
        } else {
            getDataFirebase();
            delete.setOnClickListener(oclDeleteFirebase);
        }

        ok.setOnClickListener(oclok);
        back.setOnClickListener(oclback);
        edit.setOnClickListener(ocledit);
        plus.setOnClickListener(oclPlusTextSize);
        minus.setOnClickListener(oclMinTextSize);
        download.setOnClickListener(oclDownload);
        text.setMovementMethod(new ScrollingMovementMethod());
        tog.setOnClickListener(oclFont);


        autoscroller.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ok.setVisibility(View.VISIBLE);
            }
        });
 
    }


    private void init(){
        textSize = 24;
        picture = findViewById(R.id.picture);
        dbHelper = new DbHelper(this);
        ok = findViewById(R.id.ok);
        download = findViewById(R.id.download);
        delete = findViewById(R.id.delete);
        singertext = findViewById(R.id.singer);
        tog = findViewById(R.id.togglebut);
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        text = findViewById(R.id.text);
        name = findViewById(R.id.songname);
        back = findViewById(R.id.back);
        edit = findViewById(R.id.edit);
        autoscroller = findViewById(R.id.autoscrollvalue);
        mDatabase = FirebaseDatabase.getInstance().getReference(SONG_KEY);
        listData = new ArrayList<>();
        listTemp = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        db = dbHelper.getReadableDatabase();
    }

    private void getIntentMain(){
        Intent i = getIntent();
        if (i != null){
            name.setText(i.getStringExtra(Constant.SONG_NAME));
            text.setText(i.getStringExtra(Constant.SONG_TEXT));
            singer = i.getStringExtra(Constant.SONG_SINGER);
            singertext.setText("Исполнитель: "+i.getStringExtra(Constant.SONG_SINGER));
            downloadstr = i.getStringExtra(Constant.DOWNLOAD);
        }
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
        mDatabase.addValueEventListener(vListener);
    }

    private void getDataSQL () {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

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
        if (listTemp.size() > 0) listTemp.clear();
        while(cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(DbConstant.FeedEntry.NAME));
            String itemSinger = cursor.getString(cursor.getColumnIndexOrThrow(DbConstant.FeedEntry.SINGER));
            String itemText = cursor.getString(cursor.getColumnIndexOrThrow(DbConstant.FeedEntry.TEXT));
            Song itemSong = new Song(itemSinger, itemName, itemText);
            listData.add(itemName);
            listTemp.add(itemSong);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    View.OnClickListener oclFont = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (tog.getText().toString() == "Daray"){
                tog.setTextOff("Auto");
                Typeface font = ResourcesCompat.getFont(opensong.this, R.font.daray);
                text.setTypeface(font);
            } else {
                tog.setTextOff("Daray");
                Typeface auto = Typeface.SANS_SERIF;
                text.setTypeface(auto);
            }
        }
    };

    View.OnClickListener oclDownload = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sname = name.getText().toString();
            String ssinger = singer;
            String stext = text.getText().toString();

            ContentValues values = new ContentValues();
            values.put(DbConstant.FeedEntry.NAME, sname);
            values.put(DbConstant.FeedEntry.SINGER, ssinger);
            values.put(DbConstant.FeedEntry.TEXT, stext);

            if (!listData.contains(sname)) {
                db.insert(DbConstant.FeedEntry.TABLE_NAME, null, values);
                download.setVisibility(GONE);
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

    View.OnClickListener oclDeleteFirebase = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getDataFirebase();
            Query songQueryPub = mDatabase.child("public").orderByChild("songname").equalTo(name.getText().toString());
            Query songQueryLocal = mDatabase.child("local").orderByChild("songname").equalTo(name.getText().toString());
            songQueryLocal.addListenerForSingleValueEvent(new ValueEventListener() {
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
            songQueryPub.addListenerForSingleValueEvent(new ValueEventListener() {
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
            Intent intent = new Intent(opensong.this, songs.class);
            intent.putExtra(Constant.SINGER, "Песни");
            intent.putExtra(Constant.DOWNLOAD, "no");
            startActivity(intent);
            db.close();
        }
    };

    View.OnClickListener oclDeleteSQL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            db.delete(DbConstant.TABLE_NAME, DbConstant.FeedEntry.NAME + "=?", new String[]{name.getText().toString()});

            Intent intent = new Intent(opensong.this, singers.class);
            intent.putExtra(Constant.DOWNLOAD, "yes");
            startActivity(intent);
            db.close();
        }
    };

    View.OnClickListener oclok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            
        }
    };

    View.OnClickListener oclback = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(opensong.this, songs.class);
            intent.putExtra(Constant.SINGER, singer);
            intent.putExtra(Constant.DOWNLOAD, downloadstr);
            startActivity(intent);
            db.close();
        }
    };

    View.OnClickListener ocledit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(opensong.this, newsong.class);
            intent.putExtra(Constant.SONG_NAME, name.getText().toString());
            intent.putExtra(Constant.SONG_TEXT, text.getText().toString());
            intent.putExtra(Constant.SONG_SINGER, singer);
            intent.putExtra(Constant.DOWNLOAD, downloadstr);
            startActivity(intent);
            db.close();
        }
    };
}