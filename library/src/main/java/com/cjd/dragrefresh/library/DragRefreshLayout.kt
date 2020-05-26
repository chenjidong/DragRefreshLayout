package com.cjd.dragrefresh.library

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

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

    companion object {
        const val DRAG_UI_STATE_BEGIN = 0
        const val DRAG_UI_STATE_DRAGGING = 1
        const val DRAG_UI_STATE_FINISH = 2
        const val DRAG_UI_STATE_CANCEL = -1
    }


    var headerView: View? = null
        private set(value) {
            field = value
        }
    var footerView: View? = null
        private set(value) {
            field = value
        }
    var contentView: View? = null
        private set(value) {
            field = value
        }

    private val dragUICallbacks = mutableListOf<OnDragUICallback>()

    private var headerHeight = 0
    private var footerHeight = 0
    private var offsetTopY = 0
    private var offsetBottomY = 0
    private var lastDownY = 0
    private var touchTopFlag = false //用户下滑后 不松开触摸 且胡乱滑动时
    private var touchBottomFlag = false //同上


    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 3)
            throw RuntimeException("子view 不能超过3个")

        val child1 = getChildAt(0)
        val child2 = getChildAt(1)
        val child3 = getChildAt(2)

        if (child1 != null && childCount > 1) {
            headerView = child1
        }

        if (child2 != null) {
            contentView = child2
        } else if (child1 != null && childCount == 1)
            contentView = child1

        if (child3 != null) {
            footerView = child3
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dragUICallbacks.clear()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)


        headerView?.let {
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val lp = it.layoutParams as MarginLayoutParams
            headerHeight = it.measuredHeight + lp.topMargin + lp.bottomMargin
        }

        contentView?.let {
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

        footerView?.let {
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val lp = it.layoutParams as MarginLayoutParams
            footerHeight = it.measuredHeight + lp.topMargin + lp.bottomMargin
        }

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        headerView?.let {
            val lp = it.layoutParams as MarginLayoutParams
            val left = paddingLeft + lp.leftMargin
            val top = paddingTop + lp.topMargin + offsetTopY - headerHeight
            val right = left + it.measuredWidth
            val bottom = top + it.measuredHeight

            it.layout(left, top, right, bottom)
        }

        contentView?.let {
            val lp = it.layoutParams as MarginLayoutParams
            val left = paddingLeft + lp.leftMargin
            val top = paddingTop + lp.topMargin + offsetTopY
            val right = left + it.measuredWidth
            val bottom = (top + it.measuredHeight) - offsetBottomY

            it.layout(left, top, right, bottom)
        }

        footerView?.let {
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
                lastDownY = ev.y.toInt()
                //TODO 避免后续 MOVE 和 UP /子view 无法响应
                super.dispatchTouchEvent(ev)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val moveY = ev.y.toInt()
                if (lastDownY < moveY) {
                    if (checkTouchDirection(true))
                        return super.dispatchTouchEvent(ev)
                    dragDown(ev)
                } else {
                    if (checkTouchDirection(false))
                        return super.dispatchTouchEvent(ev)
                    dragUp(ev)
                }
            }

            MotionEvent.ACTION_UP and MotionEvent.ACTION_CANCEL -> {
                val moveY = ev.y.toInt()
                if (lastDownY < moveY) {
                    dragDownFinish(ev)
                } else {
                    dragUpFinish(ev)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(ev)
    }

    /**
     * 添加 刷新回调
     * @param onDragUICallback obj
     */
    fun addDragUICallback(onDragUICallback: OnDragUICallback) {
        dragUICallbacks.add(onDragUICallback)
    }

    /**
     * 设置 header 默认高度为wrap_content
     * @param headerView 自定义view
     */
    fun setHeader(headerView: View) {
        this.headerView?.let {
            if (it != headerView) {
                removeView(it)
            }
        }
        this.headerView = headerView
        addView(
            this.headerView,
            MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT)
        )
    }

    /**
     * 设置 content 默认高度为 MATCH_PARENT
     * @param contentView 自定义view
     */
    fun setContent(contentView: View) {
        this.contentView?.let {
            if (it != contentView) {
                removeView(it)
            }
        }
        this.contentView = contentView
        addView(
            this.contentView,
            MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.MATCH_PARENT)
        )
    }

    /**
     * 设置 content 默认高度为 WRAP_CONTENT
     * @param footerView 自定义view
     */
    fun setFooter(footerView: View) {
        this.footerView?.let {
            if (it != footerView) {
                removeView(it)
            }
        }
        this.footerView = footerView
        addView(
            this.footerView,
            MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT)
        )
    }

    /**
     * 完成 header 和 footer 隐藏
     */
    fun completeRefresh(delayMillis: Long = 3000, duration: Long = 500) {
        if (offsetBottomY != 0)
            completePullUpRefresh(delayMillis, duration)

        if (offsetTopY != 0)
            completePullDownRefresh(delayMillis, duration)
    }

    /**
     * 完成下拉刷新
     * @param delayMillis 延迟时间 单位毫秒
     * @param duration 动画时间
     */
    fun completePullDownRefresh(delayMillis: Long = 3000, duration: Long = 500) {
        postDelayed({
            startValueAnimator(true, offsetTopY, 0, duration)
        }, delayMillis)
    }

    /**
     * 完成上拉刷新
     * @param delayMillis 延迟时间 单位毫秒
     * @param duration 动画时间
     */
    fun completePullUpRefresh(delayMillis: Long = 3000, duration: Long = 500) {
        postDelayed({
            startValueAnimator(false, offsetBottomY, 0, duration)
        }, delayMillis)
    }

    /**
     * 向下拖拽
     * @param ev touch 对象
     */
    private fun dragDown(ev: MotionEvent): Boolean {
        headerView?.let { head ->
            contentView?.let {
                if (!canScrollVertically(it, -1) || touchTopFlag) {//内容view 滚动到顶部
                    val moveY = ev.y.toInt()
                    val distanceY = moveY - lastDownY
                    if (!touchTopFlag) {
                        lastDownY = moveY
                        touchTopFlag = true
                        notifyCallbacks(head, DRAG_UI_STATE_BEGIN, distanceY)
                    } else
                        notifyCallbacks(head, DRAG_UI_STATE_DRAGGING, distanceY)

                    offsetTopY = distanceY

                    requestLayout()
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 向下拖拽 完成 或者取消时 （触摸抬起） 拖拽范围超出 header 高度 则显示 否则动画隐藏
     * @param ev touch 对象
     */
    private fun dragDownFinish(ev: MotionEvent) {
        headerView?.let { head ->
            contentView?.let {
                if (!canScrollVertically(it, -1) || touchTopFlag) {
                    val moveY = ev.y.toInt()
                    val distanceY = moveY - lastDownY

                    offsetTopY = if (offsetTopY > headerHeight) { //下拉不超过head height 则隐藏
                        notifyCallbacks(head, DRAG_UI_STATE_FINISH, distanceY)
                        headerHeight
                    } else {
                        notifyCallbacks(head, DRAG_UI_STATE_CANCEL, distanceY)
                        0
                    }

                    if (touchTopFlag)
                        touchTopFlag = false

                    if (distanceY > offsetTopY)
                        startValueAnimator(true, distanceY, offsetTopY)

                }
            }
        }
    }

    /**
     * 向上拖拽
     * @param ev touch 对象
     */
    private fun dragUp(ev: MotionEvent): Boolean {
        footerView?.let { footer ->
            contentView?.let {
                if (!canScrollVertically(it, 1) || touchBottomFlag) {//滚动到底部
                    val moveY = ev.y.toInt()
                    val distanceY = lastDownY - moveY
                    if (!touchBottomFlag) {
                        lastDownY = moveY
                        touchBottomFlag = true

                        notifyCallbacks(footer, DRAG_UI_STATE_BEGIN, distanceY)
                    } else
                        notifyCallbacks(footer, DRAG_UI_STATE_DRAGGING, distanceY)

                    offsetBottomY = distanceY
                    requestLayout()
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 向上拖拽 完成 或者取消时 （触摸抬起） 拖拽范围超出 footer 高度 则显示 否则动画隐藏
     * @param ev touch 对象
     */
    private fun dragUpFinish(ev: MotionEvent) {
        footerView?.let { footer ->
            contentView?.let {
                if (!canScrollVertically(it, 1) || touchBottomFlag) {
                    val moveY = ev.y.toInt()
                    val distanceY = lastDownY - moveY

                    offsetBottomY = if (offsetBottomY > footerHeight) {
                        notifyCallbacks(footer, DRAG_UI_STATE_FINISH, distanceY)
                        footerHeight
                    } else {
                        notifyCallbacks(footer, DRAG_UI_STATE_CANCEL, distanceY)
                        0
                    }

                    if (touchBottomFlag)
                        touchBottomFlag = false

                    if (distanceY > offsetBottomY)
                        startValueAnimator(false, distanceY, offsetBottomY)
                }
            }

        }
    }

    /**
     * 检查触摸方向  （按住屏幕时 下拉 然后在上拉 需要恢复 header 高度）
     * @param isTop true 下拉  false 上拉
     */
    private fun checkTouchDirection(isTop: Boolean): Boolean {
        if (isTop) {
            if (touchBottomFlag) {//上拉动作成立后 再次上拉时取消下拉动作
                completePullUpRefresh(0, 0)
                touchBottomFlag = false
                return true
            }
        } else {
            if (touchTopFlag) {//下拉动作成立后 再次上拉时取消下拉动作
                completePullDownRefresh(0, 0)
                touchTopFlag = false
                return true
            }
        }
        return false
    }

    /**
     * 检查 view 是否滚动到底部或顶部
     * @param view 要检查的view
     * @param direction < 0 顶部  > 0 底部
     */
    private fun canScrollVertically(view: View, direction: Int): Boolean {
        return view.canScrollVertically(direction)
    }

    /**
     * 属性动画，用于恢复 header 或 footer
     * @param isTop true header  false footer
     * @param start y 轴 起始点
     * @param end y 轴终点
     * @param duration 动画世界
     */
    private fun startValueAnimator(isTop: Boolean, start: Int, end: Int, duration: Long = 500) {
        if (start != end) {
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

    /**
     * 下/上拉刷新回调
     */
    private fun notifyCallbacks(view: View, state: Int, moveY: Int) {
        dragUICallbacks.forEach {
            it.onCallback(view, state, moveY)
        }
    }
}