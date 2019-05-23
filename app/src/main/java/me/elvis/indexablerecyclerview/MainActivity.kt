package me.elvis.indexablerecyclerview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.SectionIndexer
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val datas = arrayOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = MyAdapter()
        list.adapter = adapter
    }

    inner class MyAdapter : RecyclerView.Adapter<ViewHolder>(), SectionIndexer {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(layoutInflater.inflate(R.layout.item, p0, false))
        }

        override fun getItemCount(): Int {
            return datas.size
        }

        override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
            p0.tv.text = datas[p1]
        }

        override fun getSections(): Array<String> {
            return datas
        }

        override fun getSectionForPosition(position: Int): Int {
            return 0
        }

        override fun getPositionForSection(sectionIndex: Int): Int {
            return sectionIndex
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv: TextView = view.findViewById(R.id.tv)

    }
}
