package com.example.datingapplication.ui.cancel_reason;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datingapplication.R;

public class CancelReasonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_reason);

        TextView changeMyMind = findViewById(R.id.change_my_mind);
        changeMyMind.setOnClickListener(v -> finish());

        TextView waitTooLong = findViewById(R.id.wait_too_long);
        waitTooLong.setOnClickListener(v -> finish());

        TextView plansChanged = findViewById(R.id.plans_changed);
        plansChanged.setOnClickListener(v -> finish());
    }
}
