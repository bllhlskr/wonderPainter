package com.eulersoft.draw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import java.nio.file.Path

class DrawingView(context:Context,attrs : AttributeSet) :View(context,attrs){
    private var mDrawPath : CustomPath? = null
    private var mCanvasBitmap:  Bitmap? = null
    private var mDrawPaint : Paint? = null
    private var mCanvasPaint : Paint? = null
    private var mBrushSize : Float = 0.toFloat()
    private var color = Color.BLUE
    private var canvas : Canvas ? = null
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPats = ArrayList<CustomPath>()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    fun onClickUndo(){
        if(mPaths.size >0){
            mUndoPats.add(mPaths.removeAt(mPaths.size-1))
            invalidate()
        }
    }
    fun reset(){
        mPaths.clear()
        mUndoPats.clear()
        invalidate()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for(path in mPaths){
            mDrawPaint!!.strokeWidth = path.BruchThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path,mDrawPaint!!)

        }
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)
        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.BruchThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!,mDrawPaint!!)

        }

    }

// if get an error change the nullable touchx and y
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                mDrawPath!!.color = color
                mDrawPath!!.BruchThickness = mBrushSize

                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!!,touchY!!)
            }
            MotionEvent.ACTION_MOVE->{
                mDrawPath!!.lineTo(touchX!!,touchY!!)
            }
            MotionEvent.ACTION_UP->{
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color,mBrushSize)
            }
            else->{
                return false
            }
        }
        invalidate()
        return true
    }


    fun setSizeForBrush(newSize : Float ){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            newSize,resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize

    }

    fun setColor(newColor : String){
        color =Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    init{
        setUpDrawing()
    }


    private fun setUpDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20.toFloat()

    }

   internal inner class CustomPath(var color : Int ,
   var BruchThickness : Float): android.graphics.Path(){

   }


}

