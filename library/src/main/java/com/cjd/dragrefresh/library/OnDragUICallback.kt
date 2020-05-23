package com.cjd.dragrefresh.library

import android.view.View

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/22/0022
 * description
 */
interface OnDragUICallback {

    fun onHeaderRefresh(header: View)

    fun onFooterRefresh(footer: View)

}