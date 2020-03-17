package com.example.javassist;


//import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;

import java.security.PublicKey;

/**
 * @author 4399lyh
 */
public class MainActivity extends BaseActivity{

    private Cat mCat;
    Button catSay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        catSay = findViewById(R.id.say);
        mCat = new Cat();
        catSay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, mCat.say(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
