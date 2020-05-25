package com.cjd.dragrefresh

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cjd.dragrefresh.library.DragLogUtil
import com.cjd.dragrefresh.library.DragRefreshLayout
import com.cjd.dragrefresh.library.OnDragUICallback
import kotlinx.android.synthetic.main.activity_recycler_view.*
import kotlinx.android.synthetic.main.activity_scroll_view.layout
import kotlinx.android.synthetic.main.recycler_item_view.view.*

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/22/0022
 * description
 */
class RecyclerViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        rv_content.layoutManager = LinearLayoutManager(this)
        val list = mutableListOf<String>()
        for (index in 0..20) {
            list.add("item $index")
        }
        rv_content.adapter = Adapter(this, list)
        layout.onDragUICallback = object : OnDragUICallback {
            override fun onCallback(view: View, state: Int, moveY: Int) {
                DragLogUtil.d("$state $moveY")
                if (state == DragRefreshLayout.DRAG_UI_STATE_FINISH)
                    layout?.completeRefresh()
            }

        }
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
}