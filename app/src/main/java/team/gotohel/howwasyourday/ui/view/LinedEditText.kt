package team.gotohel.howwasyourday.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatEditText

class LinedEditText// we need this constructor for LayoutInflater
    (context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    private val mRect: Rect = Rect()
    private val mPaint: Paint = Paint()

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = -0x7fffff01
    }

    override fun onDraw(canvas: Canvas) {
        val count = lineCount
        val r = mRect
        val paint = mPaint

        val lineHeight = lineHeight
        val height = height

        var baseline = 0
        for (i in 0 until count) {
            baseline = getLineBounds(i, r)

            drawUnderLine(canvas, r, baseline, paint)
        }

        while (baseline < height) {
            drawUnderLine(canvas, r, baseline, paint)
            baseline += lineHeight
        }

        super.onDraw(canvas)
    }

    private fun drawUnderLine(canvas: Canvas, r: Rect, y: Int, paint: Paint) {
        canvas.drawLine(r.left.toFloat(), (y + 1).toFloat(), r.right.toFloat(), (y + 1).toFloat(), paint)
    }
}
