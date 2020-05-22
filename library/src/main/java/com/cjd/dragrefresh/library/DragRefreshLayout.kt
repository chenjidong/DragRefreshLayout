package com.cjd.dragrefresh.library

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/21/0021
 * description 下/上 拉刷新
 */
class DragRefreshLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {

    private lateinit var headerView: View
    private lateinit var contentView: View
    private lateinit var footerView: View

    private var headerHeight = 0
    private var footerHeight = 0
    private var offsetTopY = 0
    private var offsetBottomY = 0
    private var downY = 0
    private var bottomFlag = false //用户下滑后 不松开触摸 且胡乱滑动时
    private var topFlag = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 3)
            throw RuntimeException("子view 不能超过3个")

        val child1 = getChildAt(0)
        val child2 = getChildAt(1)
        val child3 = getChildAt(2)

        if (child1 != null) {
            headerView = child1
        } else {
            headerView = TextView(context).apply {
                this.isClickable = true
                this.setTextColor(Color.BLACK)
                this.gravity = Gravity.CENTER
                this.textSize = 20f
                this.text = "this is header view you"
                this.setBackgroundColor(Color.BLUE)
            }
            addView(
                headerView,
                MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT)
            )
        }

        if (child2 != null) {
            contentView = child2
        } else {
            contentView = TextView(context).apply {
                this.isClickable = true
                this.setTextColor(Color.BLACK)
                this.gravity = Gravity.CENTER
                this.textSize = 20f
                this.text = "this is content view you can try slide down or up"
                this.setBackgroundColor(Color.RED)
            }
            addView(
                contentView,
                MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.MATCH_PARENT)
            )
        }

        if (child3 != null) {
            footerView = child3
        } else {
            footerView = TextView(context).apply {
                this.isClickable = true
                this.setTextColor(Color.BLACK)
                this.gravity = Gravity.CENTER
                this.textSize = 20f
                this.text = "this is footer view you"
                this.setBackgroundColor(Color.GREEN)
            }
            addView(
                footerView,
                MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT)
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)


        headerView.let {
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val lp = it.layoutParams as MarginLayoutParams
            headerHeight = it.measuredHeight + lp.topMargin + lp.bottomMargin
        }

        contentView.let {
            val lp = it.layoutParams as MarginLayoutParams
            val childWidthMeasureSpec = getChildMeasureSpec(
                widthMeasureSpec,
                paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin,
                lp.width
            )
            val childHeightMeasureSpec = getChildMeasureSpec(
                heightMeasureSpec,
                paddingTop + paddingBottom + lp.topMargin,
                lp.height
            )

            it.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }

        footerView.let {
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val lp = it.layoutParams as MarginLayoutParams
            footerHeight = it.measuredHeight + lp.topMargin + lp.bottomMargin
        }

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        headerView.let {
            val lp = it.layoutParams as MarginLayoutParams
            val left = paddingLeft + lp.leftMargin
            val top = paddingTop + lp.topMargin + offsetTopY - headerHeight
            val right = left + it.measuredWidth
            val bottom = top + it.measuredHeight

            it.layout(left, top, right, bottom)
        }

        contentView.let {
            val lp = it.layoutParams as MarginLayoutParams
            val left = paddingLeft + lp.leftMargin
            val top = paddingTop + lp.topMargin + offsetTopY
            val right = left + it.measuredWidth
            val bottom = (top + it.measuredHeight) - offsetBottomY

            it.layout(left, top, right, bottom)
        }

        footerView.let {
            val lp = it.layoutParams as MarginLayoutParams
            val left = paddingLeft + lp.leftMargin
            val top = height - offsetBottomY
            val right = left + it.measuredWidth
            val bottom = top + it.measuredHeight

            it.layout(left, top, right, bottom)
        }
    }

    //TODO MarginLayoutParams 虽然继承 ViewGroup.LayoutParams 但是measureChildWithMargins 函数却转换异常 强行改变生成规则即可

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = ev.y.toInt()
                //TODO 避免后续 MOVE 和 UP 无法响应
                super.dispatchTouchEvent(ev)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val moveY = ev.y.toInt()
                Log.d("--->", "$downY $moveY")
                if (downY < moveY) {
                    if (bottomFlag) {//上拉动作成立后 再次上拉时取消下拉动作
                        bottomFlag = false
                        completePullUpRefresh(0, 0)
                        return super.dispatchTouchEvent(ev)
                    }

                    if (!contentView.canScrollVertically(-1) || topFlag) {//内容view 滚动到顶部

                        if (!topFlag)
                            topFlag = true

                        val distanceY = moveY - downY

                        offsetTopY = distanceY
                        requestLayout()
                        return true
                    }

                } else {
                    if (topFlag) {//下拉动作成立后 再次上拉时取消下拉动作
                        topFlag = false
                        completePullDownRefresh(0, 0)
                        return super.dispatchTouchEvent(ev)
                    }

                    if (!contentView.canScrollVertically(1) || bottomFlag) {//滚动到底部
                        if (!bottomFlag)
                            bottomFlag = true
                        offsetBottomY = downY - moveY
                        requestLayout()
                        return true
                    }
                }
            }

            MotionEvent.ACTION_UP and MotionEvent.ACTION_CANCEL -> {
                val moveY = ev.y.toInt()
                if (downY < moveY) {//往下拉

                    if (!contentView.canScrollVertically(-1) || topFlag) {
                        val distanceY = moveY - downY

                        offsetTopY = if (offsetTopY > headerHeight) { //下拉不超过head height 则隐藏
                            headerHeight
                        } else 0

                        if (distanceY > offsetTopY)
                            startValueAnimator(true, distanceY, offsetTopY)

                        if (topFlag)
                            topFlag = false

                        //TODO 完成刷新
                        completePullDownRefresh()
                    }
                } else {//往上拉

                    if (!contentView.canScrollVertically(1) || bottomFlag) {
                        val distanceY = downY - moveY

                        offsetBottomY = if (offsetBottomY > footerHeight) {
                            footerHeight
                        } else 0

                        if (distanceY > offsetBottomY)
                            startValueAnimator(false, distanceY, offsetBottomY)
                        if (bottomFlag)
                            bottomFlag = false

                        //TODO 完成刷新
                        completePullUpRefresh()
                    }
                }
            }
        }
        Log.d(
            "--->",
            "${contentView.canScrollVertically(-1)} ${contentView.canScrollVertically(1)} $topFlag $bottomFlag"
        )
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(ev)
    }

    fun completePullDownRefresh(delayMillis: Long = 3000, duration: Long = 500) {
        postDelayed({
            startValueAnimator(true, offsetTopY, 0, duration)
        }, delayMillis)
    }

    fun completePullUpRefresh(delayMillis: Long = 3000, duration: Long = 500) {
        postDelayed({
            startValueAnimator(false, offsetBottomY, 0, duration)
        }, delayMillis)
    }

    private fun startValueAnimator(isTop: Boolean, start: Int, end: Int, duration: Long = 500) {
        ValueAnimator.ofInt(start, end).apply {
            this.addUpdateListener {
                val value = it.animatedValue as Int
                if (isTop) {
                    offsetTopY = value
                } else
                    offsetBottomY = value
                requestLayout()
            }
            this.duration = duration
            this.start()
        }
    }
}