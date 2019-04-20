package net.sarasarasa.lifeup.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import kotlinx.android.synthetic.main.dialog_input_sport_data.view.*
import kotlinx.android.synthetic.main.dialog_lifeup.view.*
import kotlinx.android.synthetic.main.fragment_status.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.service.impl.AttributeLevelServiceImpl
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.StepServiceImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import java.lang.ref.WeakReference

class StatusFragment : Fragment() {

    val attributeLevelService = AttributeLevelServiceImpl()
    val attributeService = AttributeServiceImpl()
    val stepService = StepServiceImpl()
    val todoService = TodoServiceImpl()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_status, null)
        //设置toolbar
        (activity as MainActivity).initToolBar(WeakReference(view.findViewById(R.id.toolbar)))
        view.findViewById<Toolbar>(R.id.toolbar).title = getString(R.string.status_toolbar_title)

        val sharedPreferences = LifeUpApplication.getLifeUpApplication().getSharedPreferences("options", Context.MODE_PRIVATE)
        val isStatusPlayAnimation = sharedPreferences.getBoolean("isStatusPlayAnimation", false)
        view.waveView.setAnim(isStatusPlayAnimation)

        val isHidePedometer = sharedPreferences.getBoolean("isHidePedometer", false)
        if (isHidePedometer) view.sportCardView.visibility = View.GONE

        initData(view)
        return view
    }

    /** 刷新数据 **/
    override fun onResume() {
        super.onResume()
        initData(view ?: return)
    }

    /** 刷新数据 **/
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            initData(view ?: return)
            activity?.invalidateOptionsMenu()
        }
    }

    /** 设置各项数据 **/
    private fun initData(view: View) {

        val attribute = attributeService.getAttribute()
        var exp = attribute.strengthAttribute
        var levelModel = attributeLevelService.getAttributeLevelByExp(exp)

        view.tw_strengthExp.text = "${exp - levelModel.startExpValue}/${levelModel.endExpValue - levelModel.startExpValue}"
        view.tw_strengthLevel.text = "LV${levelModel.levelNum}"
        view.npb_strength.progress = (exp - levelModel.startExpValue) * 100 / (levelModel.endExpValue - levelModel.startExpValue)

        exp = attribute.knowledgeAttribute
        levelModel = attributeLevelService.getAttributeLevelByExp(exp)
        view.tw_learningExp.text = "${exp - levelModel.startExpValue}/${levelModel.endExpValue - levelModel.startExpValue}"
        view.tv_levelFirst.text = "LV${levelModel.levelNum}"
        view.npb_first.progress = (exp - levelModel.startExpValue) * 100 / (levelModel.endExpValue - levelModel.startExpValue)

        exp = attribute.charmAttribute
        levelModel = attributeLevelService.getAttributeLevelByExp(exp)
        view.tw_charmExp.text = "${exp - levelModel.startExpValue}/${levelModel.endExpValue - levelModel.startExpValue}"
        view.tw_charmLevel.text = "LV${levelModel.levelNum}"
        view.npb_charm.progress = (exp - levelModel.startExpValue) * 100 / (levelModel.endExpValue - levelModel.startExpValue)

        exp = attribute.enduranceAttribute
        levelModel = attributeLevelService.getAttributeLevelByExp(exp)
        view.tw_enduranceExp.text = "${exp - levelModel.startExpValue}/${levelModel.endExpValue - levelModel.startExpValue}"
        view.tw_enduranceLevel.text = "LV${levelModel.levelNum}"
        view.npb_endurance.progress = (exp - levelModel.startExpValue) * 100 / (levelModel.endExpValue - levelModel.startExpValue)

        exp = attribute.energyAttribute
        levelModel = attributeLevelService.getAttributeLevelByExp(exp)
        view.tw_vitalityExp.text = "${exp - levelModel.startExpValue}/${levelModel.endExpValue - levelModel.startExpValue}"
        view.tw_vitalityLevel.text = "LV${levelModel.levelNum}"
        view.npb_vitality.progress = (exp - levelModel.startExpValue) * 100 / (levelModel.endExpValue - levelModel.startExpValue)

        exp = attribute.creativity
        levelModel = attributeLevelService.getAttributeLevelByExp(exp)
        view.tw_creativeExp.text = "${exp - levelModel.startExpValue}/${levelModel.endExpValue - levelModel.startExpValue}"
        view.tw_creativeLevel.text = "LV${levelModel.levelNum}"
        view.npb_creative.progress = (exp - levelModel.startExpValue) * 100 / (levelModel.endExpValue - levelModel.startExpValue)

        exp = attribute.gradeAttribute
        levelModel = attributeLevelService.getAttributeLevelByExp(exp)
        view.tv_lifeExp.text = "${exp - levelModel.startExpValue}/${levelModel.endExpValue - levelModel.startExpValue}"
        view.tv_lifeLevel.text = "LV${levelModel.levelNum}"
        view.pgb_lifeLevel.progress = (exp - levelModel.startExpValue) * 100 / (levelModel.endExpValue - levelModel.startExpValue)

        view.tv_finishCount.text = getString(R.string.status_finish_fore) + todoService.getFinishCount() + getString(R.string.status_finish_back)

        val arrStep = ArrayList<String>()
        with(arrStep) {
            add("0步")
            add("2500步")
            add("5000步")
            add("10000步")
            add("20000步")
        }

        view.step_view.setSteps(arrStep)

        val mainActivity = context as MainActivity
        val dailyStepCount = stepService.updateAndGetTodayStepCount(mainActivity.getStep())

        if (!stepService.isTodayGotReward()) {
            view.tv_input_sport_data.visibility = View.VISIBLE
            view.tv_input_sport_data.isClickable = true
            view.tv_input_sport_data.setOnClickListener {
                showDialogInputSportData(view)
            }

        } else view.tv_input_sport_data.visibility = View.INVISIBLE

        when {
            dailyStepCount in 2500..5000 -> view.step_view.go(1, true)
            dailyStepCount in 5000..10000 -> view.step_view.go(2, true)
            dailyStepCount in 10000..20000 -> view.step_view.go(3, true)
            dailyStepCount >= 20000 -> view.step_view.go(4, true)
        }

        view.tv_step_cnt_num.text = dailyStepCount.toString() + getString(R.string.step)

        if (!mainActivity.getPedometerIsAvailable()) {
            view.tv_step_cnt_desc.text = getString(R.string.pedometer_not_available)
        }

        when {
            stepService.isTodayGotReward() -> {
                view.btn_get_reward.isEnabled = false
                view.btn_get_reward.text = getString(R.string.team_task_got)
            }
            dailyStepCount in 0..2500 -> {
                view.btn_get_reward.isEnabled = false
                view.btn_get_reward.text = getString(R.string.team_task_can_not_get)
            }
            else -> {
                view.btn_get_reward.setOnClickListener {
                    getRewardByStep()
                    it.isEnabled = false
                }
                view.btn_get_reward.text = getString(R.string.team_task_get)
                view.btn_get_reward.isEnabled = true
            }
        }
    }

    private fun getRewardByStep() {
        val exp = stepService.getRewardByStep()
        showDialogLifeUp(exp)
    }

    private fun showDialogLifeUp(exp: Long) {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_lifeup, null)
        dialogView.tv_title.text = getString(R.string.gain_str_exp)
        dialogView.tv_content.text = " ${exp} ${getString(R.string.point)}"

        context?.let { context ->
            MaterialDialog(context).show {
                customView(view = dialogView)
                positiveButton(R.string.btn_yes) { _ ->
                    view?.let { initData(it) }
                }
                lifecycleOwner(this@StatusFragment)
            }
        }

    }

    private fun showDialogInputSportData(view: View) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_input_sport_data, null)

        context?.let { context ->
            MaterialDialog(context).show {
                title(text = getString(R.string.status_input_steps))
                customView(view = dialogView)
                positiveButton(R.string.btn_yes) { _ ->
                    val step = dialogView.til_sport_data.editText?.text.toString().toLongOrNull()
                    if (step != null) {
                        if (stepService.userInputTodayStepData(step)) {
                            initData(view)
                            ToastUtils.showShortToast(getString(R.string.status_input_steps_success))
                        } else ToastUtils.showShortToast(getString(R.string.status_input_steps_failed))
                    }
                }
                negativeButton(R.string.btn_cancel)
                lifecycleOwner(this@StatusFragment)
            }
        }

    }


}
