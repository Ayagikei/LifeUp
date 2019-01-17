package net.sarasarasa.lifeup.fragment

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import android.widget.Toast
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout
import com.airbnb.lottie.LottieAnimationView
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import kotlinx.android.synthetic.main.dialog_abbr.view.*
import kotlinx.android.synthetic.main.dialog_activity.view.*
import kotlinx.android.synthetic.main.dialog_sort.view.*
import kotlinx.android.synthetic.main.fragment_todo.view.*
import kotlinx.android.synthetic.main.head_view_to_do.view.*
import kotlinx.android.synthetic.main.item_to_do.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.AddToDoItemActivity
import net.sarasarasa.lifeup.activities.EditToDoItemActivity
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.adapters.ToDoItemAdapter
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_FINISH_TEAM_TASK
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.IS_TEAM_TASK
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.dao.TaskTargetDAO
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.network.impl.UploadNetworkImpl
import net.sarasarasa.lifeup.service.impl.AchievementServiceImpl
import net.sarasarasa.lifeup.service.impl.AttributeLevelServiceImpl
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.DateUtil
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils
import net.sarasarasa.lifeup.vo.ActivityVO
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


class TodoFragment : Fragment() , EasyPermissions.PermissionCallbacks , BGASortableNinePhotoLayout.Delegate {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
                this.context?.let { ToastUtils.showShortToast("请重新登录") }
            }
            MSG_FINISH_TEAM_TASK -> {
                //团队事项完成
                refreshDataSet()
                this.context?.let { ToastUtils.showShortToast("成功完成事项") }
            }
            else -> {
                refreshDataSet()

                if (msg.obj != null)
                    this.context?.let { ToastUtils.showShortToast(msg.obj.toString()) }
            }

        }

        return@Callback true
    }

    private val uploadNetworkImpl = UploadNetworkImpl(uiHandler)
    private val teamNetworkImpl = TeamNetworkImpl(uiHandler)
    private val todoService = TodoServiceImpl()
    private val taskTargetDAO = TaskTargetDAO()
    private val attributeService = AttributeServiceImpl()
    private val attributeLevelService = AttributeLevelServiceImpl()
    private val achievementService = AchievementServiceImpl()
    private val mList: MutableList<TaskModel> = todoService.getUncompletedTodoList(true).toMutableList()
    private var dialogView: View? = null
    private var dialog: AlertDialog? = null
    private var thread: Thread? = null
    private var threadRunning: Boolean = false
    private val optionSharedPreferences = LifeUpApplication.getLifeUpApplication().getSharedPreferences("options", Context.MODE_PRIVATE)
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ToDoItemAdapter
    private lateinit var mHeaderView: View
    private lateinit var rootView: View
    private var toolbar: ActionBar? = null

    private var mPhotosSnpl:BGASortableNinePhotoLayout? = null

    companion object {
        private const val PRC_PHOTO_PICKER = 1
        private const val RC_CHOOSE_PHOTO = 1
        private const val RC_PHOTO_PREVIEW = 2
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_todo, null)

        setHasOptionsMenu(true)
        initView(view)
        rootView = view
        return view
    }

    private fun initView(view: View) {
        initRecyclerView(view)
        //设置toolbar
        (activity as MainActivity).initToolBar(view.findViewById(R.id.toolbar))

        when (optionSharedPreferences.getString("classBy", "all")) {
            "all" -> (activity as MainActivity).supportActionBar?.title = "待办：所有"
            "today" -> (activity as MainActivity).supportActionBar?.title = "待办：今天"
            "week" -> (activity as MainActivity).supportActionBar?.title = "待办：近七天"
        }
        toolbar = (activity as MainActivity).supportActionBar

        view.fab.setOnClickListener {
            val intent = Intent(this.context, AddToDoItemActivity::class.java)
            startActivity(intent)
        }

        val sharedPreferences = LifeUpApplication.getLifeUpApplication().getSharedPreferences("status", Context.MODE_PRIVATE)
        val isShowGuide = sharedPreferences.getBoolean("isShowGuide", false)

        if (!isShowGuide) {
            TapTargetSequence(activity)
                    .targets(TapTarget.forView(view.fab, "新建待办事项", "点击这里可以新建待办事项！")
                            .outerCircleColor(R.color.blue)
                            .outerCircleAlpha(0.96f)
                            .titleTextSize(20)
                            .titleTextColor(R.color.white)
                            .descriptionTextSize(12)
                            .descriptionTextColor(R.color.white)
                            .textColor(R.color.white)
                            .drawShadow(true)
                            .cancelable(false)
                            .tintTarget(false)
                            .transparentTarget(false)
                            .targetRadius(60),
                            TapTarget.forView((activity as MainActivity).findViewById(R.id.navigation), "导航栏", "告示板：如同RPG游戏中接受任务和完成任务的地方\n" +
                                    "状态：查看你的各项属性的等级。\n\n" +
                                    "社区：加入社区建立的团队，领取公共事项（需要登录）\n\n" +
                                    "统计：查看你的各项数据统计：事项完成情况、经验值获取情况、经验值分布、步数统计等。\n\n\n" +
                                    "除了社区以外的功能都支持离线使用哦~\n" +
                                    "目前大部分数据保存在本地，谨慎进行清除数据、卸载等操作。")
                                    .outerCircleColor(R.color.blue)
                                    .outerCircleAlpha(0.96f)
                                    .titleTextSize(20)
                                    .titleTextColor(R.color.white)
                                    .descriptionTextSize(12)
                                    .descriptionTextColor(R.color.white)
                                    .textColor(R.color.white)
                                    .drawShadow(true)
                                    .cancelable(false)
                                    .tintTarget(false)
                                    .transparentTarget(false)
                                    .targetRadius(60),
                            TapTarget.forToolbarNavigationIcon(view.toolbar, "侧边栏", "可以进行登录，查看成就、历史、设置等功能").id(1)
                    )
                    .start()

            val editor = sharedPreferences.edit()
            editor.putBoolean("isShowGuide", true)
            editor.apply()
        }

    }

    private fun initRecyclerView(view: View) {
        //检查逾期情况
        if (todoService.checkAndUpdateOverdueTask()) {
                ToastUtils.showLongToast("你有代办事项逾期了！请前往[历史]查看。")
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

            //本地事项可以撤销
            if (item.taskStatus != 0 && item.teamId == -1L) {
                showDialogReset(item)
                return@setOnItemLongClickListener true
            }


            mPopupMenu.menuInflater.inflate(R.menu.menu_to_do_item, mPopupMenu.menu)
            mPopupMenu.setOnMenuItemClickListener { menuItem ->

                //如果所选Item不是未完成状态或是团队事项，不可长按
                //|| item.teamId != IS_TEAM_TASK


                when (menuItem.itemId) {
                    R.id.top_item -> {
                        item.id?.let { todoService.changePriority(it) }
                        refreshDataSet()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.edit_item -> {
                        if (item.teamId != IS_TEAM_TASK) {
                            ToastUtils.showShortToast("团队事项不可编辑！")
                        } else {
                            val intent = Intent(this.context, EditToDoItemActivity::class.java)
                            intent.putExtra("id", item.id)
                            startActivity(intent)
                        }
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
                                        activity?.applicationContext?.let { it1 -> WidgetUtils.updateWidgets(it1) }
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
            val cal = Calendar.getInstance()

            if (cal.timeInMillis < item.startTime.time) {
                context?.let {
                    ToastUtils.showShortToast("该待办事项尚未到开始时间！")
                }
                return@setOnItemChildClickListener
            }

            if (item.teamId != IS_TEAM_TASK && cal.timeInMillis > item.endTime.time) {
                context?.let {
                    ToastUtils.showShortToast("该待办事项已经过了签到时间段！")
                }
                return@setOnItemChildClickListener
            }


            if (mView is LottieAnimationView &&
                    item.taskStatus == ToDoItemConstants.UNCOMPLETED) {

                mView.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {

                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        if (!isEverShowDialog) {
                            showDialogAbbr(item)
                            isEverShowDialog = true
                            refreshHeaderView(mHeaderView)
                            mView.progress = 1.0f
                            mView.isClickable = false
                        }

                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        if (!isEverShowDialog) {
                            showDialogAbbr(item)
                            isEverShowDialog = true
                            mView.progress = 1.0f
                            mView.isClickable = false
                        }
                    }

                    override fun onAnimationStart(p0: Animator?) {

                    }
                })
                mView.playAnimation()
                mView.isClickable = false

                //如果不是团队事项，这里就可以处理业务逻辑
                if (item.teamId == IS_TEAM_TASK) {
                    todoService.finishTodoItem(item.id)
                    item.taskStatus = ToDoItemConstants.COMPLETED
                }

                val activity = checkNotNull(context) as MainActivity
                activity.syncData()

                //刷新HeaderView的进度显示
                mList[position].taskStatus = ToDoItemConstants.COMPLETED

                rootView.post {
                    achievementService.checkAchievement(rootView.achievement_view, WeakReference(activity))
                }

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

                //本地事项才显示重复对话框
                if (item.taskFrequency != 0 && item.teamId == IS_TEAM_TASK) {

                    var needToRepeat = true
                    if (item.taskTargetId != null) {
                        val taskTarget = taskTargetDAO.getTaskTargetById(item.taskTargetId!!)
                        if (taskTarget != null) {
                            if (taskTarget.targetTimes == item.currentTimes) {
                                needToRepeat = false
                                ToastUtils.showShortToast("你完成了设定的目标次数！")
                            }
                        }
                    }

                    if (needToRepeat) {
                        val isShowRepeatDialog = optionSharedPreferences?.getBoolean("isShowRepeatDialog", true)
                        if (isShowRepeatDialog == true)
                            showDialogRepeat(item)
                        else {
                            todoService.repeatTask(item.id)
                            refreshDataSet()
                        }
                    }
                }

                //非本地事项显示动态对话框
                if (item.teamId != IS_TEAM_TASK) {
                    val isIgnoreActivitySubmitDialog = optionSharedPreferences?.getBoolean("isIgnoreActivitySubmitDialog", false)

                    // 检测设置里有没有勾选“默认不发表团队动态”
                    if (isIgnoreActivitySubmitDialog == false)
                        showDialogActivity(item)
                    else teamNetworkImpl.finishTeamTask(item, ActivityVO())
                }
            }
        }

        newDialog?.show()
    }

    private fun showDialogRepeat(taskModel: TaskModel) {
        val dialog = context?.let { AlertDialog.Builder(it).create() }

        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        val calendar = Calendar.getInstance()

        if (taskModel.taskFrequency != -1) {
            calendar.time = taskModel.taskExpireTime
            if (taskModel.taskFrequency != 30)
                calendar.add(Calendar.DATE, taskModel.taskFrequency)
            else calendar.add(Calendar.MONTH, 1)
        }

        if (dialog != null)
            with(dialog) {
                setTitle("重复设置")

                if (taskModel.taskFrequency != -1)
                    setMessage("要进行重复吗？\n下一次的期限日期是 ${simpleDateFormat.format(calendar.time)}。")
                else
                    setMessage("要进行重复吗？")

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

    private fun showDialogReset(taskModel: TaskModel) {
        val dialog = context?.let { AlertDialog.Builder(it).create() }

        if (dialog != null)
            with(dialog) {
                setTitle("撤销")
                setMessage("你确定要撤销完成吗？")
                setButton(AlertDialog.BUTTON_POSITIVE, "是") { _, _ ->
                    if (taskModel.id != null)
                        todoService.undoFinishTodoItem(taskModel.id)
                    ToastUtils.showShortToast("撤销成功")
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
        var attributeLevelBefore = when (item.teamId) {
            IS_TEAM_TASK -> attributeLevelService.getAttributeLevelByExp(attributeExpBefore - item.expReward)
            else -> attributeLevelService.getAttributeLevelByExp(attributeExpBefore)
        }

        //第一个属性值必定存在
        newDialogView.iv_iconFirst.setImageResource(TodoItemConverter.strAbbrToDrawableId(item.relatedAttribute1))
        newDialogView.tv_nameFirst.text = TodoItemConverter.strAbbrToStrTitle(item.relatedAttribute1)
        newDialogView.tv_levelFirst.text = "LV${attributeLevelBefore.levelNum}"



        if (item.relatedAttribute2.isNullOrBlank()) {
            newDialogView.constraintLayout_sec.visibility = View.GONE

        } else {
            attributeExpBefore = attributeService.getAttributeExpByString(item.relatedAttribute2
                    ?: "")
            attributeLevelBefore = when (item.teamId) {
                IS_TEAM_TASK -> attributeLevelService.getAttributeLevelByExp(attributeExpBefore - item.expReward)
                else -> attributeLevelService.getAttributeLevelByExp(attributeExpBefore)
            }

            newDialogView.iv_iconSec.setImageResource(TodoItemConverter.strAbbrToDrawableId(item.relatedAttribute2))
            newDialogView.tv_nameSec.text = TodoItemConverter.strAbbrToStrTitle(item.relatedAttribute2)
            newDialogView.tv_levelSec.text = "LV${attributeLevelBefore.levelNum}"

        }

        if (item.relatedAttribute3.isNullOrBlank()) {
            newDialogView.constraintLayout_thr.visibility = View.GONE
        } else {
            attributeExpBefore = attributeService.getAttributeExpByString(item.relatedAttribute3
                    ?: "")
            attributeLevelBefore = when (item.teamId) {
                IS_TEAM_TASK -> attributeLevelService.getAttributeLevelByExp(attributeExpBefore - item.expReward)
                else -> attributeLevelService.getAttributeLevelByExp(attributeExpBefore)
            }

            newDialogView.iv_iconThr.setImageResource(TodoItemConverter.strAbbrToDrawableId(item.relatedAttribute3))
            newDialogView.tv_nameThr.text = TodoItemConverter.strAbbrToStrTitle(item.relatedAttribute3)
            newDialogView.tv_levelThr.text = "LV${attributeLevelBefore.levelNum}"


        }

        if (item.completeReward.isNullOrEmpty()) {
            newDialogView.constraintLayout_treasure.visibility = View.GONE
        } else {
            newDialogView.tv_treasure.text = item.completeReward
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
        var nowExpTotal = when (index) {
            1 -> attributeService.getAttributeExpByString(relatedAttribute ?: "") - item.expReward
            2 -> attributeService.getAttributeExpByString(item.relatedAttribute2
                    ?: "") - item.expReward
            3 -> attributeService.getAttributeExpByString(item.relatedAttribute3
                    ?: "") - item.expReward
            else -> return
        }

        if (item.teamId != IS_TEAM_TASK)
            nowExpTotal += item.expReward


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



    private fun showDialogActivity(taskModel: TaskModel) {
        // val editText = EditText(context)
        val activityVO = ActivityVO()
        val builder = context?.let { AlertDialog.Builder(it) }
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_activity, null)
        val title = "动态"
        mPhotosSnpl = dialogView.snpl_moment_add_photos
        mPhotosSnpl!!.setDelegate(this)
        mPhotosSnpl!!.setOnClickListener { choicePhotoWrapper() }
        mPhotosSnpl!!.maxItemCount = 3

        if (builder != null)
            with(builder) {
                setTitle(title)

                setView(dialogView)

                setPositiveButton("发表") { _, _ ->
                    //发表动态请求
                    activityVO.activity = dialogView.editText.text.toString()
                    LoadingDialogUtils.show(context)

                    if(mPhotosSnpl!!.data.isEmpty())
                    teamNetworkImpl.finishTeamTask(taskModel, activityVO)
                    else {
                        uploadNetworkImpl.uploadImages(mPhotosSnpl!!.data,taskModel,activityVO)
                    }

                    mPhotosSnpl = null
                }
                setNeutralButton("不发表") { _, _ ->
                    teamNetworkImpl.finishTeamTask(taskModel, activityVO)
                    mPhotosSnpl = null
                }
                setNegativeButton("取消") { _, _ ->
                }

                show()
            }
    }

    private fun refreshDataSet() {
        //检查并更新逾期情况
        if (todoService.checkAndUpdateOverdueTask()) {
                ToastUtils.showLongToast("你有代办事项逾期了！请前往[历史]查看。")
        }

        mList.clear()
        mList.addAll(todoService.getUncompletedTodoList(false))
        refreshHeaderView(mHeaderView)

        if (mList.size == 0) {
            mAdapter.setFooterView(getFootView())
        } else mAdapter.removeAllFooterView()

        mAdapter.notifyDataSetChanged()

        rootView.post {
            achievementService.checkAchievement(rootView.achievement_view, WeakReference<Activity>(activity))
        }

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

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PRC_PHOTO_PICKER) {
            ToastUtils.showShortToast("您拒绝了「图片选择」所需要的相关权限!")
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onClickNinePhotoItem(sortableNinePhotoLayout: BGASortableNinePhotoLayout, view: View, position: Int, model: String, models: ArrayList<String>) {
        if(mPhotosSnpl == null) return

        val photoPickerPreviewIntent = BGAPhotoPickerPreviewActivity.IntentBuilder(activity)
                .previewPhotos(models) // 当前预览的图片路径集合
                .selectedPhotos(models) // 当前已选中的图片路径集合
                .maxChooseCount(mPhotosSnpl!!.maxItemCount) // 图片选择张数的最大值
                .currentPosition(position) // 当前预览图片的索引
                .isFromTakePhoto(false) // 是否是拍完照后跳转过来
                .build()
        startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW)
    }

    override fun onClickAddNinePhotoItem(sortableNinePhotoLayout: BGASortableNinePhotoLayout, view: View, position: Int, models: ArrayList<String>) {
        choicePhotoWrapper()
    }

    override fun onNinePhotoItemExchanged(sortableNinePhotoLayout: BGASortableNinePhotoLayout, fromPosition: Int, toPosition: Int, models: ArrayList<String>) {
    }

    override fun onClickDeleteNinePhotoItem(sortableNinePhotoLayout: BGASortableNinePhotoLayout, view: View, position: Int, model: String, models: ArrayList<String>) {
        mPhotosSnpl?.removeItem(position)
    }

    @AfterPermissionGranted(PRC_PHOTO_PICKER)
    private fun choicePhotoWrapper() {
        if(mPhotosSnpl == null) return

        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        if (context?.let { EasyPermissions.hasPermissions(it, *perms) } == true) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            val takePhotoDir = File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto")

            val photoPickerIntent = BGAPhotoPickerActivity.IntentBuilder(activity)
                    .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .maxChooseCount(3) // 图片选择张数的最大值 mPhotosSnpl!!.maxItemCount - mPhotosSnpl!!.itemCount
                    .selectedPhotos(mPhotosSnpl!!.data) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build()
            startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO)
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", PRC_PHOTO_PICKER, *perms)
        }
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

         if(mPhotosSnpl != null)
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            mPhotosSnpl!!.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data!!))
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            mPhotosSnpl!!.data = BGAPhotoPickerPreviewActivity.getSelectedPhotos(data!!)
        }
     }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("ApplySharedPref")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_sort -> {
                val bottomSheetDialog = context?.let { BottomSheetDialog(it) }
                val view = layoutInflater.inflate(R.layout.dialog_sort, null)
                view.ll_sort_start_time.setOnClickListener {
                    val editor = optionSharedPreferences?.edit()
                    editor?.putString("sortBy", "startTime")
                    editor?.commit()
                    refreshDataSet()
                    bottomSheetDialog?.cancel()
                }
                view.ll_sort_deadline.setOnClickListener {
                    val editor = optionSharedPreferences?.edit()
                    editor?.putString("sortBy", "deadline")
                    editor?.commit()
                    refreshDataSet()
                    bottomSheetDialog?.cancel()
                }
                view.ll_sort_create_time.setOnClickListener {
                    val editor = optionSharedPreferences?.edit()
                    editor?.putString("sortBy", "createTime")
                    editor?.commit()
                    refreshDataSet()
                    bottomSheetDialog?.cancel()
                }
                view.ll_sort_exp.setOnClickListener {
                    val editor = optionSharedPreferences?.edit()
                    editor?.putString("sortBy", "exp")
                    editor?.commit()
                    refreshDataSet()
                    bottomSheetDialog?.cancel()
                }
                bottomSheetDialog?.setContentView(view)
                bottomSheetDialog?.show()
                return true
            }
            R.id.action_all -> {
                val editor = optionSharedPreferences?.edit()
                editor?.putString("classBy", "all")
                editor?.commit()
                refreshDataSet()
                if (toolbar is ActionBar)
                    toolbar!!.title = "待办：所有"
                return true
            }
            R.id.action_today -> {
                val editor = optionSharedPreferences?.edit()
                editor?.putString("classBy", "today")
                editor?.commit()
                refreshDataSet()
                if (toolbar is ActionBar)
                    toolbar!!.title = "待办：今天"
                return true
            }
            R.id.action_week -> {
                val editor = optionSharedPreferences?.edit()
                editor?.putString("classBy", "week")
                editor?.commit()
                refreshDataSet()
                if (toolbar is ActionBar)
                    toolbar!!.title = "待办：近七天"
                return true
            }
            R.id.action_sort_asc_change -> {
                val isAsc = optionSharedPreferences?.getBoolean("isAsc", true)
                val editor = optionSharedPreferences?.edit()
                editor?.putBoolean("isAsc", !isAsc!!)
                editor?.commit()
                refreshDataSet()

                if (!isAsc!!)
                    ToastUtils.showShortToast("已经更改为正序")
                else
                    ToastUtils.showShortToast("已经更改为倒序")
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


}
