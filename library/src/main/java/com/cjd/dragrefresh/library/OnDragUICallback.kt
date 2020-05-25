package com.cjd.dragrefresh.library

import android.view.View

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/22/0022
 * description
 */
interface OnDragUICallback {

    /**
     * @param view  header or footer view
     * @param state 拖拽状态
     * @param moveY y轴
     */
    fun onCallback(view: View, state: Int, moveY: Int)
}