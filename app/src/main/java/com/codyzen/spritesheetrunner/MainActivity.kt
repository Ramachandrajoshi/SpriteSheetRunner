package com.codyzen.spritesheetrunner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.codyzen.spriterunner.SpriteView
import com.codyzen.spriterunner.StateChangeListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
