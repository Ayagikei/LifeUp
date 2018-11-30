package net.sarasarasa.lifeup.adapters

import android.graphics.Bitmap
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.TeamActivity
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.vo.TeamActivityListVO
import java.text.SimpleDateFormat
import java.util.*


class TeamActivityListAdapter(layoutResId: Int, data: List<TeamActivityListVO>) : BaseQuickAdapter<TeamActivityListVO, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TeamActivityListVO) {

        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())


        helper.setText(R.id.tv_headerText, item.nickname)
                .setText(R.id.tv_date, timeFormat.format(item.createTime))
                .setImageResource(R.id.iv_icon, getIconDrawable(item.activityIcon))
                .setVisible(R.id.tv_teamTitle, false)

        if (item.activityIcon == 2) {
            if (item.userActivity.isNullOrEmpty()) {
                helper.setText(R.id.tv_nickname, "完成了本次事项")
                        .setText(R.id.tv_remark, "")
            } else {
                helper.setText(R.id.tv_nickname, "完成了本次事项并发表了动态：")
                        .setText(R.id.tv_remark, item.userActivity + "\n")
            }
        } else {
            helper.setText(R.id.tv_nickname, item.userActivity)
                    .setText(R.id.tv_remark, "")
        }


        val ninePhotoLayout = helper.getView<BGANinePhotoLayout>(R.id.npl_item_moment_photos)
        if(item.activityImages != null) {
            ninePhotoLayout.data = item.activityImages
            ninePhotoLayout.visibility = View.VISIBLE
        }else{
            ninePhotoLayout.visibility = View.GONE
        }

        helper.addOnClickListener(R.id.npl_item_moment_photos)
                .addOnClickListener(R.id.iv_avatar)

        val delegate = mContext as TeamActivity
        ninePhotoLayout.setDelegate(delegate)

/*        if(item.teamTitle.isNullOrEmpty()){
            helper.setVisible(R.id.tv_teamTitle,false)
        }
        else{
            helper.setVisible(R.id.tv_teamTitle,true)
            helper.setText(R.id.tv_teamTitle,"「${item.teamTitle}」团队")
        }*/

        //设置头像
        val ivAvatar = helper.getView<ImageView>(R.id.iv_avatar)
        val requestOptions = RequestOptions.placeholderOf(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
        Glide.with(mContext as TeamActivity).asBitmap().load(item.userHead).apply(requestOptions).into(object : BitmapImageViewTarget(ivAvatar) {
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
            0 -> R.drawable.ic_new
            1 -> R.drawable.ic_add
            2 -> R.drawable.ic_sign
            else -> R.drawable.ic_empty
        }
    }

    /** 获得[taskStatus]属性图标的[Drawable Id] **/
    private fun getStatusIconDrawable(status: Int): Int {
        return TodoItemConverter.strStatusToDrawableId(status)
    }

}