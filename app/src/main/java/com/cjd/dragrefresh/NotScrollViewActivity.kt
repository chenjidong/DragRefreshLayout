package com.cjd.dragrefresh

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cjd.dragrefresh.library.DragRefreshLayout
import com.cjd.dragrefresh.library.OnDragUICallback
import kotlinx.android.synthetic.main.activity_not_scroll_view.*

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/22/0022
 * description
 */
class NotScrollViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_scroll_view)

        layout.onDragUICallback = object : OnDragUICallback {
            override fun onCallback(view: View, state: Int, moveY: Int) {
                if (state == DragRefreshLayout.DRAG_UI_STATE_FINISH)
                    layout?.completeRefresh()
            }

        }
    }
}