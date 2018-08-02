package net.sarasarasa.lifeup.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.ToDoItemAdapter
import net.sarasarasa.lifeup.datas.ToDo
import java.util.*

class TodoFragment : Fragment() {

    internal lateinit var mRecyclerView: RecyclerView
    internal lateinit var mAdapter: ToDoItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_todo, null)
        mRecyclerView = view.findViewById(R.id.rv)
        mAdapter = ToDoItemAdapter(R.layout.item_to_do, genData())
        mAdapter.setHeaderView(getHeaderView())
        mRecyclerView.layoutManager = LinearLayoutManager(view.context)
        mRecyclerView.adapter = mAdapter

        return view;
    }

    private fun genData(): List<ToDo> {
        val list = ArrayList<ToDo>()
        val random = Random()
        for (i in 0..9) {
            val name = random.nextInt(10).toString()
            val todo = ToDo(name, "content", false)
            list.add(todo)
        }
        return list
    }

    private fun getHeaderView(): View {
        var headerView = layoutInflater.inflate(R.layout.head_view_to_do, null)
        headerView.findViewById<TextView>(R.id.tw_finishCounter).text = "Hhhh"
        return headerView
    }


}
