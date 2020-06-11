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


class SpriteView(context: Context,attrs: AttributeSet?) : View(context, attrs) {
    var image: Bitmap? = null
    var spriteWidth = 0.0
    var spriteHeight = 0.0
    var columns = 4
    var rows = 4
    var fps = columns * rows
    var lastFrame = fps
    var isFixedRow = false

    var renderRow = 0
    var renderColumn = 0
    private val srcFrame = Rect()
    private val dstFrame = Rect()
    private var timer = Timer()
    private var currentFrame = 0


    init {
        if (attrs != null){
            val a = context.obtainStyledAttributes(
                attrs, R.styleable.SpriteView, 0, 0
            )
            image=a.getDrawable(R.styleable.SpriteView_src)?.toBitmap()
            columns = a.getInt(R.styleable.SpriteView_columns,columns)
            rows = a.getInt(R.styleable.SpriteView_rows,rows)
            fps = a.getInt(R.styleable.SpriteView_fps,fps)
            lastFrame = a.getInt(R.styleable.SpriteView_lastFrame,lastFrame)
            renderRow = a.getInt(R.styleable.SpriteView_renderRow,renderRow)
            isFixedRow = a.getBoolean(R.styleable.SpriteView_isFixedRow,isFixedRow)
            start()
        }
    }

    constructor(context: Context) : this(context,null)



    fun start() {
        stop()
        if (image != null) {
            spriteWidth = image!!.width.toDouble() / columns
            spriteHeight = image!!.height.toDouble() / rows
        }
        timer.schedule(0, (1000 / fps).toLong()) {
            invalidate()
        }
    }

    fun stop(){
        timer.cancel()
        timer = Timer()
    }
    override fun onDraw(canvas: Canvas?) {
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

            canvas?.drawBitmap(image!!, srcFrame, dstFrame, null)
            if (renderColumn == columns - 1 && !isFixedRow) {
                renderRow = ++renderRow % rows
            }
            renderColumn = ++renderColumn % columns
            currentFrame++
            if(lastFrame == currentFrame){
                renderRow = if(isFixedRow) renderRow else 0
                renderColumn = 0
                currentFrame = 0
            }
        }
    }
}