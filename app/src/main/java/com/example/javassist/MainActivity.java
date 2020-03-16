package com.example.javassist;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Cat mCat;
    Button catSay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        catSay = findViewById(R.id.say);
        Log.e("--->", "===================");
        new AntilazyLoad();
        Log.e("--->", "===================");
        mCat = new Cat();
        catSay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, mCat.say(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
