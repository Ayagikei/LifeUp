package net.sarasarasa.lifeup.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
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
import net.sarasarasa.lifeup.activities.ExpActivity
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.StepServiceImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.DateUtil


class StatisticsFragment : Fragment() {

    private val todoServiceImpl = TodoServiceImpl()
    private val attributeService = AttributeServiceImpl()
    private val stepService = StepServiceImpl()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, null)
        //设置toolbar
        (activity as MainActivity).initToolBar(view.findViewById(R.id.toolbar))
        view.findViewById<Toolbar>(R.id.toolbar).title = "统计"

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
        initTaskLineChart(view)
        initExpLineChart(view)
        initStepBarChart(view)
        initExpPieChart(view)

        view.tv_exp_line_chart_btn.setOnClickListener {
            val intent = Intent(context, ExpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initTaskLineChart(view: View) {
        val lineEntries = ArrayList<Entry>()
        val countList = todoServiceImpl.listFinishTaskCountPastDays(7)
        for ((i, e) in countList.withIndex()) {
            val entry = Entry(i.toFloat(), e.toFloat())
            lineEntries.add(entry)
        }
        val lineDataSet = LineDataSet(lineEntries, "")
        lineDataSet.apply {
            setDrawCircleHole(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            context?.let { color = (ContextCompat.getColor(it, R.color.blue)) }
            setDrawCircles(false)
            lineWidth = 1.5f
        }

        val lineData = LineData(lineDataSet)
        lineData.setDrawValues(false)
        val xAxis = view.line_chart_task.xAxis
        val stringDateList = DateUtil.listStringDatePastDays(7)
        xAxis.apply {
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            axisMinimum = 0f
            granularity = 1f
            xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
                stringDateList[value.toInt()]
            }
        }

        val yAxis = view.line_chart_task.axisLeft
        yAxis.apply {
            granularity = 1f
            axisMinimum = 0f
        }
        view.line_chart_task.apply {
            legend.isEnabled = false
            setTouchEnabled(false)
            axisRight.isEnabled = false
            data = lineData
            description.isEnabled = false
            setDrawGridBackground(false)
            setNoDataText("暂时没有相应数据")
            animateY(1000, Easing.Linear)
            animateX(1000, Easing.Linear)
            invalidate()
        }

    }

    private fun initExpLineChart(view: View) {
        val lineEntries = ArrayList<Entry>()
        val countList = attributeService.listDailyTotalExpPastDays(7)
        for ((i, e) in countList.withIndex()) {
            val entry = Entry(i.toFloat(), e.toFloat())
            lineEntries.add(entry)
        }
        val lineDataSet = LineDataSet(lineEntries, "")
        lineDataSet.apply {
            setDrawCircleHole(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            context?.let { color = (ContextCompat.getColor(it, R.color.blue)) }
            setDrawCircles(false)
            lineWidth = 1.5f
        }

        val lineData = LineData(lineDataSet)
        lineData.setDrawValues(false)
        val xAxis = view.line_chart_exp.xAxis
        xAxis.apply {
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            axisMinimum = 0f
            granularity = 1f
        }
        val stringDateList = DateUtil.listStringDatePastDays(7)
        xAxis.valueFormatter = IAxisValueFormatter { value, _ ->
            stringDateList[value.toInt()]
        }
        val yAxis = view.line_chart_exp.axisLeft
        yAxis.granularity = 1f

        view.line_chart_exp.apply {
            legend.isEnabled = false
            setTouchEnabled(false)
            axisRight.isEnabled = false
            data = lineData
            description.isEnabled = false
            setDrawGridBackground(false)
            setNoDataText("暂时没有相应数据")
            animateY(1000, Easing.Linear)
            animateX(1000, Easing.Linear)
            invalidate()
        }

    }

    private fun initStepBarChart(view: View) {
        val barEntries = ArrayList<BarEntry>()
        val stepList = stepService.listFinishTaskCountPastDays(7)
        for ((i, e) in stepList.withIndex()) {
            val entry = BarEntry(i.toFloat(), e.toFloat())
            barEntries.add(entry)
        }
        val barDataSet = BarDataSet(barEntries, "")
        context?.let { barDataSet.color = (ContextCompat.getColor(it, R.color.color_bar_chart)) }
        barDataSet.setDrawValues(true)
        val barData = BarData(barDataSet)
        val xAxisOfBarData = view.bar_chart_step.xAxis
        val stringDateList = DateUtil.listStringDatePastDays(7)
        xAxisOfBarData.apply {
            valueFormatter = IAxisValueFormatter { value, axis ->
                stringDateList[value.toInt()]
            }
            position = XAxis.XAxisPosition.BOTTOM
            //xAxisOfBarData.setDrawAxisLine(false)
            setDrawGridLines(false)
        }
        val yAxisOfBarData = view.bar_chart_step.axisLeft
        yAxisOfBarData.apply {
            axisMinimum = 0f
            setDrawGridLines(false)
        }

        view.bar_chart_step.apply {
            legend.isEnabled = false
            data = barData
            description.isEnabled = false
            setDrawGridBackground(false)
            axisRight.isEnabled = false
            animateY(1000, Easing.Linear)
            animateX(1000, Easing.Linear)
            setNoDataText("暂时没有相应数据")
            invalidate()
        }
    }

    private fun initExpPieChart(view: View) {
        val pieEntries = ArrayList<PieEntry>()
        val pieColors = ArrayList<Int>()
        addPieEntry(pieEntries, pieColors)
        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.apply {
            sliceSpace = 3.0f
            colors = pieColors
            valueTextSize = 9.0f
        }

        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(PercentFormatter())
        context?.let { pieData.setValueTextColor(ContextCompat.getColor(it, R.color.white)) }
        view.pie_chart_exp.apply {
            data = pieData
            setUsePercentValues(true)
            setEntryLabelTextSize(10.0f)
            centerText = "经验分布"
            description.isEnabled = false
            setNoDataText("暂时没有相应数据")
            animateY(1000, Easing.Linear)
            animateX(1000, Easing.Linear)
            invalidate()
        }
    }

    private fun addPieEntry(pieEntries: ArrayList<PieEntry>, colors: ArrayList<Int>) {
        val totalExp = attributeService.getTotalAttrExp()

        if (attributeService.getAttribute().strengthAttribute != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().strengthAttribute.toFloat() / totalExp, "力量"))
            context?.let { colors.add(ContextCompat.getColor(it, R.color.color_abbr_strength)) }
        }
        if (attributeService.getAttribute().knowledgeAttribute != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().knowledgeAttribute.toFloat() / totalExp, "学识"))
            context?.let { colors.add(ContextCompat.getColor(it, R.color.color_abbr_learning)) }
        }

        if (attributeService.getAttribute().charmAttribute != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().charmAttribute.toFloat() / totalExp, "魅力"))
            context?.let { colors.add(ContextCompat.getColor(it, R.color.color_abbr_charm)) }
        }
        if (attributeService.getAttribute().enduranceAttribute != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().enduranceAttribute.toFloat() / totalExp, "耐力"))
            context?.let { colors.add(ContextCompat.getColor(it, R.color.color_abbr_endurance)) }
        }

        if (attributeService.getAttribute().energyAttribute != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().energyAttribute.toFloat() / totalExp, "活力"))
            context?.let { colors.add(ContextCompat.getColor(it, R.color.color_abbr_vitality)) }
        }
        if (attributeService.getAttribute().creativity != 0) {
            pieEntries.add(PieEntry(attributeService.getAttribute().creativity.toFloat() / totalExp, "创造"))
            context?.let { colors.add(ContextCompat.getColor(it, R.color.color_abbr_creative)) }
        }
    }

}
