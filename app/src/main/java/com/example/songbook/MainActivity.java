package com.example.songbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_CODE = 1;
    private RelativeLayout main, singers, songs, mysongs, newsong;
    private TextView logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);

        singers.setOnClickListener(oclsingers);
        songs.setOnClickListener(oclsongs);
        mysongs.setOnClickListener(oclmysongs);
        newsong.setOnClickListener(oclnewsong);
        logout.setOnClickListener(oclsingout);

    }

    private void init(){
        singers = findViewById(R.id.singers);
        songs = findViewById(R.id.findsong);
        mysongs = findViewById(R.id.downloadsong);
        newsong = findViewById(R.id.addsong);
        logout = findViewById(R.id.logout);
    }
    View.OnClickListener oclsingers = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, singers.class);
            intent.putExtra(Constant.DOWNLOAD, "no");
            startActivity(intent);
        }
    };
    View.OnClickListener oclsongs = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, songs.class);
            intent.putExtra(Constant.SINGER, "Песни");
            intent.putExtra(Constant.DOWNLOAD, "no");
            startActivity(intent);
        }
    };
    View.OnClickListener oclmysongs = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, singers.class);
            intent.putExtra(Constant.DOWNLOAD, "yes");
            startActivity(intent);
        }
    };
    View.OnClickListener oclnewsong = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, newsong.class);
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
        }
    };

    View.OnClickListener oclsingout = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
    };
}