package net.sarasarasa.lifeup.fragment

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.fragment_todo.view.*
import kotlinx.android.synthetic.main.item_to_do.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.AddToDoItemActivity
import net.sarasarasa.lifeup.activities.EditToDoItemActivity
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.adapters.ToDoItemAdapter
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl


class TodoFragment : Fragment() {

    private val todoService = TodoServiceImpl()
    private val mList: MutableList<TaskModel> = todoService.getUncompletedTodoList().toMutableList()
    private var dialogView: View? = null
    private var dialog: AlertDialog? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ToDoItemAdapter
    private lateinit var mHeaderView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_todo, null)

        initView(view)
        return view
    }

    private fun initView(view: View) {
        initRecyclerView(view)

        //设置toolbar
        (activity as MainActivity).initToolBar(view.findViewById(R.id.toolbar))

        view.fab.setOnClickListener {
            val intent = Intent(this.context, AddToDoItemActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecyclerView(view: View) {
        mRecyclerView = view.findViewById(R.id.rv)
        mAdapter = ToDoItemAdapter(R.layout.item_to_do, mList)
        mAdapter.setHeaderView(getHeaderView())
        mRecyclerView.layoutManager = LinearLayoutManager(view.context)
        mRecyclerView.adapter = mAdapter

        //设置长按Item的长按事件
        mAdapter.setOnItemLongClickListener { adapter, mView, position ->
            val mPopupMenu = PopupMenu(mView.context, mView.av_checkBtn)
            mPopupMenu.menuInflater.inflate(R.menu.menu_to_do_item, mPopupMenu.menu)
            mPopupMenu.setOnMenuItemClickListener { menuItem ->
                //获得所选item
                val item = adapter.getItem(position) as TaskModel

                when (menuItem.itemId) {
                    R.id.edit_item -> {
                        val intent = Intent(this.context, EditToDoItemActivity::class.java)
                        intent.putExtra("id", item.id)
                        startActivity(intent)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.delete_item -> {
                        context?.let {
                            AlertDialog.Builder(it).setTitle("删除")
                                    .setMessage("你确定要删除该待办事项吗？你会损失一些经验值。")
                                    .setPositiveButton("确定") { _, _ ->
                                        // 点击“确认”后的操作

                                        if (todoService.deleteTodoItem(item.id)) {
                                            Toast.makeText(it, "成功删除待办事项",
                                                    Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(it, "删除操作出现异常",
                                                    Toast.LENGTH_SHORT).show()
                                        }
                                        refreshDataSet()
                                    }
                                    .setNegativeButton("取消") { _, _ ->
                                    }.show()
                        }
                        return@setOnMenuItemClickListener true
                    }
                    R.id.give_up_item -> {
                        context?.let {
                            AlertDialog.Builder(it).setTitle("放弃")
                                    .setMessage("你确定要放弃该待办事项吗？你会损失一些经验值。")
                                    .setPositiveButton("确定") { _, _ ->
                                        // 点击“确认”后的操作
                                        todoService.giveUpTodoItem(item.id)

                                        if (todoService.deleteTodoItem(item.id)) {
                                            Toast.makeText(it, "成功放弃待办事项",
                                                    Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(it, "放弃操作出现异常",
                                                    Toast.LENGTH_SHORT).show()
                                        }
                                        refreshDataSet()
                                    }
                                    .setNegativeButton("取消") { _, _ ->
                                    }.show()
                        }
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener true
                }
            }

            mPopupMenu.show()
            return@setOnItemLongClickListener true
        }

        mAdapter.setOnItemChildClickListener { adapter, mView, position ->
            if (mView is LottieAnimationView) {
                mView.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        showDialogAbbr()
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        showDialogAbbr()
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }
                })

                mView.playAnimation()
                mView.isClickable = false

                val taskModel = adapter.getItem(position) as TaskModel
                todoService.finishTodoItem(taskModel.id)
            }
        }
    }

    private fun showDialogAbbr() {
        if (dialog != null)
            return

        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_abbr, null)
        dialog = context?.let { AlertDialog.Builder(it).create() }

        with(dialog) {
            this?.setTitle("你获得了经验值")
            this?.setIcon(net.sarasarasa.lifeup.R.drawable.ic_award_exp)
            this?.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "确定") { _, _ ->
                dismiss()
                dialog = null
            }
            this?.setView(dialogView)
            this?.show()
        }
    }

    private fun showDialogLifeUp() {
        if (dialog != null)
            return

        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_lifeup, null)
        dialog = context?.let { AlertDialog.Builder(it).create() }

        with(dialog) {
            this?.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "确定") { _, _ ->
                dismiss()
                dialog = null
            }
            this?.setView(dialogView)
            this?.show()
        }
    }

    private fun refreshDataSet() {
        mList.clear()
        mList.addAll(todoService.getUncompletedTodoList())
        mHeaderView.findViewById<TextView>(R.id.tw_finishCounter).text = "今天已经完成1个待办事项（共${mList.size}个）"
        mAdapter.notifyDataSetChanged()
    }


    private fun getHeaderView(): View {
        mHeaderView = layoutInflater.inflate(R.layout.head_view_to_do, null)
        mHeaderView.findViewById<TextView>(R.id.tw_finishCounter).text = "今天已经完成1个待办事项（共${mList.size}个）"
        return mHeaderView
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
