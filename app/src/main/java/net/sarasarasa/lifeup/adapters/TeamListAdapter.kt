package net.sarasarasa.lifeup.adapters

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.vo.TeamListVO
import java.text.SimpleDateFormat
import java.util.*


class TeamListAdapter(layoutResId: Int, data: List<TeamListVO>) : BaseQuickAdapter<TeamListVO, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TeamListVO) {

        val timeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        helper.setText(R.id.tv_nickname, item.teamTitle)
                .setText(R.id.tv_remark, item.teamDesc)
                .setText(R.id.tv_date, timeFormat.format(item.startDate))
                .setText(R.id.tv_headerText, item.teamFreq?.let { TodoItemConverter.iFrequencyToTitleString(it) })


        //设置头像
        val ivAvatar = helper.getView<ImageView>(R.id.iv_avatar)
        val requestOptions = RequestOptions.bitmapTransform(RoundCorner(10f)).placeholder(R.drawable.ic_pic_loading).error(R.drawable.ic_pic_error)

        Glide.with(mContext).asBitmap().load(item.teamHead).apply(requestOptions).into(ivAvatar)


    }

    /** 获得[abbr]属性图标的[Drawable Id] **/
    private fun getAbbrIconDrawable(abbr: String?): Int {
        return TodoItemConverter.strAbbrToDrawableId(abbr)
    }

    /** 获得[taskStatus]属性图标的[Drawable Id] **/
    private fun getStatusIconDrawable(status: Int): Int {
        return TodoItemConverter.strStatusToDrawableId(status)
    }

}