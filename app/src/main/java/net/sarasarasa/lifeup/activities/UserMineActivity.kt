package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import kotlinx.android.synthetic.main.activity_user_mine.*
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

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        initData()
    }

    private fun initData() {
        val mine = userService.getMine()

        tv_userName.text = mine.nickName ?: "试用用户"
        tv_userDesc.text = mine.userAddress ?: "登陆以获得更优质的体验"
        val attributeModel = attributeService.getAttribute()
        val level = attributeLevelService.getAttributeLevelByExp(attributeModel.gradeAttribute).levelNum
        tv_teamDesc.text = "人生等级：LV$level"

        tv_finishCnt.text = "完成了${todoService.getFinishCount()}个待办事项"
        tv_giveupcnt.text = "放弃了${todoService.getGiveUpCount()}个待办事项"
        tv_overduecnt.text = "逾期了${todoService.getOverdueCount()}个待办事项"
        tv_lifeExp.text = "人生总经验值：${attributeModel.gradeAttribute}点"
        tv_attrExp.text = "属性总经验值：${attributeService.getTotalAttrExp()}点"

        val requestOptions = RequestOptions.placeholderOf(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)

        if (!mine.userHead.isNullOrBlank())
            Glide.with(this).asBitmap().load(mine.userHead).apply(requestOptions).into(object : BitmapImageViewTarget(iv_avatar) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(this@UserMineActivity.resources, resource)
                    circularBitmapDrawable.isCircular = true
                    iv_avatar.setImageDrawable(circularBitmapDrawable)
                }
            })

    }

    fun openProfile(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun openHistory(view: View) {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }


}