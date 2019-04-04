package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import kotlinx.android.synthetic.main.activity_to_do_item_detail.*
import kotlinx.android.synthetic.main.content_to_do_item_detail.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.COMPLETED
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.GIVE_UP
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.OUT_OF_DATE
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.UNCOMPLETED
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.dao.TaskTargetDAO
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils
import java.text.SimpleDateFormat
import java.util.*

class ToDoItemDetailActivity : AppCompatActivity() {

    private var id: Long = 0
    private val todoService = TodoServiceImpl()
    private var taskModel: TaskModel? = null
    private val taskTargetDAO = TaskTargetDAO()
    private val timeFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_item_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        id = intent.getLongExtra("id", -1)

        if (id != -1L) {
            taskModel = todoService.getATodoItem(id)
            if (taskModel != null)
                initStatus()
            else finish()
        } else {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        initStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        LoadingDialogUtils.dismissAndClearReference()
    }

    private fun initStatus() {
        tv_category.text = todoService.getCategoryNameById(taskModel?.categoryId ?: 0L)
        tv_content.text = taskModel?.content

        if (!taskModel?.remark.isNullOrEmpty())
            tv_remark.text = taskModel?.remark
        else {
            iv_remark.visibility = View.GONE
            tv_remark.visibility = View.GONE
        }

        tv_status.text = when (taskModel?.taskStatus) {
            UNCOMPLETED -> "事项状态：尚未完成"
            COMPLETED -> "事项状态：已完成（${timeFormatter.format(taskModel?.endDate)}）"
            OUT_OF_DATE -> "事项状态：逾期（${timeFormatter.format(taskModel?.endDate)}）"
            GIVE_UP -> "事项状态：放弃（${timeFormatter.format(taskModel?.endDate)}）"
            else -> ""
        }

        // 设置事项类型并且实现跳转团队页面
        val ssbType = SpannableStringBuilder("事项类型：")
        ssbType.append(if (taskModel?.teamId == -1L) "本地事项"
        else "团队事项")

        if (taskModel?.teamId != -1L) {
            val clickableSpanType = object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }

                override fun onClick(view: View) {
                    val intent = Intent(this@ToDoItemDetailActivity, TeamActivity::class.java)
                    intent.putExtra("teamId", taskModel?.teamId)
                    startActivity(intent)
                }
            }
            ssbType.setSpan(clickableSpanType, ssbType.indexOf("：") + 1, ssbType.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val foregroundColorSpanType = ForegroundColorSpan(applicationContext.resources.getColor(R.color.colorPrimary))
            ssbType.setSpan(foregroundColorSpanType, ssbType.indexOf("：") + 1, ssbType.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        tv_type.movementMethod = LinkMovementMethod.getInstance()
        tv_type.text = ssbType


        if (taskModel?.taskRemindTime != null) {
            tv_alarm.text = "提醒定于 ${timeFormatter.format(taskModel?.taskRemindTime)}"
        } else {
            iv_alarm.visibility = View.GONE
            tv_alarm.visibility = View.GONE
        }

        if (getEndTime() == null) {
            tv_date.text = "开始于 ${timeFormatter.format(taskModel?.startTime)}"
        } else {
            tv_date.text = "可完成于\n${timeFormatter.format(taskModel?.startTime)} - ${timeFormatter.format(getEndTime())}"
        }

        tv_repeat.text = getRepeatText()

        if (taskModel?.taskTargetId != null && taskModel?.taskFrequency != 0) {
            val taskTarget = taskTargetDAO.getTaskTargetById(taskModel?.taskTargetId!!)

            if (taskTarget != null && taskTarget.targetTimes != 0) {
                val currentTimes = if (taskModel?.taskStatus == 1) taskModel?.currentTimes else taskModel?.currentTimes?.minus(1)
                tv_target.text = "目标次数已完成 ${currentTimes}/${taskTarget.targetTimes}"
            } else {
                iv_target.visibility = View.GONE
                tv_target.visibility = View.GONE
            }
        } else {
            iv_target.visibility = View.GONE
            tv_target.visibility = View.GONE
        }

        if (!taskModel?.completeReward.isNullOrEmpty())
            tv_reward.text = "完成奖励：${taskModel?.completeReward}"
        else {
            iv_reward.visibility = View.GONE
            tv_reward.visibility = View.GONE
        }

        if (taskModel?.teamId == -1L)
            tv_degree.text = "紧迫程度 LV${taskModel?.taskUrgencyDegree}   困难程度 LV${taskModel?.taskDifficultyDegree}"
        else {
            iv_degree.visibility = View.GONE
            tv_degree.visibility = View.GONE
        }

        tv_exp.text = "完成奖励${getAttributeString()}${taskModel?.expReward}点"

    }

    private fun getAttributeString(): String {
        val stringBuffer = StringBuffer("「${TodoItemConverter.strAbbrToStrTitle(taskModel?.relatedAttribute1)}」")
        if (!taskModel?.relatedAttribute2.isNullOrEmpty()) {
            stringBuffer.append("、")
                    .append("「${TodoItemConverter.strAbbrToStrTitle(taskModel?.relatedAttribute2)}」")
        }
        if (!taskModel?.relatedAttribute3.isNullOrEmpty()) {
            stringBuffer.append("、")
                    .append("「${TodoItemConverter.strAbbrToStrTitle(taskModel?.relatedAttribute3)}」")
        }
        return stringBuffer.toString()
    }

    private fun getEndTime(): Date? {
        if (taskModel?.teamId != -1L)
            return taskModel?.endTime
        else {
            return taskModel?.taskExpireTime
        }
    }

    private fun getRepeatText(): String {
        return if (taskModel?.enableEbbinghausMode == true && taskModel?.taskFrequency == 0) "艾宾浩斯记忆法-最后一天"
        else if (taskModel?.enableEbbinghausMode == true) "艾宾浩斯记忆法-${taskModel?.taskFrequency}天"
        else if (taskModel?.taskFrequency == 1 && taskModel?.isIgnoreDayOfWeek?.contains(1) == true) {
            TodoItemConverter.iFrequencyWithIgnoreToNormalString(taskModel?.isIgnoreDayOfWeek!!.toIntArray())
        } else TodoItemConverter.iFrequencyToNormalString(taskModel?.taskFrequency)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_to_do_item_detail, menu)

        if (taskModel?.taskStatus != 0) {
            menu.removeItem(R.id.action_edit)
            menu.removeItem(R.id.action_give_up)
        }


        if (taskModel?.taskStatus == 0 || taskModel?.taskStatus == 1) {
            menu.removeItem(R.id.action_set_to_finish)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                if (taskModel?.taskStatus == UNCOMPLETED) {
                    MaterialDialog(this).show {
                        title(text = "删除")
                        message(text = "你确定要删除该待办事项吗？")
                        positiveButton(R.string.btn_yes) {
                            if (todoService.deleteTodoItem(taskModel?.id)) {
                                ToastUtils.showShortToast("删除成功")
                                finish()
                            } else ToastUtils.showShortToast("删除操作出现异常")
                        }
                        negativeButton(R.string.btn_cancel)
                        lifecycleOwner(this@ToDoItemDetailActivity)
                    }
                } else {
                    MaterialDialog(this).show {
                        title(text = "删除")
                        message(text = "你确定要删除该事项的历史记录吗？")
                        positiveButton(R.string.btn_yes) {
                            if (taskModel?.id?.let { it1 -> todoService.hideHistoryItem(it1) } == 1) {
                                ToastUtils.showShortToast("删除历史记录成功")
                                finish()
                            } else ToastUtils.showShortToast("删除历史记录操作出现异常")
                        }
                        negativeButton(R.string.btn_cancel)
                        lifecycleOwner(this@ToDoItemDetailActivity)
                    }
                }
                return true
            }
            R.id.action_give_up -> {
                if (taskModel?.teamId != -1L) {
                    ToastUtils.showShortToast("团队事项暂不可放弃！")
                }
                if (taskModel?.taskStatus == UNCOMPLETED)
                    MaterialDialog(this).show {
                        title(text = "放弃")
                        message(text = "你确定要放弃该待办事项吗？\n如果是重复周期事项的话，只会放弃这一次的事项，不会影响到事项的重复。")
                        positiveButton(R.string.btn_yes) { if (giveUpItem()) finish() }
                        negativeButton(R.string.btn_cancel)
                        lifecycleOwner(this@ToDoItemDetailActivity)
                    }
                return true
            }
            R.id.action_edit -> {
                if (taskModel?.teamId != -1L) {
                    ToastUtils.showShortToast("团队事项不可编辑！")
                } else {
                    val intent = Intent(this, EditToDoItemActivity::class.java)
                    intent.putExtra("id", taskModel?.id)
                    startActivity(intent)
                }
                return true
            }
            R.id.action_set_to_finish -> {
                if (taskModel?.taskStatus != UNCOMPLETED)
                    MaterialDialog(this).show {
                        title(text = "设为「已经完成」")
                        message(text = "你实际上已经完成了该待办事项吗？")
                        positiveButton(R.string.btn_yes) {
                            if (todoService.setOverdueItemToFinish(taskModel?.id)) {
                                ToastUtils.showShortToast(getString(R.string.history_set_to_success_success))
                            }
                        }
                        negativeButton(R.string.btn_cancel)
                        lifecycleOwner(this@ToDoItemDetailActivity)
                    }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun giveUpItem(): Boolean {
        return if (todoService.giveUpTodoItem(taskModel?.id)) {
                ToastUtils.showShortToast("成功放弃待办事项")
                // 放弃事项不再中断重复事项（所以要进行下一次重复）
                if (taskModel?.taskFrequency != 0) todoService.repeatTask(taskModel?.id)
                WidgetUtils.updateWidgets(LifeUpApplication.getLifeUpApplication())
            true
            } else {
                ToastUtils.showShortToast("放弃操作出现异常")
            false
            }
    }

}