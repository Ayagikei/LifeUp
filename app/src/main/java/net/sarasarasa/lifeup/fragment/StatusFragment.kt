package net.sarasarasa.lifeup.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_lifeup.view.*
import kotlinx.android.synthetic.main.fragment_status.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.service.impl.AttributeLevelServiceImpl
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.StepServiceImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl

class StatusFragment : Fragment() {

    val attributeLevelService = AttributeLevelServiceImpl()
    val attributeService = AttributeServiceImpl()
    val stepService = StepServiceImpl()
    val todoService = TodoServiceImpl()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_status, null)
        //设置toolbar
        (activity as MainActivity).initToolBar(view.findViewById(R.id.toolbar))
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
        }
    }

    /** 设置各项数据 **/
    fun initData(view: View) {
        /** TODO：优化
         **可以每一组内部的view设置为相同的id，仅外部不同，仅传一个外部view参数即获得所有view的方法来优化此方法。
         **/

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

        view.tv_finishCount.text = "到目前为止，你一共完成了${todoService.getFinishCount()}个待办事项！\n继续努力！"

        val arrStep = ArrayList<String>()
        with(arrStep) {
            arrStep.add("0步")
            arrStep.add("2500步")
            arrStep.add("5000步")
            arrStep.add("10000步")
            arrStep.add("20000步")
        }

        view.step_view.setSteps(arrStep)

        val mainActivity = context as MainActivity
        val dailyStepCount = stepService.updateAndGetTodayStepCount(mainActivity.getStep())

        when {
            dailyStepCount in 2500..5000 -> view.step_view.go(1, true)
            dailyStepCount in 5000..10000 -> view.step_view.go(2, true)
            dailyStepCount in 10000..20000 -> view.step_view.go(3, true)
            dailyStepCount >= 20000 -> view.step_view.go(4, true)
        }

        view.tv_step_cnt_num.text = "${dailyStepCount}步"

        if (stepService.isTodayGotReward()) {
            view.btn_get_reward.isEnabled = false
            view.btn_get_reward.text = "已领取"
        } else if (dailyStepCount in 0..2500) {
            view.btn_get_reward.isEnabled = false
            view.btn_get_reward.text = "暂不可领取"
        } else {
            view.btn_get_reward.setOnClickListener {
                getRewardByStep()
                it.isEnabled = false
            }
        }
    }

    private fun getRewardByStep() {
        val exp = stepService.getRewardByStep()
        showDialogLifeUp(exp)
    }

    private fun showDialogLifeUp(exp: Long) {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_lifeup, null)
        dialogView.tv_title.text = "你获得了「力量」属性经验值！"
        dialogView.tv_content.text = " ${exp} 点"
        val dialog = context?.let { AlertDialog.Builder(it).create() }

        with(dialog) {
            this?.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "确定") { _, _ ->
                view?.let { initData(it) }
                dismiss()
            }
            this?.setView(dialogView)
            this?.show()
        }
    }

}
