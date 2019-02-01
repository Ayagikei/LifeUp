package net.sarasarasa.lifeup.activities

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.foot_view_to_do.view.*
import kotlinx.android.synthetic.main.item_finished_to_do.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.HistoryAdapter
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils


class HistoryActivity : AppCompatActivity() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
                ToastUtils.showShortToast(getString(R.string.network_invalid_token))
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
    private val mList: MutableList<TaskModel> = todoService.getCompletedTodoList(100, 0).toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: HistoryAdapter
    private var currentOffset = 0
    private var maxOffset = todoService.countCompletedTodoList() - 100

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
        mAdapter.emptyView = getEmptyView()

        mAdapter.setOnLoadMoreListener({
            getNewList()
        }, mRecyclerView)

        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as TaskModel

            when (view.id) {
                R.id.btn_undo -> {
                    if (item.taskStatus == ToDoItemConstants.COMPLETED) {
                        todoService.undoFinishTodoItem(item.id)
                        ToastUtils.showShortToast(getString(R.string.history_reset_success))
                        mAdapter.remove(position)
                    } else if (item.taskStatus == ToDoItemConstants.OUT_OF_DATE) {
                        if (item.teamId == -1L) {
                            item.id?.let { todoService.restartTask(it) }
                            view.visibility = View.INVISIBLE
                        } else {
                            teamNetworkImpl.getNextTeamTask(item.teamId)
                            view.visibility = View.INVISIBLE
                        }
                    }
                    WidgetUtils.updateWidgets(applicationContext)
                }
                R.id.tv_btn -> {
                    val mPopupMenu = PopupMenu(view.context, view.tv_btn)
                    mPopupMenu.menuInflater.inflate(R.menu.menu_history_item, mPopupMenu.menu)

                    if (item.taskStatus != ToDoItemConstants.OUT_OF_DATE) {
                        mPopupMenu.menu.removeItem(R.id.finish_item)
                    }

                    mPopupMenu.setOnMenuItemClickListener { menuItem ->

                        when (menuItem.itemId) {
                            R.id.delete_item -> {
                                item.id?.let {
                                    if (todoService.hideHistoryItem(it) == 1)
                                        ToastUtils.showShortToast(getString(R.string.history_delete_success))
                                }
                                mAdapter.remove(position)
                                //mAdapter.notifyItemRemoved(position)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.finish_item -> {
                                item.id?.let {
                                    if (todoService.setOverdueItemToFinish(it)) {
                                        ToastUtils.showShortToast(getString(R.string.history_set_to_success_success))
                                        item.taskStatus = 1
                                        mAdapter.notifyItemChanged(position)
                                    }
                                }
                                return@setOnMenuItemClickListener true
                            }
                            else -> true
                        }
                    }
                    mPopupMenu.show()
                }
            }
        }
    }

    private fun refreshDataSet() {
        mList.clear()
        currentOffset = 0
        mList.addAll(todoService.getCompletedTodoList(100, currentOffset))
        mAdapter.notifyDataSetChanged()
    }


    private fun getEmptyView(): View {
        val view = layoutInflater.inflate(R.layout.foot_view_to_do, null)
        view.textView11.text = getString(R.string.history_empty)
        return view
    }

    private fun getNewList() {
        currentOffset += 100
        mAdapter.addData(todoService.getCompletedTodoList(100, currentOffset))

        if (currentOffset >= maxOffset || currentOffset < 0) {
            mAdapter.loadMoreEnd()
        } else {
            mAdapter.loadMoreComplete()
            mAdapter.setEnableLoadMore(true)
        }

        mAdapter.notifyDataSetChanged()
    }

}
