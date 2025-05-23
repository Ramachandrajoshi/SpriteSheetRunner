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

class SpriteView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    var image: Bitmap? = null
    var spriteWidth = 0.0
        private set
    var spriteHeight = 0.0
        private set

    var columns = 1 // Default to 1, will be set properly in init or setters
        set(value) {
            field = if (value > 0) value else 1 // Ensure at least 1
            // Update spriteWidth when columns change, if image is available
            image?.let {
                spriteWidth = it.width.toDouble() / field
            }
            lastFrame = field * rows
        }

    var rows = 1 // Default to 1
        set(value) {
            field = if (value > 0) value else 1 // Ensure at least 1
            // Update spriteHeight when rows change, if image is available
            image?.let {
                spriteHeight = it.height.toDouble() / field
            }
            lastFrame = columns * field
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
        private set
    var renderColumn = 0
        private set
    var currentFrame = 0
        private set
        
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
                // Setters for columns/rows already handle this if image is set before them.
                // If image is set after, these need to be called.
                // For safety, recalculate here after all are known.
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
        
        // Sprite dimensions are now updated via setters or init block
        // spriteWidth = currentImage.width.toDouble() / columns
        // spriteHeight = currentImage.height.toDouble() / rows

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
                currentFrame++ // currentFrame is now private set, update it here
                
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

        if (!running && currentFrame == 0) { 
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
            return 
        }
        
        if(spriteWidth <=0 || spriteHeight <=0) return

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
        
        if (running) {
            if (renderColumn == columns - 1 && !isFixedRow) {
                renderRow = ++renderRow % rows
            }
            renderColumn = ++renderColumn % columns

            post { 
                stateChangeListener?.onUpdateFrame(this)
            }
        }
    }
}