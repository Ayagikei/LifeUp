package net.sarasarasa.lifeup.utils;

import android.app.Activity;
import android.util.Log;

import net.sarasarasa.lifeup.R;
import net.sarasarasa.lifeup.application.LifeUpApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class ExportImportDB {

/*    private static String currentDBPath = "//data//" + "net.sarasarasa.lifeup"
            + "//databases//" + "LifeUpDB.db";*/

    private static String currentDBPath = "//databases//" + "LifeUpDB.db";

    public static void importDB(Activity context) {
        try {
            File backupDBDir = LifeUpApplication.Companion.getLifeUpApplication().getExternalFilesDir("backup");
            File data = LifeUpApplication.Companion.getLifeUpApplication().getFilesDir().getParentFile();

            Log.d("LifeUpBackUp", "importDB: data.getAbsolutePath()");

            if (backupDBDir != null && backupDBDir.canWrite()) {

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(backupDBDir, "LifeUpDB.db");

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                ToastUtils.Companion.showShortToast(context.getString(R.string.backup_restore_success));
            }
        } catch (Exception e) {
            ToastUtils.Companion.showShortToast(context.getString(R.string.backup_restore_failed) + e.toString());
        }
    }

    //exporting database
    public static void exportDB(Activity context) {
        try {
            File backupDBDir = LifeUpApplication.Companion.getLifeUpApplication().getExternalFilesDir("backup");
            File data = LifeUpApplication.Companion.getLifeUpApplication().getFilesDir().getParentFile();

            Log.d("LifeUpBackUp", "importDB: " + data);

            if (backupDBDir != null && backupDBDir.canWrite()) {
                File currentDB = new File(data, currentDBPath);

                if (!backupDBDir.exists())
                    backupDBDir.mkdirs();

                File backupDB = new File(backupDBDir, "LifeUpDB.db");

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                ToastUtils.Companion.showShortToast(context.getString(R.string.backup_success) + backupDB.getAbsolutePath());
            }
        } catch (Exception e) {
            ToastUtils.Companion.showShortToast(context.getString(R.string.backup_failed) + e.toString());
        }
    }

    public static File getBackupFile() {
        try {
            File backupDBDir = LifeUpApplication.Companion.getLifeUpApplication().getExternalFilesDir("backup");
            if (backupDBDir != null && backupDBDir.canWrite()) {
                if (!backupDBDir.exists())
                    backupDBDir.mkdirs();

                return new File(backupDBDir, "LifeUpDB.db");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}