package net.sarasarasa.lifeup.activities

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.foot_view_to_do.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.HistoryAdapter
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils


class HistoryActivity : AppCompatActivity() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
                ToastUtils.showShortToast("授权失效，请重试")
            }
            else -> {
                if (msg.obj != null)
                    ToastUtils.showShortToast(msg.obj.toString())
            }

        }

        return@Callback true
    }

    private val todoService = TodoServiceImpl()
    private val teamNetworkImpl = TeamNetworkImpl(uiHandler)
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

        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as TaskModel

            when (view.id) {
                R.id.btn_undo -> {
                    if (item.taskStatus == ToDoItemConstants.COMPLETED) {
                        todoService.undoFinishTodoItem(item.id)
                        ToastUtils.showShortToast("撤销成功")
                        refreshDataSet()
                    } else if (item.taskStatus == ToDoItemConstants.OUT_OF_DATE) {
                        if (item.teamId == -1L) {
                            item.id?.let { todoService.restartTask(it) }
                            view.visibility = View.INVISIBLE
                        } else {
                            teamNetworkImpl.getNextTeamTask(item.teamId)
                            view.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
        mAdapter.emptyView = getEmptyView()
    }

    private fun refreshDataSet() {
        mList.clear()
        mList.addAll(todoService.getCompletedTodoList())
        mAdapter.notifyDataSetChanged()
    }


    private fun getEmptyView(): View {
        val view = layoutInflater.inflate(R.layout.foot_view_to_do, null)
        view.textView11.text = "没有已经完成的待办事项，添加一些吧"
        return view
    }

}
