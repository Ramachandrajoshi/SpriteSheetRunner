package com.codyzen.spritesheetrunner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.BitmapFactory // Added for BitmapFactory
import com.codyzen.spriterunner.BitMapUtils
import com.codyzen.spritesheetrunner.databinding.ActivityMainBinding
import com.codyzen.spriterunner.StateChangeListener // Added for StateChangeListener
// Removed: import com.codyzen.spriterunner.SpriteView // SpriteView is accessed via binding

class MainActivity : AppCompatActivity(), StateChangeListener { // Implement StateChangeListener

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spriteViewDemo.stateChangeListener = this // Set listener

        // Attempt to load a sample bitmap
        try {
//            // Assuming R.drawable.sample_sprite_sheet exists.
//            // If not, this will throw a Resources$NotFoundException.
//            // The R class for the app module is com.codyzen.spritesheetrunner.R
            val bitmap = BitMapUtils.decodeSampledBitmapFromResource(resources, R.drawable.tile, 600, 600)
            binding.spriteViewDemo.image = bitmap
            binding.spriteViewDemo.rows = 8
            binding.spriteViewDemo.columns = 8
            binding.textviewStatus.text = "Status: Image loaded. Ready."
        } catch (e: Exception) {
            // Log the exception or handle it more gracefully if needed
            e.printStackTrace()
            binding.textviewStatus.text = "Status: Error loading sample_sprite_sheet. Add it to res/drawable."
            // You could set a placeholder color or small default bitmap here if desired
            // binding.spriteViewDemo.setBackgroundColor(android.graphics.Color.LTGRAY)
        }

        binding.buttonStart.setOnClickListener {
            binding.spriteViewDemo.start()
        }
        binding.buttonStop.setOnClickListener {
            binding.spriteViewDemo.stop()
        }
        binding.buttonPause.setOnClickListener {
            binding.spriteViewDemo.pause()
        }
    }

    override fun onUpdateFrame(view: com.codyzen.spriterunner.SpriteView) {
        // Ensure UI updates are on the main thread, though setOnClickListener callbacks are.
        // For StateChangeListener methods called from SpriteView's animation thread,
        // it's good practice if SpriteView ensures they are called on the UI thread via post {}.
        // Assuming SpriteView's post {} usage for stateChangeListener is correct.
        binding.textviewStatus.text = "Status: Frame ${view.currentFrame}"
    }

    override fun onStart(view: com.codyzen.spriterunner.SpriteView) {
        binding.textviewStatus.text = "Status: Started"
    }

    override fun onStop(view: com.codyzen.spriterunner.SpriteView) {
        binding.textviewStatus.text = "Status: Stopped"
    }
}
