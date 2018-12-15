package net.sarasarasa.lifeup.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import kotlinx.android.synthetic.main.content_user_mine.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.service.impl.AttributeLevelServiceImpl
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl


class UserMineActivity : AppCompatActivity() {

    val userService = UserServiceImpl()
    val attributeService = AttributeServiceImpl()
    val attributeLevelService = AttributeLevelServiceImpl()
    val todoService = TodoServiceImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_mine)

/*
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
*/

        initData()
    }

    private fun initData() {
        val mine = userService.getMine()
        val attributeModel = attributeService.getAttribute()

        tv_userName.text = mine.nickName
        tv_teamAmount.text = todoService.getFinishCount().toString()
        tv_followingAmount.text = todoService.getOverdueCount().toString()
        tv_followerAmount.text = todoService.getGiveUpCount().toString()

        tv_expAmount.text = attributeService.getTotalAttrExp().toString()
        tv_lifeExpAmount.text = attributeModel.gradeAttribute.toString()


        val requestOptions = RequestOptions.placeholderOf(R.drawable.ic_pic_loading).error(R.drawable.ic_pic_error)

        if (!mine.userHead.isNullOrBlank())
            Glide.with(this).asBitmap().load(mine.userHead).apply(requestOptions).into(object : BitmapImageViewTarget(iv_avatar) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(this@UserMineActivity.resources, resource)
                    circularBitmapDrawable.isCircular = true
                    iv_avatar.setImageDrawable(circularBitmapDrawable)
                }
            })

    }




}