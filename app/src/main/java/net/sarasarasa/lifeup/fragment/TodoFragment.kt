package net.sarasarasa.lifeup.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_todo.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.AddToDoItemActivity
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.adapters.ToDoItemAdapter
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl

class TodoFragment : Fragment() {

    private val todoService = TodoServiceImpl()
    private val mList: MutableList<TaskModel> = todoService.getTodoList().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ToDoItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_todo, null)
        mRecyclerView = view.findViewById(R.id.rv)
        mAdapter = ToDoItemAdapter(R.layout.item_to_do, mList)
        mAdapter.setHeaderView(getHeaderView())
        mRecyclerView.layoutManager = LinearLayoutManager(view.context)
        mRecyclerView.adapter = mAdapter
        //设置toolbar
        (activity as MainActivity).initToolBar(view.findViewById(R.id.toolbar))

        view.fab.setOnClickListener {
            val intent = Intent(this.context, AddToDoItemActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun refreshDataSet() {
        mList.clear()
        mList.addAll(todoService.getTodoList())
        mAdapter.notifyDataSetChanged()
    }


    private fun getHeaderView(): View {
        var headerView = layoutInflater.inflate(R.layout.head_view_to_do, null)
        //headerView.findViewById<TextView>(R.id.tw_finishCounter).text = "Hhhh"
        return headerView
    }


    override fun onResume() {
        super.onResume()
        Toast.makeText(context, "onResume() 刷新mAdapter", Toast.LENGTH_LONG).show()
        refreshDataSet()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            Toast.makeText(context, "onHiddenChanged", Toast.LENGTH_LONG).show()
            refreshDataSet()
        }
    }




}
