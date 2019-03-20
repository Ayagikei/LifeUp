package net.sarasarasa.lifeup.adapters

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.vo.TeamMembaerListVO


class BoardrListAdapter(layoutResId: Int, data: List<TeamMembaerListVO>) : BaseQuickAdapter<TeamMembaerListVO, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TeamMembaerListVO) {

        helper.setText(R.id.tv_nickname, item.nickname)
                .setText(R.id.tv_exp, item.point.toString())
                .setText(R.id.tv_rank, item.rank.toString())

        when (item.rank) {
            in 1..3 -> {
                helper.setImageResource(R.id.iv_rank, getIconDrawable(item.rank))
                        .setVisible(R.id.iv_rank, true)
                        .setVisible(R.id.tv_rank, false)
            }
            else -> {
                helper.setVisible(R.id.iv_rank, false)
                        .setVisible(R.id.tv_rank, true)
            }
        }

        //设置头像
        val ivAvatar = helper.getView<ImageView>(R.id.iv_avatar)
        val requestOptions = RequestOptions.placeholderOf(R.drawable.ic_pic_loading).error(R.drawable.ic_pic_error)
        Glide.with(mContext).asBitmap().load(item.userHead).apply(requestOptions).into(object : BitmapImageViewTarget(ivAvatar) {
            override fun setResource(resource: Bitmap?) {
                val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.resources, resource)
                circularBitmapDrawable.isCircular = true
                ivAvatar.setImageDrawable(circularBitmapDrawable)
            }
        })


    }

    /** 获得[abbr]属性图标的[Drawable Id] **/
    private fun getIconDrawable(num: Int?): Int {

        Log.i("Num", num.toString())

        return when (num) {
            1 -> R.drawable.ic_rank_1
            2 -> R.drawable.ic_rank_2
            3 -> R.drawable.ic_rank_3
            else -> R.drawable.ic_empty
        }
    }

    /** 获得[taskStatus]属性图标的[Drawable Id] **/
    private fun getStatusIconDrawable(status: Int): Int {
        return TodoItemConverter.strStatusToDrawableId(status)
    }

}