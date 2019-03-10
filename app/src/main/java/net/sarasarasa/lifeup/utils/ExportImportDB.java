package net.sarasarasa.lifeup.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class ExportImportDB {

    private static String currentDBPath = "//data//" + "net.sarasarasa.lifeup"
            + "//databases//" + "LifeUpDB.db";

    public static void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {

                File currentDB = new File(data, currentDBPath);
                File backupDBDir = new File(sd + "//LifeUp//backup");
                File backupDB = new File(backupDBDir, "LifeUpDB.db");

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                ToastUtils.Companion.showShortToast("恢复成功");
            }
        } catch (Exception e) {
            ToastUtils.Companion.showShortToast("恢复失败" + e.toString());
        }
    }

    //exporting database
    public static void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                File currentDB = new File(data, currentDBPath);
                File backupDBDir = new File(sd + "//LifeUp//backup");

                if (!backupDBDir.exists())
                    backupDBDir.mkdirs();

                File backupDB = new File(backupDBDir, "LifeUpDB.db");

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                ToastUtils.Companion.showShortToast("备份成功：" + backupDB.getAbsolutePath());
            }
        } catch (Exception e) {
            ToastUtils.Companion.showShortToast("备份失败：" + e.toString());
        }
    }

    public static File getBackupFile() {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                File backupDBDir = new File(sd + "//LifeUp//backup");

                if (!backupDBDir.exists())
                    backupDBDir.mkdirs();

                File backupDB = new File(backupDBDir, "LifeUpDB.db");
                return backupDB;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}