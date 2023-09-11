package com.example.savewhatsapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.savewhatsapp.R

class CircularDottedLoader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var dotRadius = 12f
    private var dotSpacing = 40f
    private var dotColor = Color.BLUE

    private val paint = Paint().apply {
        isAntiAlias = true
        color = dotColor
    }

    private val dotCount = 12
    private var angleOffset = 0f

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CircularDottedLoader, 0, 0)
            dotRadius = typedArray.getDimension(R.styleable.CircularDottedLoader_dotRadius, dotRadius)
            dotSpacing = typedArray.getDimension(R.styleable.CircularDottedLoader_dotSpacing, dotSpacing)
            dotColor = typedArray.getColor(R.styleable.CircularDottedLoader_dotColor, dotColor)
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f

        for (i in 0 until dotCount) {
            val angle = Math.toRadians((i * (360 / dotCount) + angleOffset).toDouble())
            val x = cx + dotSpacing * Math.cos(angle).toFloat()
            val y = cy + dotSpacing * Math.sin(angle).toFloat()
            canvas.drawCircle(x, y, dotRadius, paint)
        }
    }

    fun startLoadingAnimation() {
        val rotateAnimation = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.duration = 1500
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.repeatCount = Animation.INFINITE
        this.startAnimation(rotateAnimation)
    }

    fun stopLoadingAnimation() {
        this.clearAnimation()
    }
}
