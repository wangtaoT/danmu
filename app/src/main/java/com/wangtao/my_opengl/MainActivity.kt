package com.wangtao.my_opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.codeages.live.danmaku.DanmakuView;
import net.codeages.live.danmaku.parser.TextParser;

public class MainActivity extends AppCompatActivity {

    private DanmakuView danmakuView;
    private EditText    et;
    private Button      btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        danmakuView = findViewById(R.id.danmaku_view);
        et = findViewById(R.id.et);
        btn = findViewById(R.id.btn);

        danmakuView.start();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                danmakuView.shootDanmaku(et.getText().toString(), new TextParser());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        danmakuView.release();
    }
}
