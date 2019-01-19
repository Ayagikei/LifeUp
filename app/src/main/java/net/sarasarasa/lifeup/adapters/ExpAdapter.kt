package net.sarasarasa.lifeup.adapters

import android.support.v4.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.models.ExpModel
import java.text.SimpleDateFormat
import java.util.*


class ExpAdapter(layoutResId: Int, data: List<ExpModel>) : BaseQuickAdapter<ExpModel, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ExpModel) {

        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val createTime = if (item.createTime != null)
            timeFormat.format(item.createTime)
        else ""


        helper.setText(R.id.tv_content, item.content)
                .setText(R.id.tv_time, createTime)

        if (!item.isDecrease) {
            helper.setText(R.id.tv_number, "+${item.value}")
                    .setTextColor(R.id.tv_number, ContextCompat.getColor(mContext, R.color.color_exp_increase))
        } else {
            helper.setText(R.id.tv_number, "-${item.value}")
                    .setTextColor(R.id.tv_number, ContextCompat.getColor(mContext, R.color.color_exp_decrease))
        }

        when {
            item.amountOfAttribute == 3 -> helper.setVisible(R.id.iv_iconSkillFrist, true)
                    .setVisible(R.id.iv_iconSkillSecond, true)
                    .setVisible(R.id.iv_iconSkillThird, true).setImageResource(R.id.iv_iconSkillFrist, getAbbrIconDrawable(item.relatedAttribute.getOrNull(0)))
                    .setImageResource(R.id.iv_iconSkillSecond, getAbbrIconDrawable(item.relatedAttribute.getOrNull(1)))
                    .setImageResource(R.id.iv_iconSkillThird, getAbbrIconDrawable(item.relatedAttribute.getOrNull(2)))
            item.amountOfAttribute == 2 -> helper.setVisible(R.id.iv_iconSkillFrist, false)
                    .setVisible(R.id.iv_iconSkillSecond, true)
                    .setVisible(R.id.iv_iconSkillThird, true)
                    .setImageResource(R.id.iv_iconSkillSecond, getAbbrIconDrawable(item.relatedAttribute.getOrNull(0)))
                    .setImageResource(R.id.iv_iconSkillThird, getAbbrIconDrawable(item.relatedAttribute.getOrNull(1)))
            item.amountOfAttribute == 1 -> helper.setVisible(R.id.iv_iconSkillFrist, false)
                    .setVisible(R.id.iv_iconSkillSecond, false)
                    .setVisible(R.id.iv_iconSkillThird, true)
                    .setImageResource(R.id.iv_iconSkillThird, getAbbrIconDrawable(item.relatedAttribute.getOrNull(0)))
        }

    }

    /** 获得[abbr]属性图标的[Drawable Id] **/
    private fun getAbbrIconDrawable(abbr: String?): Int {
        return TodoItemConverter.strAbbrToDrawableId(abbr)
    }


}