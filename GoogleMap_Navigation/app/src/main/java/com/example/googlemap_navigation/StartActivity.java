package com.example.googlemap_navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        //버튼 객체 설정
        Button findPath_btn = (Button)findViewById(R.id.findPath_btn);
        Button findCenter_btn = (Button)findViewById(R.id.findCenter_btn);
        Button calTaxiFee_btn = (Button)findViewById(R.id.calTaxiFee_btn);


        //길찾기 버튼 클릭 이벤트 설정
        findPath_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //중심지 찾기 버튼 클릭 이벤트 설정
        findCenter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, FindCenterActivity.class);
                startActivity(intent);
            }
        });

        //택시요금 계산 버튼 클릭 이벤트 설정
        calTaxiFee_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}
