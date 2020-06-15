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
class DragFooterHeader @JvmOverloads constructor(
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
    private val rotateAnimationReversal: RotateAnimation by lazy {
        RotateAnimation(
            360f,
            0f,
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

    private var ivLoadingLeft: ImageView? = null
    private var ivLoadingRight: ImageView? = null
    private var tvTitle: TextView? = null
    private var isRunning = false

    init {
        val header = LayoutInflater.from(context).inflate(R.layout.drag_default_footer, this)
        ivLoadingLeft = header.findViewById(R.id.drag_loading_left)
        ivLoadingRight = header.findViewById(R.id.drag_loading_right)
        tvTitle = header.findViewById(R.id.drag_title)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ivLoadingLeft?.let {
            it.clearAnimation()
            it.startAnimation(rotateAnimation)
        }
        ivLoadingRight?.let {
            it.clearAnimation()
            it.startAnimation(rotateAnimationReversal)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ivLoadingLeft?.clearAnimation()
        ivLoadingRight?.clearAnimation()
    }

}