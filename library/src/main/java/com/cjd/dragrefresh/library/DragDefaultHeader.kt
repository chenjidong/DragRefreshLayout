package com.cjd.dragrefresh.library


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/11/0011
 * description
 */
class DragDefaultHeader @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {

    private val rotateAnimation: RotateAnimation by lazy {
        RotateAnimation(
            0f,
            360f,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            this.interpolator = LinearInterpolator()
            this.duration = 500
            this.fillAfter = true
            this.repeatCount = Animation.START_ON_FIRST_FRAME
        }
    }

    private var ivLoading: ImageView? = null
    private var tvTitle: TextView? = null

    init {
        val header = LayoutInflater.from(context).inflate(R.layout.drag_default_header, this)
        ivLoading = header.findViewById(R.id.drag_loading)
        tvTitle = header.findViewById(R.id.drag_title)

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ivLoading?.startAnimation(rotateAnimation)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ivLoading?.clearAnimation()
    }

}