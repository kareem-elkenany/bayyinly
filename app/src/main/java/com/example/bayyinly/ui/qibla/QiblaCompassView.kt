package com.example.bayyinly.ui.qibla

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class QiblaCompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val darkGreen = Color.parseColor("#2D402B")
    private val softGreen = Color.parseColor("#79AE6F")
    private val beige = Color.parseColor("#F8F5EA")
    private val sand = Color.parseColor("#D3CABA")
    private val gold = Color.parseColor("#C89B3C")

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = beige
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = sand
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8D856F")
        strokeWidth = 3f
        strokeCap = Paint.Cap.ROUND
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = darkGreen
        textAlign = Paint.Align.CENTER
        textSize = 32f
        isFakeBoldText = true
    }
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = softGreen
        style = Paint.Style.FILL
    }
    private val arrowAccentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = gold
        style = Paint.Style.FILL
    }
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = darkGreen
        style = Paint.Style.FILL
    }

    private var currentHeading: Float? = null
    private var qiblaBearing: Float? = null

    fun updateDirections(heading: Float?, bearing: Float?) {
        currentHeading = heading
        qiblaBearing = bearing
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width, height).toFloat()
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = size * 0.42f
        val heading = currentHeading ?: 0f

        canvas.drawCircle(centerX, centerY, radius, circlePaint)
        canvas.drawCircle(centerX, centerY, radius, strokePaint)
        drawTicks(canvas, centerX, centerY, radius, heading)
        drawCardinalLabels(canvas, centerX, centerY, radius, heading)
        drawQiblaArrow(canvas, centerX, centerY, radius, heading)
        canvas.drawCircle(centerX, centerY, radius * 0.08f, centerPaint)
    }

    private fun drawTicks(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, heading: Float) {
        for (degree in 0 until 360 step 15) {
            val relativeDegree = degree - heading - 90f
            val radians = Math.toRadians(relativeDegree.toDouble())
            val isMajorTick = degree % 45 == 0
            val tickLength = if (isMajorTick) radius * 0.11f else radius * 0.06f
            tickPaint.strokeWidth = if (isMajorTick) 4f else 2f

            val outerX = centerX + cos(radians).toFloat() * radius
            val outerY = centerY + sin(radians).toFloat() * radius
            val innerX = centerX + cos(radians).toFloat() * (radius - tickLength)
            val innerY = centerY + sin(radians).toFloat() * (radius - tickLength)
            canvas.drawLine(innerX, innerY, outerX, outerY, tickPaint)
        }
    }

    private fun drawCardinalLabels(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, heading: Float) {
        val labels = listOf(
            0f to "N",
            90f to "E",
            180f to "S",
            270f to "W"
        )

        labels.forEach { (bearing, label) ->
            val radians = Math.toRadians((bearing - heading - 90f).toDouble())
            val labelRadius = radius * 0.72f
            val x = centerX + cos(radians).toFloat() * labelRadius
            val y = centerY + sin(radians).toFloat() * labelRadius + textPaint.textSize / 3f
            textPaint.color = if (label == "N") softGreen else darkGreen
            canvas.drawText(label, x, y, textPaint)
        }
    }

    private fun drawQiblaArrow(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, heading: Float) {
        val bearing = qiblaBearing ?: return
        val relativeBearing = normalizeDegrees(bearing - heading)
        val arrowLength = radius * 0.72f
        val arrowWidth = radius * 0.18f

        canvas.save()
        canvas.rotate(relativeBearing, centerX, centerY)

        val arrowPath = Path().apply {
            moveTo(centerX, centerY - arrowLength)
            lineTo(centerX - arrowWidth, centerY + radius * 0.1f)
            lineTo(centerX, centerY - radius * 0.05f)
            lineTo(centerX + arrowWidth, centerY + radius * 0.1f)
            close()
        }
        canvas.drawPath(arrowPath, arrowPaint)

        val kaabaPath = Path().apply {
            val square = radius * 0.11f
            moveTo(centerX - square, centerY - arrowLength - square * 1.4f)
            lineTo(centerX + square, centerY - arrowLength - square * 1.4f)
            lineTo(centerX + square, centerY - arrowLength + square * 0.6f)
            lineTo(centerX - square, centerY - arrowLength + square * 0.6f)
            close()
        }
        canvas.drawPath(kaabaPath, arrowAccentPaint)
        canvas.restore()
    }

    private fun normalizeDegrees(value: Float): Float {
        return ((value % 360f) + 360f) % 360f
    }
}
