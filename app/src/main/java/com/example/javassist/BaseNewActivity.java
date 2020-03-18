package com.example.javassist;



import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

@SuppressLint("Registered")
public class BaseNewActivity extends InsteadActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        System.out.println("yiyiyiyiyi");
        super.onCreate(savedInstanceState);
    }
}
