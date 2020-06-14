package com.codyzen.spriterunner

interface StateChangeListener {
    fun onUpdateFrame(view:SpriteView){}
    fun onStart(view:SpriteView){}
    fun onStop(view:SpriteView){}
}
