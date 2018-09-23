package net.sarasarasa.lifeup.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import kotlinx.android.synthetic.main.content_user_mine.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.HistoryActivity
import net.sarasarasa.lifeup.activities.SettingActivity
import net.sarasarasa.lifeup.activities.UserActivity
import net.sarasarasa.lifeup.activities.WelcomeActivity
import net.sarasarasa.lifeup.service.impl.AttributeLevelServiceImpl
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl

class MeFragment : Fragment() {

    val userService = UserServiceImpl()
    val attributeService = AttributeServiceImpl()
    val attributeLevelService = AttributeLevelServiceImpl()
    val todoService = TodoServiceImpl()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.activity_user_mine, container, false)



        initView(rootView)

        return rootView
    }

    private fun initView(rootView: View) {
        val mine = userService.getMine()
        val attributeModel = attributeService.getAttribute()

        rootView.tv_userName.text = mine.nickName
        rootView.tv_finishAmount.text = todoService.getFinishCount().toString()
        rootView.tv_overdueAmount.text = todoService.getOverdueCount().toString()
        rootView.tv_giveupAmount.text = todoService.getGiveUpCount().toString()

        rootView.tv_expAmount.text = attributeService.getTotalAttrExp().toString()
        rootView.tv_lifeExpAmount.text = attributeModel.gradeAttribute.toString()


        val requestOptions = RequestOptions.placeholderOf(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)

        if (!mine.userHead.isNullOrBlank())
            Glide.with(this).asBitmap().load(mine.userHead).apply(requestOptions).into(object : BitmapImageViewTarget(rootView.iv_avatar) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(activity!!.resources, resource)
                    circularBitmapDrawable.isCircular = true
                    rootView.iv_avatar.setImageDrawable(circularBitmapDrawable)
                }
            })
    }


    fun openProfile(view: View) {
        val intent = Intent(activity, UserActivity::class.java)
        startActivity(intent)
    }

    fun openHistory(view: View) {
        val intent = Intent(activity, HistoryActivity::class.java)
        startActivity(intent)
    }

    fun openWelcome(view: View) {
        val intent = Intent(activity, WelcomeActivity::class.java)
        startActivity(intent)
    }

    fun openSetting(view: View) {
        val intent = Intent(activity, SettingActivity::class.java)
        startActivity(intent)
    }


}
