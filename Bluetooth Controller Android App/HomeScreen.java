package com.example.bluetoothshenanegans2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HomeScreen extends AppCompatActivity {
    private Button connect2;
    private EditText editText;
    public static final String EXTRA_TEXT = "com.example.bluetoothshenanegans2.EXTRA_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        connect2 = findViewById(R.id.connect_to_bluetooth);
        connect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
        editText = findViewById(R.id.editText);
    }

    public void openMainActivity(){
        String myString = new String(editText.getText().toString());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_TEXT,myString);
        startActivity(intent);
    }

}