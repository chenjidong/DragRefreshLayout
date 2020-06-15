package com.cjd.dragrefresh

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cjd.dragrefresh.library.DragRefreshLayout
import com.cjd.dragrefresh.library.OnDragUICallback
import kotlinx.android.synthetic.main.activity_scroll_view.*

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/22/0022
 * description
 */
class ScrollViewActivity : AppCompatActivity(), OnDragUICallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll_view)

        layout.addDragUICallback(this)
    }

    private fun getData() {

        Handler().postDelayed({
            layout?.completeRefresh(0, 0)
        }, 3000)
    }

    override fun onCallback(view: View, state: Int, moveY: Int) {
        if (state == DragRefreshLayout.DRAG_UI_STATE_FINISH)
            getData()

    }
}