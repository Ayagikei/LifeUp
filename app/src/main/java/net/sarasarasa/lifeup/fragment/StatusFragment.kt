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

class StatusFragment : Fragment() {

    val attributeLevelService = AttributeLevelServiceImpl()
    val attributeService = AttributeServiceImpl()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_status, null)
        //设置toolbar
        (activity as MainActivity).initToolBar(view.findViewById(R.id.toolbar))
        initData(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        initData(view ?: return)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            initData(view ?: return)
        }
    }

    fun initData(view: View) {
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
    }


}
