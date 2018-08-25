package net.sarasarasa.lifeup.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_status.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.service.impl.AttributeLevelServiceImpl
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl

class StatusFragment : Fragment() {

    val attributeLevelService = AttributeLevelServiceImpl()
    val attributeService = AttributeServiceImpl()
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
    }


}
