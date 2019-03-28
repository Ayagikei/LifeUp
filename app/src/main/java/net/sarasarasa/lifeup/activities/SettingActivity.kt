package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_setting.view.*
import kotlinx.android.synthetic.main.activity_setting_display.view.*
import kotlinx.android.synthetic.main.activity_setting_task.view.*
import kotlinx.android.synthetic.main.activity_setting_widget.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.SharedPreferencesUtils
import net.sarasarasa.lifeup.utils.ToastUtils


class SettingActivity : AppCompatActivity() {

    val userService = UserServiceImpl()
    val todoService = TodoServiceImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_wrapper)

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            transaction = supportFragmentManager.beginTransaction()
            transaction?.add(R.id.fragment_container, SettingMainFragment())
            transaction?.commit()
        }



/*        var svReopenSplash = findViewById<SettingView>(R.id.setting_item_reopenSplash)
        svReopenSplash.setOnItemViewClick {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }*/

        val sharedPreferences = getSharedPreferences("options", Context.MODE_PRIVATE)
        val isShowRepeatDialog = sharedPreferences.getBoolean("isShowRepeatDialog", false)
        val isIgnoreActivitySubmitDialog = sharedPreferences.getBoolean("isIgnoreActivitySubmitDialog", true)
        val isDefaultRemake = sharedPreferences.getBoolean("isDefaultRemake", true)
        val isWidgetDarkTheme = sharedPreferences.getBoolean("isWidgetDarkTheme", false)
        val isWidgetDarkThemeWhiteIconAndFonts = sharedPreferences.getBoolean("isWidgetDarkThemeWhiteIconAndFonts", false)
        val isHideNotBegunItem = sharedPreferences.getBoolean("isHideNotBegunItem", false)
        val isStatusPlayAnimation = sharedPreferences.getBoolean("isStatusPlayAnimation", false)
        val isHideCommunity = sharedPreferences.getBoolean("isHideCommunity", false)
        val isHidePedometer = sharedPreferences.getBoolean("isHidePedometer", false)
        val editor = sharedPreferences.edit()



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

        switch_hide_community.isChecked = isHideCommunity
        switch_hide_community.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isHideCommunity", isChecked)
            editor.apply()
        }

        switch_hide_pedometer.isChecked = isHidePedometer
        switch_hide_pedometer.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isHidePedometer", isChecked)
            editor.apply()
        }

        switch_widget_dark_theme_white_icon_and_fonts.isChecked = isWidgetDarkThemeWhiteIconAndFonts
        switch_widget_dark_theme_white_icon_and_fonts.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isWidgetDarkThemeWhiteIconAndFonts", isChecked)
            editor.apply()
            WidgetUtils.updateWidgets(applicationContext)
        }

        setting_item_backup_restore.setOnItemViewClick {
            val intent = Intent(this, BackupActivity::class.java)
            startActivity(intent)
        }

        setting_item_guide.setOnItemViewClick {
            val statusSharedPreferences = getSharedPreferences("status", Context.MODE_PRIVATE)
            val statusEditor = statusSharedPreferences.edit()
            statusEditor.putBoolean("isShowGuide", false)
            statusEditor.apply()
            ToastUtils.showShortToast(getString(R.string.setting_reopen_guide))
        }
/*        setting_item_change_widget_font_color.setOnItemViewClick {
            val mOnColorPickerListener = object : OnColorPickerListener {
                override fun onColorCancel(dialog: ColorPickerDialog) {//取消选择的颜色

                }

                override fun onColorChange(dialog: ColorPickerDialog, color: Int) {//实时监听颜色变化

                }

                override fun onColorConfirm(dialog: ColorPickerDialog, color: Int) {//确定的颜色


                    val hexColor = "#" + Integer.toHexString(color)
                    ToastUtils.showShortToast(hexColor.toString())

                    try {
                        tv_widget.setTextColor(Color.parseColor(hexColor))
                    }catch (e:Exception){
                        ToastUtils.showShortToast(e.toString())
                    }
                }
            }

            val mColorPickerDialog = ColorPickerDialog(
                    this,
                    resources.getColor(R.color.colorPrimary),
                    false,
                    mOnColorPickerListener
            ).show()

        }*/
        if (userService.getToken() == "") {
            setting_item_logout.setItemText(getString(R.string.setting_relogin))
        }

        setting_item_logout.setOnItemViewClick {
            //清空token和非本地事项
            if (userService.getToken() == "") {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                userService.saveToken("")
                todoService.deleteTeamTask()

                ToastUtils.showShortToast(getString(R.string.setting_logout_success))
            }
        }

/*        setting_item_account.setOnItemViewClick {
            ToastUtils.showShortToast("此功能暂不可用！")
        }*/

    }
}
