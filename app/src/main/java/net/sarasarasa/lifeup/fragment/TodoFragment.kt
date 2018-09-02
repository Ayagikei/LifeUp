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
import kotlinx.android.synthetic.main.dialog_abbr.view.*
import kotlinx.android.synthetic.main.fragment_todo.view.*
import kotlinx.android.synthetic.main.head_view_to_do.view.*
import kotlinx.android.synthetic.main.item_to_do.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.AddToDoItemActivity
import net.sarasarasa.lifeup.activities.EditToDoItemActivity
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.adapters.ToDoItemAdapter
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.service.impl.AttributeLevelServiceImpl
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.DateUtil
import net.sarasarasa.lifeup.utils.ToastUtils
import java.text.SimpleDateFormat
import java.util.*


class TodoFragment : Fragment() {

    private val todoService = TodoServiceImpl()
    private val attributeService = AttributeServiceImpl()
    private val attributeLevelService = AttributeLevelServiceImpl()
    private val mList: MutableList<TaskModel> = todoService.getUncompletedTodoList().toMutableList()
    private var dialogView: View? = null
    private var dialog: AlertDialog? = null
    private var thread: Thread? = null
    private var threadRunning: Boolean = false
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
        //检查逾期情况
        if (todoService.checkAndUpdateOverdueTask()) {
            val mContext = context
            if (mContext != null)
                ToastUtils.showLongToast(mContext, "你有代办事项逾期了！请前往[历史]查看。")
        }

        mRecyclerView = view.findViewById(R.id.rv)
        mAdapter = ToDoItemAdapter(R.layout.item_to_do, mList)
        mAdapter.setHeaderView(getHeaderView())

        if (mList.size == 0)
            mAdapter.setFooterView(getFootView(), 0)

        //mAdapter.setFooterView(getFootView())
        mRecyclerView.layoutManager = LinearLayoutManager(view.context)
        mRecyclerView.adapter = mAdapter

        //设置长按Item的长按事件
        mAdapter.setOnItemLongClickListener { adapter, mView, position ->
            //获得所选item
            val item = adapter.getItem(position) as TaskModel
            val mPopupMenu = PopupMenu(mView.context, mView.av_checkBtn)
            mPopupMenu.menuInflater.inflate(R.menu.menu_to_do_item, mPopupMenu.menu)
            mPopupMenu.setOnMenuItemClickListener { menuItem ->


                //如果所选Item不是未完成状态，不可长按
                if (item.taskStatus != 0)
                    return@setOnMenuItemClickListener true

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

                                        if (todoService.giveUpTodoItem(item.id)) {
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

            if (item.taskStatus == 0)
                mPopupMenu.show()
            return@setOnItemLongClickListener true
        }

        mAdapter.setOnItemChildClickListener { adapter, mView, position ->
            val item = adapter.getItem(position) as TaskModel
            var isEverShowDialog = false

            if (mView is LottieAnimationView &&
                    item.taskStatus == ToDoItemConstants.UNCOMPLETED) {

                mView.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        if (!isEverShowDialog) {
                            showDialogAbbr(item)
                            isEverShowDialog = true
                        }
                        //refreshHeaderView(mHeaderView)
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        if (!isEverShowDialog) {
                            showDialogAbbr(item)
                            isEverShowDialog = true
                        }
                    }

                    override fun onAnimationStart(p0: Animator?) {

                    }
                })
                mView.playAnimation()

                mView.isClickable = false
                todoService.finishTodoItem(item.id)

                val activity = checkNotNull(context) as MainActivity
                activity.syncData()

                //刷新HeaderView的进度显示
                mList[position].taskStatus = ToDoItemConstants.COMPLETED

            } // end of the if
        }
    }

    private fun showDialogAbbr(item: TaskModel) {

        if (dialog != null || item.relatedAttribute1.isNullOrBlank())
            return

        val newDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_abbr, null)
        val newDialog = context?.let { AlertDialog.Builder(it).create() }
        initDialogViewData(newDialogView, item)

        if (checkNotNull(newDialog?.isShowing)) return


        with(newDialog) {
            this?.setTitle("你获得了经验值")
            this?.setIcon(net.sarasarasa.lifeup.R.drawable.ic_award_exp)
            this?.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "确定") { _, _ ->
                cancel()
            }
            this?.setView(newDialogView)
            this?.setOnShowListener {
                threadRunning = true
                doProgressOrigin(newDialogView, item, 1)
                doProgressOrigin(newDialogView, item, 2)
                doProgressOrigin(newDialogView, item, 3)
            }

            this?.setOnCancelListener {
                threadRunning = false
                thread?.interrupt()
                cancel()

                if (item.taskFrequency != 0)
                    showDialogRepeat(item)
            }
        }

        newDialog?.show()
    }

    private fun showDialogRepeat(taskModel: TaskModel) {
        val dialog = context?.let { AlertDialog.Builder(it).create() }

        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        val calendar = Calendar.getInstance()
        calendar.time = taskModel.taskExpireTime
        if (taskModel.taskFrequency != 30)
            calendar.add(Calendar.DATE, taskModel.taskFrequency)
        else calendar.add(Calendar.MONTH, 1)

        if (dialog != null)
            with(dialog) {
                setTitle("重复设置")
                setMessage("要进行重复吗？\n下一次的期限日期是 ${simpleDateFormat.format(calendar.time)}。")
                setButton(AlertDialog.BUTTON_POSITIVE, "是") { _, _ ->
                    if (taskModel.id != null)
                        todoService.repeatTask(taskModel.id)
                    refreshDataSet()
                    dialog.cancel()
                }
                setButton(AlertDialog.BUTTON_NEGATIVE, "否") { _, _ ->
                    dialog.cancel()
                }

                show()
            }
    }

    private fun initDialogViewData(newDialogView: View, item: TaskModel) {

        //属性现在的总经验值
        var attributeExpBefore = attributeService.getAttributeExpByString(item.relatedAttribute1
                ?: "")
        //等到前等级
        var attributeLevelBefore = attributeLevelService.getAttributeLevelByExp(attributeExpBefore - item.expReward)

        //第一个属性值必定存在
        newDialogView.iv_iconFirst.setImageResource(TodoItemConverter.strAbbrToDrawableId(item.relatedAttribute1))
        newDialogView.tv_nameFirst.text = TodoItemConverter.strAbbrToStrTitle(item.relatedAttribute1)
        newDialogView.tv_levelFirst.text = "LV${attributeLevelBefore.levelNum}"



        if (item.relatedAttribute2.isNullOrBlank()) {
            newDialogView.constraintLayout_sec.visibility = View.GONE

        } else {
            attributeExpBefore = attributeService.getAttributeExpByString(item.relatedAttribute2
                    ?: "")
            attributeLevelBefore = attributeLevelService.getAttributeLevelByExp(attributeExpBefore - item.expReward)

            newDialogView.iv_iconSec.setImageResource(TodoItemConverter.strAbbrToDrawableId(item.relatedAttribute2))
            newDialogView.tv_nameSec.text = TodoItemConverter.strAbbrToStrTitle(item.relatedAttribute2)
            newDialogView.tv_levelSec.text = "LV${attributeLevelBefore.levelNum}"

        }

        if (item.relatedAttribute3.isNullOrBlank()) {
            newDialogView.constraintLayout_thr.visibility = View.GONE
        } else {
            attributeExpBefore = attributeService.getAttributeExpByString(item.relatedAttribute3
                    ?: "")
            attributeLevelBefore = attributeLevelService.getAttributeLevelByExp(attributeExpBefore - item.expReward)

            newDialogView.iv_iconThr.setImageResource(TodoItemConverter.strAbbrToDrawableId(item.relatedAttribute3))
            newDialogView.tv_nameThr.text = TodoItemConverter.strAbbrToStrTitle(item.relatedAttribute3)
            newDialogView.tv_levelThr.text = "LV${attributeLevelBefore.levelNum}"


        }
    }

    private fun doProgressOrigin(dialogView: View, item: TaskModel, index: Int) {
        val relatedAttribute = when (index) {
            1 -> item.relatedAttribute1
            2 -> item.relatedAttribute2
            3 -> item.relatedAttribute3
            else -> return
        }

        if (relatedAttribute.isNullOrBlank()) return

        //完成前的
        val nowExpTotal = when (index) {
            1 -> attributeService.getAttributeExpByString(relatedAttribute ?: "") - item.expReward
            2 -> attributeService.getAttributeExpByString(item.relatedAttribute2
                    ?: "") - item.expReward
            3 -> attributeService.getAttributeExpByString(item.relatedAttribute3
                    ?: "") - item.expReward
            else -> return
        }


        var nowExp = nowExpTotal - attributeLevelService.getAttributeLevelByExp(nowExpTotal).startExpValue
        val levelMaxExp = attributeLevelService.getAttributeLevelByExp(nowExpTotal).endExpValue - attributeLevelService.getAttributeLevelByExp(nowExpTotal).startExpValue

        val progressBar = when (index) {
            1 -> dialogView.npb_first
            2 -> dialogView.npb_Sec
            3 -> dialogView.npb_thr
            else -> return
        }

        progressBar.progress = nowExp * 100 / levelMaxExp
        var finalProgress = (nowExp + item.expReward) * 100 / levelMaxExp


        if (finalProgress >= 100) {
            //要升级的情况
            thread = Thread {
                try {
                    //先走到尾巴
                    val toMax = progressBar.max - progressBar.progress
                    while (threadRunning == true && progressBar.progress != progressBar.max) {
                        activity?.runOnUiThread { progressBar.incrementProgressBy(if (toMax / 20 > 0) toMax / 20 else 1) }
                        Thread.sleep(40)
                    }

                    //升级，进度条重置为0
                    val newLevelModel = attributeLevelService.getAttributeLevelByExp(nowExpTotal + item.expReward)
                    val textViewLevel = when (index) {
                        1 -> dialogView.tv_levelFirst
                        2 -> dialogView.tv_levelSec
                        3 -> dialogView.tv_expThr
                        else -> return@Thread
                    }
                    activity?.runOnUiThread {
                        progressBar.progress = 0
                        textViewLevel.text = "LV${newLevelModel.levelNum}"
                    }

                    nowExp = nowExpTotal + item.expReward - newLevelModel.startExpValue
                    val nextMaxExpTotal = newLevelModel.endExpValue
                    val nextMaxExp = newLevelModel.endExpValue - newLevelModel.startExpValue
                    finalProgress = nowExp * 100 / nextMaxExp
                    Thread.sleep(40)

                    while (threadRunning == true && progressBar.progress != finalProgress) {
                        activity?.runOnUiThread { progressBar.incrementProgressBy(if (finalProgress / 30 > 0) finalProgress / 30 else 1) }
                        Thread.sleep(40)
                    }

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            thread?.start()
        } else {
            //不需要升级
            thread = Thread {
                try {
                    var progressToGo = finalProgress - progressBar.progress

                    while (threadRunning == true && progressBar.progress != finalProgress) {
                        activity?.runOnUiThread { progressBar.incrementProgressBy(if (progressToGo / 30 > 0) progressToGo / 30 else 1) }
                        Thread.sleep(40)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            thread?.start()
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
        //检查并更新逾期情况
        if (todoService.checkAndUpdateOverdueTask()) {
            val mContext = context
            if (mContext != null)
                ToastUtils.showLongToast(mContext, "你有代办事项逾期了！请前往[历史]查看。")
        }

        mList.clear()
        mList.addAll(todoService.getUncompletedTodoList())
        refreshHeaderView(mHeaderView)

        if (mList.size == 0) {
            mAdapter.setFooterView(getFootView())
        } else mAdapter.removeAllFooterView()

        mAdapter.notifyDataSetChanged()
    }

    private fun refreshHeaderView(view: View): View {
        val finishCnt = todoService.getTodayFinishCount()
        val taskCnt = todoService.getTodayTaskCount()

        view.findViewById<TextView>(R.id.tw_finishCounter).text = "今天已经完成${finishCnt}个待办事项（共${taskCnt}个）"
        if (taskCnt == 0) {
            view.pgb_lifeLevel.progress = 0
        } else {
            view.pgb_lifeLevel.progress = finishCnt * 100 / taskCnt
        }

        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("MM月dd日", Locale.getDefault())
        val strDate = simpleDateFormat.format(calendar.time) + DateUtil.getWeekOfDate(calendar.timeInMillis)
        view.findViewById<TextView>(R.id.tv_headerText).text = strDate
        mAdapter.notifyDataSetChanged()
        return view
    }

    private fun getHeaderView(): View {
        mHeaderView = layoutInflater.inflate(R.layout.head_view_to_do, null)
        refreshHeaderView(mHeaderView)

        return mHeaderView
    }

    private fun getFootView(): View {
        return layoutInflater.inflate(R.layout.foot_view_to_do, null)
    }

    override fun onResume() {
        super.onResume()
        refreshDataSet()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            refreshDataSet()
        }
    }


}
