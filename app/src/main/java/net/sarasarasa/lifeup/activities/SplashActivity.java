package net.sarasarasa.lifeup.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import net.sarasarasa.lifeup.service.AttributeLevelService;
import net.sarasarasa.lifeup.service.AttributeService;
import net.sarasarasa.lifeup.service.TodoService;
import net.sarasarasa.lifeup.service.impl.AttributeLevelServiceImpl;
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl;
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl;

import java.util.Locale;

import static org.litepal.LitePalApplication.getContext;

/**
 * Created by AyagiKei on 2018/6/19 0019.
 */

public class SplashActivity extends AppCompatActivity {

    AttributeService attributeService = new AttributeServiceImpl();
    TodoService todoService = new TodoServiceImpl();
    AttributeLevelService attributeLevelService = new AttributeLevelServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断是不是第一次打开应用
        SharedPreferences sharedPreferences = getSharedPreferences("status", MODE_PRIVATE);
        boolean isFirst = sharedPreferences.getBoolean("isFirst", true);
        int iDataBaseVersion = sharedPreferences.getInt("iiDataBaseVersion", 0);
        Editor editor = sharedPreferences.edit();

        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = Locale.SIMPLIFIED_CHINESE;
        resources.updateConfiguration(config, dm);

        if (isFirst) {
            //第一次进入的时候，跳转到引导页
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();

            //初始化数据
            attributeService.initAttribute();
            attributeLevelService.initAttributeLevel();
            todoService.addGuideTask();
            editor.putBoolean("isFirst", false);
            editor.apply();

        } else {
            //否则，进入主页面

            if (iDataBaseVersion == 0) {
                attributeLevelService.initAttributeLevel();
                editor.putInt("iiDataBaseVersion", 1);
            }

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }
}