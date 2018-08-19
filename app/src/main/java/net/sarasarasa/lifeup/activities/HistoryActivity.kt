package net.sarasarasa.lifeup.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_history.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.HistoryAdapter
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl


class HistoryActivity : AppCompatActivity() {

    private val todoService = TodoServiceImpl()
    private val mList: MutableList<TaskModel> = todoService.getCompletedTodoList().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = rv
        mAdapter = HistoryAdapter(R.layout.item_finished_to_do, mList)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter
    }

}
