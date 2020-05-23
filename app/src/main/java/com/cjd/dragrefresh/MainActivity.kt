package com.cjd.dragrefresh

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * @author chenjidong
 * @email 374122600@qq.com
 * created 2020/5/21/0021
 * description 下/上 拉刷新
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun demoClick(view: View) {
        startActivity(Intent(this, DemoActivity::class.java))
    }

    fun notScrollClick(view: View) {
        startActivity(Intent(this, NotScrollViewActivity::class.java))
    }

    fun scrollViewClick(view: View) {
        startActivity(Intent(this, ScrollViewActivity::class.java))
    }

    fun recyclerViewClick(view: View) {
        startActivity(Intent(this, RecyclerViewActivity::class.java))
    }

    fun listViewClick(view: View) {
        startActivity(Intent(this, ListViewActivity::class.java))
    }
}
