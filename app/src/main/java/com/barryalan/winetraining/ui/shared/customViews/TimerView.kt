package com.barryalan.winetraining.ui.shared.customViews

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi

class TimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var time = "00:00"
    private var color = Color.GREEN
    private var radius = 200f
    private var path = Path()
    private val outlineWidth = 10f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 80.0f
        typeface = Typeface.create("Roboto", Typeface.BOLD)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        path.addCircle(width / 2f, height / 2f, radius, Path.Direction.CW)


        canvas.save()
        canvas.clipOutPath(path)
        paint.color = Color.BLACK
        canvas.drawCircle(width / 2f, height / 2f, radius + outlineWidth, paint)
        canvas.restore()


        paint.color = color
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)
        paint.color = Color.BLACK
        canvas.drawText(time, width / 2f, height / 2f + paint.textSize / 3, paint)


    }

    fun setClockColor(newColor: Int) {
        if (color != newColor) {
            color = newColor
            invalidate()
        }
    }

    fun setClockTime(newTime: String) {
        if (time != newTime) {
            time = newTime
            invalidate()
        }
    }

    fun setClockRadius(newRadius: Float) {
        if (radius != newRadius) {
            radius = newRadius
            invalidate()
        }
    }


}