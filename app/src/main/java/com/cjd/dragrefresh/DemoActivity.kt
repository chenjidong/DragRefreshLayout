package com.cjd.dragrefresh

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cjd.dragrefresh.library.DragDefaultHeader
import com.cjd.dragrefresh.library.DragFooterHeader
import com.cjd.dragrefresh.library.DragRefreshLayout
import com.cjd.dragrefresh.library.OnDragUICallback
import kotlinx.android.synthetic.main.activity_demo.*

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/22/0022
 * description
 */
class DemoActivity : AppCompatActivity(), OnDragUICallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        layout.addDragUICallback(this)

        layout.setHeader(DragDefaultHeader(this).apply {
            layout.addDragUICallback(this)
        })
        layout.setContent(TextView(this).apply {
            this.isClickable = true
            this.setTextColor(Color.BLACK)
            this.gravity = Gravity.CENTER
            this.textSize = 20f
            this.text = "this is content view you can try slide down or up"
            this.setBackgroundColor(Color.RED)
        })
        layout.setFooter(DragFooterHeader(this).apply {
            layout.addDragUICallback(this)
        })
    }

    override fun onCallback(view: View, state: Int, moveY: Int) {
        if (state == DragRefreshLayout.DRAG_UI_STATE_FINISH)
            layout?.completeRefresh()
    }
}