package net.sarasarasa.lifeup.utils

import android.content.Context
import android.util.TypedValue

/**
 * Created by AyagiKei on 2018/6/17 0017.
 */

class DensityUtil {

    companion object {

        lateinit var context: Context

        fun init(context: Context) {
            this.context = context
        }

        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        fun dp2px(dpVal: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dpVal, context.resources.displayMetrics).toInt()
        }

        /**
         * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
         */
        fun px2dip(pxValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        fun dp2px(mContext: Context, dpVal: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dpVal, mContext.resources.displayMetrics).toInt()
        }

        /**
         * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
         */
        fun px2dip(mContext: Context, pxValue: Float): Int {
            val scale = mContext.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }
    }


}
