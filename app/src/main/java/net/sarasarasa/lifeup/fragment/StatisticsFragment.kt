package net.sarasarasa.lifeup.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.android.synthetic.main.fragment_statistics.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.StepServiceImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.DateUtil


class StatisticsFragment : Fragment() {

    val todoServiceImpl = TodoServiceImpl()
    val attributeService = AttributeServiceImpl()
    val stepService = StepServiceImpl()
    val todoService = TodoServiceImpl()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, null)
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
    private fun initData(view: View) {
        val lineEntries = ArrayList<Entry>()
        val countList = todoServiceImpl.listFinishTaskCountPastDays(7)
        for ((i, e) in countList.withIndex()) {
            val entry = Entry(i.toFloat(), e.toFloat())
            lineEntries.add(entry)
        }
        val lineDataSet = LineDataSet(lineEntries, "")
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        lineDataSet.color = resources.getColor(R.color.blue)
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 1.5f
        val lineData = LineData(lineDataSet)
        lineData.setDrawValues(false)
        val xAxis = view.line_chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.granularity = 1f
        val stringDateList = DateUtil.listStringDatePastDays(7)
        xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
            stringDateList[value.toInt()]
        }
        val yAxis = view.line_chart.axisLeft
        yAxis.granularity = 1f
        yAxis.axisMinimum = 0f
        view.line_chart.legend.isEnabled = false
        view.line_chart.setTouchEnabled(false)
        view.line_chart.axisRight.isEnabled = false
        view.line_chart.data = lineData
        view.line_chart.description.isEnabled = false
        view.line_chart.setDrawGridBackground(false)
        view.line_chart.invalidate()

        val barEntries = ArrayList<BarEntry>()
        val stepList = stepService.listFinishTaskCountPastDays(7)
        for ((i, e) in stepList.withIndex()) {
            val entry = BarEntry(i.toFloat(), e.toFloat())
            barEntries.add(entry)
        }
        val barDataSet = BarDataSet(barEntries, "")
        barDataSet.color = resources.getColor(R.color.color_bar_chart)
        barDataSet.setDrawValues(true)
        val barData = BarData(barDataSet)
        val xAxisOfBarData = view.bar_chart.xAxis
        xAxisOfBarData.valueFormatter = IAxisValueFormatter { value, axis ->
            stringDateList[value.toInt()]
        }
        xAxisOfBarData.position = XAxis.XAxisPosition.BOTTOM
        //xAxisOfBarData.setDrawAxisLine(false)
        xAxisOfBarData.setDrawGridLines(false)
        val yAxisOfBarData = view.bar_chart.axisLeft
        yAxisOfBarData.axisMinimum = 0f
        yAxisOfBarData.setDrawGridLines(false)
        view.bar_chart.legend.isEnabled = false
        view.bar_chart.data = barData
        view.bar_chart.description.isEnabled = false
        view.bar_chart.setDrawGridBackground(false)
        view.bar_chart.axisRight.isEnabled = false
        view.bar_chart.animateY(1000, Easing.Linear)
        view.bar_chart.animateX(1000, Easing.Linear)
        view.bar_chart.invalidate()

        val pieEntries = ArrayList<PieEntry>()
        val pieColors = ArrayList<Int>()
        addPieEntry(pieEntries, pieColors)
        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.sliceSpace = 3.0f
        pieDataSet.colors = pieColors
        pieDataSet.valueTextSize = 9.0f
        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextColor(resources.getColor(R.color.white))
        view.chart.data = pieData
        view.chart.setUsePercentValues(true)
        view.chart.setEntryLabelTextSize(10.0f)
        view.chart.centerText = "经验分布"
        view.chart.description.isEnabled = false
        view.chart.invalidate()
    }

    private fun addPieEntry(pieEntries: ArrayList<PieEntry>, colors: ArrayList<Int>) {
        val totalExp = attributeService.getTotalAttrExp()

        if (attributeService.getAttribute().strengthAttribute != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().strengthAttribute.toFloat() / totalExp, "力量"))
            colors.add(resources.getColor(R.color.color_abbr_strength))
        }
        if (attributeService.getAttribute().knowledgeAttribute != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().knowledgeAttribute.toFloat() / totalExp, "学识"))
            colors.add(resources.getColor(R.color.color_abbr_learning))
        }

        if (attributeService.getAttribute().charmAttribute != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().charmAttribute.toFloat() / totalExp, "魅力"))
            colors.add(resources.getColor(R.color.color_abbr_charm))
        }
        if (attributeService.getAttribute().enduranceAttribute != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().enduranceAttribute.toFloat() / totalExp, "耐力"))
            colors.add(resources.getColor(R.color.color_abbr_endurance))
        }

        if (attributeService.getAttribute().energyAttribute != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().energyAttribute.toFloat() / totalExp, "活力"))
            colors.add(resources.getColor(R.color.color_abbr_vitality))
        }
        if (attributeService.getAttribute().creativity != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().creativity.toFloat() / totalExp, "创造"))
            colors.add(resources.getColor(R.color.color_abbr_creative))
        }
    }

}
