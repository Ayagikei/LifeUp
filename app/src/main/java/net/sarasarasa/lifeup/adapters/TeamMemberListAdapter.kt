package net.sarasarasa.lifeup.adapters

import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatButton
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.vo.TeamMembaerListVO
import java.text.SimpleDateFormat
import java.util.*


class TeamMemberListAdapter(layoutResId: Int, data: List<TeamMembaerListVO>) : BaseQuickAdapter<TeamMembaerListVO, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TeamMembaerListVO) {

        val timeFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())


        helper.setText(R.id.tv_nickname, item.nickname)
                .setText(R.id.tv_remark, timeFormat.format(item.createTime) + "加入")

        when (item.isFollow) {
            -1 -> helper.setVisible(R.id.btn_follow, false)
            0 -> {
                helper.setVisible(R.id.btn_follow, true)
                val btn = helper.getView<AppCompatButton>(R.id.btn_follow)
                btn.text = "关注"
                val colorStateList = ContextCompat.getColorStateList(mContext, R.color.blue)
                ViewCompat.setBackgroundTintList(btn, colorStateList)
            }
            1 -> {
                helper.setVisible(R.id.btn_follow, true)
                val btn = helper.getView<AppCompatButton>(R.id.btn_follow)
                btn.text = "已关注"
                val colorStateList = ContextCompat.getColorStateList(mContext, R.color.clicked_btn)
                ViewCompat.setBackgroundTintList(btn, colorStateList)
            }
            2 -> {
                helper.setVisible(R.id.btn_follow, true)
                val btn = helper.getView<AppCompatButton>(R.id.btn_follow)
                btn.text = "互相关注"
                val colorStateList = ContextCompat.getColorStateList(mContext, R.color.clicked_btn)
                ViewCompat.setBackgroundTintList(btn, colorStateList)
            }
        }

        helper.addOnClickListener(R.id.btn_follow)

        //设置头像
        val ivAvatar = helper.getView<ImageView>(R.id.iv_avatar)
        val requestOptions = RequestOptions.placeholderOf(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
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