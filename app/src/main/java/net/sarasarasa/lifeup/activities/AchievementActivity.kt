package net.sarasarasa.lifeup.activities

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import kotlinx.android.synthetic.main.activity_achievement.*
import kotlinx.android.synthetic.main.dialog_achievement.view.*
import kotlinx.android.synthetic.main.item_achivement_without_star.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.models.AchievementModel
import net.sarasarasa.lifeup.service.impl.AchievementServiceImpl
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import java.util.*


class AchievementActivity : AppCompatActivity() {
    private val achievementService = AchievementServiceImpl()
    private val attributeService = AttributeServiceImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initData()
    }

    private fun initData() {
        grid_layout.rowCount = 3
        grid_layout.columnCount = 3

        addView(0, R.drawable.ic_achieve_start, 1, 1)
        addView(1, R.drawable.ic_achieve_busy, 2, 1)
        addView(2, R.drawable.ic_achieve_step, 3, 1)
        addView(3, R.drawable.ic_achieve_book, 1, 2)
        addView(4, R.drawable.ic_achieve_team, 2, 2)
        addView(5, R.drawable.ic_achieve_team_task, 3, 2)
        addView(6, R.drawable.ic_achieve_sport, 1, 3)
    }

    private fun addView(id: Int, resId: Int, col: Int, row: Int) {
        val cm = ColorMatrix()
        cm.setSaturation(0f) // 设置饱和度
        val grayColorFilter = ColorMatrixColorFilter(cm)

        val view = View.inflate(this, R.layout.item_achivement_without_star, null)
        val rowSpec = GridLayout.spec(row, 1, 1.0f)
        val columnSpec = GridLayout.spec(col, 1, 1.0f)
        val params = GridLayout.LayoutParams(rowSpec, columnSpec)
        view.layoutParams = params

        view.iv_icon.setImageResource(resId)
        val achievement00 = achievementService.getAchievementById(id)
        view.tv_title.text = achievement00.title
        if (!achievement00.isGotReward)
            view.iv_icon.colorFilter = grayColorFilter
        view.iv_icon.setOnClickListener {
            showDialogAchievement(achievement00, view)
        }
        grid_layout.addView(view, params)
    }


    private fun showDialogAchievement(achievement: AchievementModel, view: View) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_achievement, null)
        dialogView.tv_achieve_title.text = achievement.title
        dialogView.tv_achieve_desc.text = achievement.desc

        when (achievement.achievementId) {
            0 -> dialogView.imageView.setImageResource(R.drawable.ic_achieve_start)
            1 -> dialogView.imageView.setImageResource(R.drawable.ic_achieve_busy)
            2 -> dialogView.imageView.setImageResource(R.drawable.ic_achieve_step)
            3 -> dialogView.imageView.setImageResource(R.drawable.ic_achieve_book)
            4 -> dialogView.imageView.setImageResource(R.drawable.ic_achieve_team)
            5 -> dialogView.imageView.setImageResource(R.drawable.ic_achieve_team_task)
            6 -> dialogView.imageView.setImageResource(R.drawable.ic_achieve_sport)
        }

        val dbAchievement = achievementService.getAchievementById(achievement.achievementId)

        if (!dbAchievement.isGotReward && dbAchievement.hasFinished)
            dialogView.btn_reward.setOnClickListener {
                achievementService.finishAchievement(achievement.achievementId)
                it.visibility = View.GONE
                view.iv_icon.colorFilter = null

                when (achievement.achievementId) {
                    0 -> {
                        val attrs = ArrayList<String>(Arrays.asList("vitality"))
                        attributeService.increaseMultiExp(attrs, 200, "完成成就「${achievement.title}」")
                        ToastUtils.showShortToast("你获得了200点「活力」经验值！")
                    }
                    1 -> {
                        val attrs = ArrayList<String>(Arrays.asList("endurance"))
                        attributeService.increaseMultiExp(attrs, 2000, "完成成就「${achievement.title}」")
                        ToastUtils.showShortToast("你获得了2000点「耐力」经验值！")
                    }
                    2 -> {
                        val attrs = ArrayList<String>(Arrays.asList("strength", "endurance"))
                        attributeService.increaseMultiExp(attrs, 2000, "完成成就「${achievement.title}」")
                        ToastUtils.showShortToast("你获得了2000点「力量」、「耐力」经验值！")
                    }
                    3 -> {
                        val attrs = ArrayList<String>(Arrays.asList("vitality", "endurance"))
                        attributeService.increaseMultiExp(attrs, 5000, "完成成就「${achievement.title}」")
                        ToastUtils.showShortToast("你获得了5000点「活力」、「耐力」经验值！")
                    }
                    4 -> {
                        val attrs = ArrayList<String>(Arrays.asList("charm"))
                        attributeService.increaseMultiExp(attrs, 500, "完成成就「${achievement.title}」")
                        ToastUtils.showShortToast("你获得了5000点「魅力」经验值！")
                    }
                    5 -> {
                        val attrs = ArrayList<String>(Arrays.asList("charm"))
                        attributeService.increaseMultiExp(attrs, 10000, "完成成就「${achievement.title}」")
                        ToastUtils.showShortToast("你获得了10000点「魅力」经验值！")
                    }
                    6 -> {
                        val attrs = ArrayList<String>(Arrays.asList("strength"))
                        attributeService.increaseMultiExp(attrs, 3000, "完成成就「${achievement.title}」")
                        ToastUtils.showShortToast("你获得了3000点「力量」经验值！")
                    }
                }


            }
        else dialogView.btn_reward.visibility = View.GONE

        val dialog = this.let { AlertDialog.Builder(it).create() }
        with(dialog) {
            this?.setView(dialogView)
            this?.show()
        }
    }


}
