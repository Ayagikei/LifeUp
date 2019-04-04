package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import kotlinx.android.synthetic.main.activity_backup.*
import kotlinx.android.synthetic.main.content_backup.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.utils.ExportImportDB
import java.text.SimpleDateFormat
import java.util.*


class BackupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        init()
    }

    private fun init() {
        updateRestoreInfo()

        ly_restore.setOnClickListener {
            processRestore()
        }

        ly_backup.setOnClickListener {
            processBackup()
        }

    }

    private fun processBackup() {
            ExportImportDB.exportDB()
            // 备份成功后更新备份信息
            updateRestoreInfo()
    }

    private fun processRestore() {
        MaterialDialog(this).show {
            title(text = "恢复")
            message(text = "你确定要恢复数据吗？\n为了应用的正常运行，需要重启应用。")
            positiveButton(R.string.btn_yes) {

                ExportImportDB.importDB()
                    restartApplication()
            }
            negativeButton(R.string.btn_cancel)
            lifecycleOwner(this@BackupActivity)
        }

    }

    private fun updateRestoreInfo() {
            val backupDB = ExportImportDB.getBackupFile()
            if (backupDB != null) {
                tv_backup_tint.text = "备份路径：${backupDB.absolutePath}"
                if (!backupDB.exists()) {
                    tv_restore_tint.text = "未检测到备份文件！"
                } else {
                    val lastModifiedDate = Date()
                    lastModifiedDate.time = backupDB.lastModified()
                    val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                    tv_restore_tint.text = "检测到备份文件：${formatter.format(lastModifiedDate)}"
                }
            }

    }

    private fun restartApplication() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}