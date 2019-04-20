package net.sarasarasa.lifeup.adapters

import android.util.Log
import android.view.View
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.UserActivity
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.vo.TeamActivityListVO
import java.text.SimpleDateFormat
import java.util.*


class UserActivityListAdapter(layoutResId: Int, data: List<TeamActivityListVO>) : BaseQuickAdapter<TeamActivityListVO, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TeamActivityListVO) {

        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())


        helper.setText(R.id.tv_date, timeFormat.format(item.createTime))
                .setImageResource(R.id.iv_icon, getIconDrawable(item.activityIcon))


        if (item.activityIcon == 2) {
            if (item.userActivity.isNullOrEmpty()) {
                helper.setText(R.id.tv_nickname, mContext.getString(R.string.team_activity_finish))
                        .setText(R.id.tv_remark, "")
            } else {
                helper.setText(R.id.tv_nickname, mContext.getString(R.string.team_activity_finish_and_submit))
                        .setText(R.id.tv_remark, item.userActivity + "\n")
            }
        } else {
            if (item.activityIcon == 1) {
                helper.setText(R.id.tv_nickname, mContext.getString(R.string.welcome) + item.nickname + mContext.getString(R.string.join_team) + "「" + item.teamTitle + "」")
                        .setText(R.id.tv_remark, "")
            } else {
                helper.setText(R.id.tv_nickname, item.userActivity)
                        .setText(R.id.tv_remark, "")
            }
        }


        if (item.teamTitle.isNullOrEmpty()) {
            helper.setVisible(R.id.tv_teamTitle, false)
        } else {
            helper.setVisible(R.id.tv_teamTitle, true)
            helper.setText(R.id.tv_teamTitle, "「${item.teamTitle}」" + mContext.getString(R.string.team))
        }

        val ninePhotoLayout = helper.getView<BGANinePhotoLayout>(R.id.npl_item_moment_photos)
        if (item.activityImages != null) {
            ninePhotoLayout.data = item.activityImages
            ninePhotoLayout.visibility = View.VISIBLE
        } else {
            ninePhotoLayout.visibility = View.GONE
        }

        helper.addOnClickListener(R.id.npl_item_moment_photos)

        val delegate = mContext as UserActivity
        ninePhotoLayout.setDelegate(delegate)


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