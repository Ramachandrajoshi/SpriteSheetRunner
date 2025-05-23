package com.codyzen.spriterunner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import java.util.*
import kotlin.concurrent.schedule


class SpriteView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    var image: Bitmap? = null
    var spriteWidth = 0.0
    var spriteHeight = 0.0
    var columns = 4
    set(value) {
        lastFrame = value * rows
        field = value
    }
    var rows = 4
        set(value) {
            lastFrame = columns * value
            field = value
        }

    var fps = columns * rows
    var lastFrame = columns * rows
    var isFixedRow = false
    var autoPlay = true
    val isRunning: Boolean
        get() {
        return running
    }
//    var maxCycles = 0
//    var currentCycle = 0
    var stateChangeListener: StateChangeListener? = null

    var renderRow = 0
    var renderColumn = 0
    var currentFrame = 0
    private var running = false
    private val srcFrame = Rect()
    private val dstFrame = Rect()
    private var timer = Timer()

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(
                attrs, R.styleable.SpriteView, 0, 0
            )
            image = a.getDrawable(R.styleable.SpriteView_src)?.toBitmap()
            columns = a.getInt(R.styleable.SpriteView_columns, columns)
            rows = a.getInt(R.styleable.SpriteView_rows, rows)
            fps = a.getInt(R.styleable.SpriteView_fps, fps)
            lastFrame = a.getInt(R.styleable.SpriteView_lastFrame, lastFrame)
            renderRow = a.getInt(R.styleable.SpriteView_renderRow, renderRow)
            isFixedRow = a.getBoolean(R.styleable.SpriteView_isFixedRow, isFixedRow)
            autoPlay = a.getBoolean(R.styleable.SpriteView_autoPlay, autoPlay)

//            maxCycles = a.getInt(R.styleable.SpriteView_cycles, maxCycles)
            a.recycle()
            if (autoPlay)
                start()
        }
    }

    constructor(context: Context) : this(context, null)


    fun start() {
        stop()

        if (image != null) {
            spriteWidth = image!!.width.toDouble() / columns
            spriteHeight = image!!.height.toDouble() / rows

            timer.schedule(0, (1000 / fps).toLong()) {
                running = true
                invalidate()
                if (lastFrame == currentFrame) {
                    resetFrames()
                }
                currentFrame++
//            if (maxCycles > 0 && currentCycle > maxCycles) {
//                stop()
//                return@schedule
//            }
//            if (currentFrame == 0) {
//                currentCycle += 1
//            }
            }
            post {
                stateChangeListener?.onStart(this)
            }
        }
    }

    fun stop() {
        pause()
        if (currentFrame > 0)
            post {
                stateChangeListener?.onStop(this)
            }

        resetFrames()
        invalidate()
        running = false
//        currentCycle = 0
    }

    fun pause() {
        timer.cancel()
        timer = Timer()
    }

    fun resetFrames() {
        renderRow = if (isFixedRow) renderRow else 0
        renderColumn = 0
        currentFrame = 0
    }

    override fun onDraw(canvas: Canvas) {
        if (image != null) {
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

            canvas.drawBitmap(image!!, srcFrame, dstFrame, null)
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