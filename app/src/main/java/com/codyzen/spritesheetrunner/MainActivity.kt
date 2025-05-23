package com.codyzen.spritesheetrunner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
// Removed duplicate: import androidx.appcompat.app.AppCompatActivity
// Removed: import android.view.View
// Removed: import com.codyzen.spriterunner.SpriteView
// Removed: import com.codyzen.spriterunner.StateChangeListener
// Removed: import kotlinx.android.synthetic.main.activity_main.*
import com.codyzen.spritesheetrunner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
