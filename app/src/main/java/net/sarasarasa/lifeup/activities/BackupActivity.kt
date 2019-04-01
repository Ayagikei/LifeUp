package net.sarasarasa.lifeup.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import kotlinx.android.synthetic.main.activity_backup.*
import kotlinx.android.synthetic.main.content_backup.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.utils.ExportImportDB
import net.sarasarasa.lifeup.utils.ToastUtils
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*


class BackupActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        private const val PRC_BACKUP_RESTORE = 1
    }

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

    @AfterPermissionGranted(PRC_BACKUP_RESTORE)
    private fun processBackup() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            ExportImportDB.exportDB()
            // 备份成功后更新备份信息
            updateRestoreInfo()
        } else {
            EasyPermissions.requestPermissions(this, "备份数据需要以下权限：1. 读写外部存储空间", PRC_BACKUP_RESTORE, *perms)
        }
    }

    @AfterPermissionGranted(PRC_BACKUP_RESTORE)
    private fun processRestore() {
        MaterialDialog(this).show {
            title(text = "恢复")
            message(text = "你确定要恢复数据吗？\n为了应用的正常运行，需要重启应用。")
            positiveButton(R.string.btn_yes) {
                val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (EasyPermissions.hasPermissions(this@BackupActivity, *perms)) {
                    ExportImportDB.importDB()
                    restartApplication()
                } else {
                    EasyPermissions.requestPermissions(this@BackupActivity, "恢复备份数据需要以下权限：1. 读写外部存储空间", PRC_BACKUP_RESTORE, *perms)
                }
            }
            negativeButton(R.string.btn_cancel)
            lifecycleOwner(this@BackupActivity)
        }

    }

    @AfterPermissionGranted(PRC_BACKUP_RESTORE)
    private fun updateRestoreInfo() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
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
        } else {
            EasyPermissions.requestPermissions(this, "检测备份数据需要以下权限：1. 读写外部存储空间", PRC_BACKUP_RESTORE, *perms)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PRC_BACKUP_RESTORE) {
            ToastUtils.showShortToast("您拒绝了「备份/恢复」所需要的相关权限!")
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }


    private fun restartApplication() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}