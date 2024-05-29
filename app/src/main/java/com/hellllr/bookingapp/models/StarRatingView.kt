package com.bhelllr.eventsapp.models



import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class StarRatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var starCount: Int = 5
    private var rating: Int = 0
    private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val starPath = Path()
    private val filledStarColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark)
    private val emptyStarColor = ContextCompat.getColor(context, android.R.color.darker_gray)
    private val starSize = 100 // size of each star in pixels

    init {
        starPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until starCount) {
            val x = i * (starSize + 20) + paddingStart
            val y = paddingTop
            drawStar(canvas, x.toFloat(), y.toFloat(), if (i < rating) filledStarColor else emptyStarColor)
        }
    }

    private fun drawStar(canvas: Canvas, x: Float, y: Float, color: Int) {
        starPaint.color = color
        starPath.reset()
        starPath.moveTo(x, y - starSize / 2f)
        for (i in 1..5) {
            val angle = Math.toRadians((i * 144).toDouble())
            starPath.lineTo(
                (x + (starSize / 2f * Math.cos(angle))).toFloat(),
                (y - (starSize / 2f * Math.sin(angle))).toFloat()
            )
        }
        starPath.close()
        canvas.drawPath(starPath, starPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val newRating = ((event.x - paddingStart) / (starSize + 20)).toInt() + 1
                if (newRating != rating) {
                    rating = newRating.coerceIn(0, starCount)
                    invalidate()
                }
            }
        }
        return true
    }

    fun setRating(newRating: Int) {
        rating = newRating.coerceIn(0, starCount)
        invalidate()
    }

    fun getRating(): Int {
        return rating
    }
}
