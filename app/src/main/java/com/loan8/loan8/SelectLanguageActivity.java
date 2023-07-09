package com.loan8.loan8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectLanguageActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnEnglishId, btnMalaysiaId, btnChinaId, btnOtherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        init();
    }

    private void init() {
        btnEnglishId = findViewById(R.id.btnEnglishId);
        btnEnglishId.setOnClickListener(this);
        btnMalaysiaId = findViewById(R.id.btnMalaysiaId);
        btnMalaysiaId.setOnClickListener(this);
        btnChinaId = findViewById(R.id.btnChinaId);
        btnChinaId.setOnClickListener(this);
        btnOtherId = findViewById(R.id.btnOtherId);
        btnOtherId.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnEnglishId:
                startActivity(new Intent(SelectLanguageActivity.this, LogInActivity.class));
                break;
            case R.id.btnMalaysiaId:
                startActivity(new Intent(SelectLanguageActivity.this, LogInActivity.class));
                break;
            case R.id.btnChinaId:
                startActivity(new Intent(SelectLanguageActivity.this, LogInActivity.class));
                break;
            case R.id.btnOtherId:
                startActivity(new Intent(SelectLanguageActivity.this, LogInActivity.class));
                break;
        }
    }
}