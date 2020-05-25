package com.cjd.dragrefresh

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cjd.dragrefresh.library.DragRefreshLayout
import com.cjd.dragrefresh.library.OnDragUICallback
import kotlinx.android.synthetic.main.activity_list_view.*

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/22/0022
 * description
 */
class ListViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)
        val list = mutableListOf<String>()
        for (index in 0..20) {
            list.add("item $index")
        }
        lv_content.adapter = Adapter(this, list)
        layout.onDragUICallback = object : OnDragUICallback {
            override fun onCallback( view: View, state: Int, moveY: Int) {
                if (state == DragRefreshLayout.DRAG_UI_STATE_FINISH)
                    layout?.completeRefresh()
            }

        }
    }


    class Adapter(val context: Context, val list: List<String>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view =
                LayoutInflater.from(context).inflate(R.layout.recycler_item_view, parent, false)
            view.findViewById<TextView>(R.id.tv_title).text = list[position]
            return view
        }

        override fun getItem(position: Int): Any = list[position]

        override fun getItemId(position: Int): Long {
            return 0L
        }

        override fun getCount(): Int = list.size
    }


}