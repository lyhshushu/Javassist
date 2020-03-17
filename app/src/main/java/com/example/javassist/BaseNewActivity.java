package com.example.javassist;



import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class BaseNewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        System.out.println("yiyiyiyiyi");
        super.onCreate(savedInstanceState);
    }
}
