package com.example.bayyinly.ui.tasbih

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import com.example.bayyinly.R

class BeadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var onSwipe: (() -> Unit)? = null

    private var count: Int = 0
    private var target: Int = 33
    private var ringPosition: Int = 0

    private val greenColor = ContextCompat.getColor(context, R.color.green)
    private val darkGreenColor = Color.parseColor("#2E4B26") // Deep dark green for counted beads
    private val inactiveGrayColor = Color.parseColor("#D1D1D1")

    private val mainBeadPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val activeRingBeadPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val inactiveRingBeadPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(40, 0, 0, 0)
        maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
    }

    private var outerRadius = 0f
    private var innerBeadRadius = 0f
    private var smallBeadRadius = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var beadOffsetY = 0f

    private var downY = 0f
    private var swipeConsumed = false
    private val swipeThreshold by lazy { resources.displayMetrics.density * 40f }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    /**
     * Updates the counter and redraws the view.
     * Small beads will turn dark green if their index is less than [newCount].
     */
    fun updateCount(newCount: Int, newTarget: Int = 33, totalCount: Int = 0) {
        this.count = newCount
        this.target = newTarget
        this.ringPosition = totalCount % 33
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        outerRadius = minOf(w, h) / 2f * 0.95f
        innerBeadRadius = outerRadius * 0.52f
        smallBeadRadius = outerRadius * 0.07f
        
        textPaint.textSize = innerBeadRadius * 0.6f

        // Shader for counted (active) small beads - vibrant dark green
        activeRingBeadPaint.shader = RadialGradient(
            0f, 0f, smallBeadRadius * 1.5f,
            intArrayOf(greenColor, darkGreenColor),
            floatArrayOf(0.2f, 1f),
            Shader.TileMode.CLAMP
        )

        // Shader for uncounted (inactive) small beads - subtle gray
        inactiveRingBeadPaint.shader = RadialGradient(
            0f, 0f, smallBeadRadius * 1.5f,
            intArrayOf(Color.parseColor("#E0E0E0"), inactiveGrayColor),
            floatArrayOf(0.2f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        // 1. Draw ring — always 33 beads, one lights per swipe, resets at 33
        for (i in 0 until 33) {
            val angle = Math.toRadians((i * 360.0 / 33) - 90.0)
            val bx = centerX + (outerRadius * 0.74f) * Math.cos(angle).toFloat()
            val by = centerY + (outerRadius * 0.74f) * Math.sin(angle).toFloat()
            canvas.save()
            canvas.translate(bx, by)
            canvas.drawCircle(0f, 0f, smallBeadRadius, if (i < ringPosition) activeRingBeadPaint else inactiveRingBeadPaint)
            canvas.restore()
        }

        // 2. Draw Main Bead with Offset (Swiping animation)
        canvas.save()
        canvas.translate(0f, beadOffsetY)

        // Subtle shadow beneath the main bead
        canvas.drawCircle(centerX, centerY + innerBeadRadius * 0.1f, innerBeadRadius, shadowPaint)

        // Gradient for main bead using app's green palette
        mainBeadPaint.shader = RadialGradient(
            centerX - innerBeadRadius * 0.3f,
            centerY - innerBeadRadius * 0.3f,
            innerBeadRadius * 1.6f,
            intArrayOf(Color.WHITE, greenColor, darkGreenColor),
            floatArrayOf(0f, 0.25f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(centerX, centerY, innerBeadRadius, mainBeadPaint)

        // Large Counter Text inside the main bead
        canvas.drawText(count.toString(), centerX, centerY + textPaint.textSize / 3.5f, textPaint)
        
        // Swipe arrow hint (only visible when not swiping)
        if (beadOffsetY < 5f) {
            val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                alpha = 80
                strokeWidth = 6f
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
            }
            val ay = centerY + innerBeadRadius * 0.45f
            val arrowLen = 40f
            canvas.drawLine(centerX, ay, centerX, ay + arrowLen, arrowPaint)
            canvas.drawLine(centerX - 12f, ay + arrowLen - 12f, centerX, ay + arrowLen, arrowPaint)
            canvas.drawLine(centerX + 12f, ay + arrowLen - 12f, centerX, ay + arrowLen, arrowPaint)
        }

        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = event.y
                swipeConsumed = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val delta = event.y - downY
                if (delta > 0f) {
                    // Visual feedback: Bead moves down with the finger
                    beadOffsetY = delta.coerceAtMost(innerBeadRadius * 0.35f)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val delta = event.y - downY
                if (delta >= swipeThreshold && !swipeConsumed) {
                    swipeConsumed = true
                    onSwipe?.invoke() // Trigger increment in Fragment
                    animateSuccess()
                } else {
                    animateReset()
                }
            }
        }
        return true
    }

    private fun animateSuccess() {
        // Pop down animation when swipe is successful
        ValueAnimator.ofFloat(beadOffsetY, innerBeadRadius * 0.45f).apply {
            duration = 100
            addUpdateListener { beadOffsetY = it.animatedValue as Float; invalidate() }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) = animateReset()
            })
            start()
        }
    }

    private fun animateReset() {
        // Snap back animation with overshoot for a physical feel
        ValueAnimator.ofFloat(beadOffsetY, 0f).apply {
            duration = 450
            interpolator = OvershootInterpolator(2.2f)
            addUpdateListener { beadOffsetY = it.animatedValue as Float; invalidate() }
            start()
        }
    }
}
