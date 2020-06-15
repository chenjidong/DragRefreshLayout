package com.cjd.dragrefresh

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cjd.dragrefresh.library.DragDefaultHeader
import com.cjd.dragrefresh.library.DragLogUtil
import com.cjd.dragrefresh.library.DragRefreshLayout
import com.cjd.dragrefresh.library.OnDragUICallback
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_recycler_view.*
import kotlinx.android.synthetic.main.activity_scroll_view.layout
import kotlinx.android.synthetic.main.recycler_item_view.view.*

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/22/0022
 * description
 */
class RecyclerViewActivity : AppCompatActivity(), OnDragUICallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        rv_content.layoutManager = LinearLayoutManager(this)
        val list = mutableListOf<String>()
        for (index in 0..20) {
            list.add("item $index")
        }
        rv_content.adapter = Adapter(this, list)
        layout.addDragUICallback(this)
        layout.setHeader(DragDefaultHeader(this))
        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            val absoluteOffset = kotlin.math.abs(p1)
            if (absoluteOffset == p0.totalScrollRange) {
                layout?.isEnabled = false
            } else if (absoluteOffset == 0)
                layout?.isEnabled = true
            else {

                if (layout?.isEnabled == true)
                    layout?.isEnabled = false
            }
        })
    }

    private fun getData() {

        Handler().postDelayed({
            layout?.completeRefresh(0, 0)
        }, 3000)
    }


    class Adapter(val context: Context, val list: List<String>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view =
                LayoutInflater.from(context).inflate(R.layout.recycler_item_view, parent, false)
            return RecyclerViewHolder(view)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            holder.itemView.tv_title.text = list[position]
        }

    }

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCallback(view: View, state: Int, moveY: Int) {

        if (state == DragRefreshLayout.DRAG_UI_STATE_FINISH) {
            DragLogUtil.d("state:$state move:$moveY")
            getData()
        }

    }
}