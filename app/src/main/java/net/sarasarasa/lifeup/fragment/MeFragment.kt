package net.sarasarasa.lifeup.fragment

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
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.service.impl.AttributeLevelServiceImpl
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl

class MeFragment : Fragment() {

    val userService = UserServiceImpl()
    val attributeService = AttributeServiceImpl()
    val attributeLevelService = AttributeLevelServiceImpl()
    val todoService = TodoServiceImpl()
    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.activity_user_mine, container, false)
        (activity as MainActivity).initToolBar(rootView.findViewById(R.id.toolbar))


        initView(rootView)

        return rootView
    }

    private fun initView(rootView: View) {
        val mine = userService.getMine()
        val attributeModel = attributeService.getAttribute()

        rootView.tv_userName.text = mine.nickName
        rootView.tv_teamAmount.text = todoService.getFinishCount().toString()
        rootView.tv_followingAmount.text = todoService.getOverdueCount().toString()
        rootView.tv_followerAmount.text = todoService.getGiveUpCount().toString()

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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            initView(rootView)
        }
    }

    override fun onResume() {
        super.onResume()

        initView(rootView)
    }


}
