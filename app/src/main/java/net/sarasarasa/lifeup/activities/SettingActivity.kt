package net.sarasarasa.lifeup.activities

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_setting.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils


class SettingActivity : AppCompatActivity() {

    val userService = UserServiceImpl()
    val todoService = TodoServiceImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setSupportActionBar(setting_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

/*        var svReopenSplash = findViewById<SettingView>(R.id.setting_item_reopenSplash)
        svReopenSplash.setOnItemViewClick {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }*/

        val sharedPreferences = getSharedPreferences("options", Context.MODE_PRIVATE)
        val isShowRepeatDialog = sharedPreferences.getBoolean("isShowRepeatDialog", true)
        val isWidgetDarkTheme = sharedPreferences.getBoolean("isWidgetDarkTheme", false)
        val isHideNotBegunItem = sharedPreferences.getBoolean("isHideNotBegunItem", false)
        val isStatusPlayAnimation = sharedPreferences.getBoolean("isStatusPlayAnimation", false)
        val editor = sharedPreferences.edit()


        switch_default_repeat.isChecked = !isShowRepeatDialog
        switch_default_repeat.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isShowRepeatDialog", !isChecked)
            editor.apply()
        }

        switch_widget_dark_theme.isChecked = isWidgetDarkTheme
        switch_widget_dark_theme.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isWidgetDarkTheme", isChecked)
            editor.apply()
            WidgetUtils.updateWidgets(applicationContext)
        }

        switch_hide_item.isChecked = isHideNotBegunItem
        switch_hide_item.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isHideNotBegunItem", isChecked)
            editor.apply()
            WidgetUtils.updateWidgets(applicationContext)
        }

        switch_status_play_animation.isChecked = isStatusPlayAnimation
        switch_status_play_animation.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isStatusPlayAnimation", isChecked)
            editor.apply()
        }


        setting_item_logout.setOnItemViewClick {
            //清空token和非本地事项
            userService.saveToken("")
            todoService.deleteTeamTask()

            ToastUtils.showShortToast("退出成功")
        }

/*        setting_item_account.setOnItemViewClick {
            ToastUtils.showShortToast("此功能暂不可用！")
        }*/

    }
}
