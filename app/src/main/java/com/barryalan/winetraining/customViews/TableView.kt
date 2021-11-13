package com.barryalan.winetraining.customViews


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class TableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val outlineWidth = 5f
    private var tRotation = 0f
    private var number = "0"
    private var color = Color.GRAY
    private var boothOrTable = 1

    fun getTableNumber() = number
    fun getTableColor() = color

    fun setTableColor(newColor: Int) {
        if (color != newColor) {
            color = newColor
            invalidate()
        }
    }

    fun setBoothOrTable(boothOrTable: Int) {
        if (this.boothOrTable != boothOrTable) {
            this.boothOrTable = boothOrTable
            invalidate()
        }
    }

    fun setTableRotation(newRotation: Float) {
        if (tRotation != newRotation) {
            tRotation = newRotation
            invalidate()
        }
    }

    fun setTableNumber(newNumber: String) {
        if (number != newNumber) {
            number = newNumber
            invalidate()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()

        //draw outline
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            canvas.clipOutRect(
                (width / 2) - width / 2 + outlineWidth,
                (height / 2) - height / 2 + outlineWidth,
                (width / 2) + width / 2 - outlineWidth,
                (height / 2) + height / 2 - outlineWidth,
            )
        } else {
            canvas.clipRect(
                (width / 2) - width / 2 + outlineWidth,
                (height / 2) - height / 2 + outlineWidth,
                (width / 2) + width / 2 - outlineWidth,
                (height / 2) + height / 2 - outlineWidth,
                Region.Op.DIFFERENCE
            )
        }

        paint.color = Color.BLACK

        if (boothOrTable == 0) {
            canvas.drawRoundRect(
                (width / 2f) - width / 2,
                (height / 2f) - height / 2,
                (width / 2f) + width / 2,
                (height / 2f) + height / 2,
                30f, 30f,
                paint
            )

            //restore the clip out
            canvas.restore()


            //draw inside
            paint.color = color
            canvas.drawRoundRect(
                (width / 2) - width / 2 + outlineWidth,
                (height / 2) - height / 2 + outlineWidth,
                (width / 2) + width / 2 - outlineWidth,
                (height / 2) + height / 2 - outlineWidth,
                12f, 12f,
                paint
            )
        } else {
            canvas.drawRect(
                (width / 2f) - width / 2,
                (height / 2f) - height / 2,
                (width / 2f) + width / 2,
                (height / 2f) + height / 2,
                paint
            )

            //restore the clip out
            canvas.restore()


            //draw inside
            paint.color = color
            canvas.drawRect(
                (width / 2) - width / 2 + outlineWidth,
                (height / 2) - height / 2 + outlineWidth,
                (width / 2) + width / 2 - outlineWidth,
                (height / 2) + height / 2 - outlineWidth,
                paint
            )

        }


        canvas.save()
        canvas.rotate(-tRotation, width / 2f, height / 2f)

        //draw text
        paint.color = Color.BLACK
        paint.textSize = height / 2f
        canvas.drawText(number, width / 2f, height / 2f + paint.textSize / 3, paint)

        //restore rotation
        canvas.restore()


    }

}
