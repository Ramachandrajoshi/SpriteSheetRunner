package com.codyzen.spriterunner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
// Removed: import java.util.*
// Removed: import kotlin.concurrent.schedule


class SpriteView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    var image: Bitmap? = null
    var spriteWidth = 0.0
    var spriteHeight = 0.0

    var columns = 1 // Default to 1, will be set properly in init or setters
        set(value) {
            field = if (value > 0) value else 1 // Ensure at least 1
            spriteWidth = image?.width?.toDouble()?.div(field) ?: 0.0
            lastFrame = field * rows
            // Consider if fps should be automatically updated here or only explicitly
        }

    var rows = 1 // Default to 1
        set(value) {
            field = if (value > 0) value else 1 // Ensure at least 1
            spriteHeight = image?.height?.toDouble()?.div(field) ?: 0.0
            lastFrame = columns * field
            // Consider if fps should be automatically updated here or only explicitly
        }

    var fps = 1 // Default to 1, ensure it's positive
        set(value) {
            field = if (value > 0) value else 1 // Ensure fps is positive
        }

    var lastFrame = columns * rows // Initial calculation
    var isFixedRow = false
    var autoPlay = true
    val isRunning: Boolean
        get() {
            return running
        }
    var stateChangeListener: StateChangeListener? = null

    var renderRow = 0
    var renderColumn = 0
    var currentFrame = 0
    private var running = false
    private val srcFrame = Rect()
    private val dstFrame = Rect()

    // Handler for animation
    private val animationHandler = Handler(Looper.getMainLooper())
    private var animationRunnable: Runnable? = null

    init {
        // Set initial default values that depend on each other
        columns = 4
        rows = 4
        fps = columns * rows // Initial fps based on default columns/rows
        lastFrame = columns * rows

        if (attrs != null) {
            val a = context.obtainStyledAttributes(
                attrs, R.styleable.SpriteView, 0, 0
            )
            image = a.getDrawable(R.styleable.SpriteView_src)?.toBitmap()
            
            // Columns and Rows must be set before calculating spriteWidth/Height and lastFrame from XML
            columns = a.getInt(R.styleable.SpriteView_columns, columns) // Uses setter
            rows = a.getInt(R.styleable.SpriteView_rows, rows) // Uses setter
            
            // Recalculate dependent properties if image is available
            image?.let {
                spriteWidth = it.width.toDouble() / columns
                spriteHeight = it.height.toDouble() / rows
            }

            // FPS and lastFrame from XML, using current values as defaults for getInt
            fps = a.getInt(R.styleable.SpriteView_fps, fps) // Uses setter
            lastFrame = a.getInt(R.styleable.SpriteView_lastFrame, lastFrame)


            renderRow = a.getInt(R.styleable.SpriteView_renderRow, renderRow)
            isFixedRow = a.getBoolean(R.styleable.SpriteView_isFixedRow, isFixedRow)
            autoPlay = a.getBoolean(R.styleable.SpriteView_autoPlay, autoPlay)
            
            a.recycle()
            if (autoPlay)
                start()
        }
    }

    constructor(context: Context) : this(context, null)

    fun start() {
        stop() // Resets animationRunnable and running state

        val currentImage = image ?: return // Null safety for image
        
        // Ensure columns and rows are positive before calculating sprite dimensions
        if (columns <= 0 || rows <= 0) return

        spriteWidth = currentImage.width.toDouble() / columns
        spriteHeight = currentImage.height.toDouble() / rows

        val frameDelay = (1000 / fps).toLong()
        if (frameDelay <= 0) { // Safety check for delay
            return
        }

        animationRunnable = object : Runnable {
            override fun run() {
                // Core animation logic:
                invalidate() // Request a redraw
                if (lastFrame == currentFrame) {
                    resetFrames()
                }
                currentFrame++
                
                if (running) { // Check if animation should still be running before posting next
                    animationHandler.postDelayed(this, frameDelay)
                }
            }
        }
        running = true
        animationHandler.postDelayed(animationRunnable!!, 0) // Start immediately

        post {
            stateChangeListener?.onStart(this)
        }
    }

    fun stop() {
        pause() // Removes callbacks and sets running to false
        if (currentFrame > 0) // Only trigger onStop if it actually ran
            post {
                stateChangeListener?.onStop(this)
            }

        resetFrames()
        invalidate() // Redraw in reset state
        // running = false; // This is now handled in pause()
    }

    fun pause() {
        running = false // Signal the runnable to stop posting new frames
        animationRunnable?.let { animationHandler.removeCallbacks(it) }
        animationRunnable = null // Release the runnable
    }

    fun resetFrames() {
        renderRow = if (isFixedRow) renderRow else 0
        renderColumn = 0
        currentFrame = 0
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas) // Important to call super
        val currentImage = image ?: return // Null safety for image

        if (!running && currentFrame == 0) { // If stopped and reset, draw initial frame or clear
            // Optionally draw the very first frame (renderColumn=0, renderRow=0 if !isFixedRow)
            // or leave blank. Current logic will draw the current (reset) frame.
             val srcX = renderColumn * spriteWidth
             val srcY = renderRow * spriteHeight
             srcFrame.left = srcX.toInt()
             srcFrame.top = srcY.toInt()
             srcFrame.right = (srcX + spriteWidth).toInt()
             srcFrame.bottom = (srcY + spriteHeight).toInt()

             dstFrame.left = 0
             dstFrame.top = 0
             dstFrame.right = width
             dstFrame.bottom = height
             canvas.drawBitmap(currentImage, srcFrame, dstFrame, null)
            return // Don't advance frames if not running
        }
        
        if(spriteWidth <=0 || spriteHeight <=0) return // Avoid division by zero if image dimensions are zero

        val srcX = renderColumn * spriteWidth
        val srcY = renderRow * spriteHeight
        srcFrame.left = srcX.toInt()
        srcFrame.top = srcY.toInt()
        srcFrame.right = (srcX + spriteWidth).toInt()
        srcFrame.bottom = (srcY + spriteHeight).toInt()

        dstFrame.left = 0
        dstFrame.top = 0
        dstFrame.right = width
        dstFrame.bottom = height

        canvas.drawBitmap(currentImage, srcFrame, dstFrame, null)
        
        // Frame update logic should ideally be tied to the animationRunnable,
        // but for View invalidation, onDraw is where drawing happens.
        // We only update renderColumn/Row if running.
        if (running) {
            if (renderColumn == columns - 1 && !isFixedRow) {
                renderRow = ++renderRow % rows
            }
            renderColumn = ++renderColumn % columns

            post { // UI thread operation
                stateChangeListener?.onUpdateFrame(this)
            }
        }
    }
}