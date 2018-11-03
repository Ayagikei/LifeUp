package net.sarasarasa.lifeup.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Pedometer implements SensorEventListener {
    private static final int sensorTypeD = Sensor.TYPE_STEP_DETECTOR;
    private static final int sensorTypeC = Sensor.TYPE_STEP_COUNTER;
    private SensorManager mSensorManager;
    private Sensor mStepCount;
    private Sensor mStepDetector;
    private float mCount;//步行总数
    private float mDetector;//步行探测器
    private Context context;

    public Pedometer() {

    }

    public Pedometer(Context context) {
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mStepCount = mSensorManager.getDefaultSensor(sensorTypeC);
        mStepDetector = mSensorManager.getDefaultSensor(sensorTypeD);
    }

    public void register() {
        if (mStepCount == null && mStepDetector == null) {
            ToastUtils.Companion.showLongToast("你的手机可能不支持计步传感器，部分功能不可用。");
        } else {
            if (mStepCount != null)
                register(mStepCount, SensorManager.SENSOR_DELAY_FASTEST);

            if (mStepDetector != null)
                register(mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void unRegister() {
        mSensorManager.unregisterListener(this);
    }

    private void register(Sensor sensor, int rateUs) {
        mSensorManager.registerListener(this, sensor, rateUs);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == sensorTypeC) {
            setStepCount(event.values[0]);
        }
        if (event.sensor.getType() == sensorTypeD) {
            if (event.values[0] == 1.0) {
                mDetector++;
            }
        }
    }

    public float getStepCount() {
        return mCount;
    }

    private void setStepCount(float count) {
        this.mCount = count;
    }

    public float getmDetector() {
        return mDetector;
    }

}
