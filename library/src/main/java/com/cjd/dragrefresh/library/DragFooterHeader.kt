package com.cjd.dragrefresh.library


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
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
) : FrameLayout(context, attributeSet, defStyleAttr), OnDragUICallback {

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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ivLoadingLeft?.clearAnimation()
        ivLoadingRight?.clearAnimation()
    }

    override fun onCallback(view: View, state: Int, moveY: Int) {
        if (state == DragRefreshLayout.DRAG_UI_STATE_FINISH) {
            if (!isRunning) {
                tvTitle?.text = "正在加载"
                isRunning = true
                ivLoadingLeft?.let {
                    it.clearAnimation()
                    it.startAnimation(rotateAnimation)
                }
                ivLoadingRight?.let {
                    it.clearAnimation()
                    it.startAnimation(rotateAnimationReversal)
                }
            }
        } else if (state == DragRefreshLayout.DRAG_UI_STATE_BEGIN) {
            tvTitle?.text = "上拉刷新"
            if (isRunning) {
                isRunning = false
                ivLoadingLeft?.clearAnimation()
                ivLoadingRight?.clearAnimation()
            }
        } else if (state == DragRefreshLayout.DRAG_UI_STATE_DRAGGING) {
            if (moveY >= view.measuredHeight / 2) {
                var rotation = 360f / (view.measuredHeight) * moveY
                ivLoadingLeft?.let {

                    it.rotation = rotation
                }
                ivLoadingRight?.let {
                    it.rotation = -rotation
                }
            }

            if (moveY > view.measuredHeight) {
                tvTitle?.text = "松开加载"
            }
        }
    }


}