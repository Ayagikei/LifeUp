package net.sarasarasa.lifeup.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.sarasarasa.lifeup.service.AttributeService;
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl;

/**
 * Created by AyagiKei on 2018/6/19 0019.
 */

public class SplashActivity extends AppCompatActivity {

    AttributeService attributeService = new AttributeServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断是不是第一次打开应用
        SharedPreferences sharedPreferences = getSharedPreferences("status", MODE_PRIVATE);
        boolean isFirst = sharedPreferences.getBoolean("isFirst", true);
        Editor editor = sharedPreferences.edit();

        if (isFirst) {
            //第一次进入的时候，跳转到引导页
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            //初始化数据
            attributeService.initAttribute();
            editor.putBoolean("isFirst", false);
            editor.apply();

        } else {
            //否则，进入主页面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }
}