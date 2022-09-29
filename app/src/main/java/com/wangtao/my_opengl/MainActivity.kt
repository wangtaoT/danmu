package com.wangtao.my_opengl

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import net.codeages.live.danmaku.DanmakuView
import net.codeages.live.danmaku.parser.TextParser

class MainActivity : AppCompatActivity() {
    private var danmakuView: DanmakuView? = null
    private var et: EditText? = null
    private var btn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        danmakuView = findViewById(R.id.danmaku_view)
        et = findViewById(R.id.et)
        btn = findViewById(R.id.btn)
        danmakuView?.start()
        btn?.setOnClickListener {
            danmakuView?.shootDanmaku(
                et?.text.toString(), TextParser()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        danmakuView!!.release()
    }
}