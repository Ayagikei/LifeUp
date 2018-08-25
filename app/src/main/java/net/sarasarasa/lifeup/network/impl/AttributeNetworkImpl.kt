package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.AttributeConstants.Companion.MSG_ATTR_GET_SUCCESS
import net.sarasarasa.lifeup.constants.AttributeConstants.Companion.MSG_ATTR_UPDATE_SUCCESS
import net.sarasarasa.lifeup.constants.AttributeConstants.Companion.MSG_CONNECT_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.network.AttributeNetwork
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.vo.AttributionVO
import net.sarasarasa.lifeup.vo.ResultVO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AttributeNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    val attributeService = AttributeServiceImpl()
    val userService = UserServiceImpl()

    fun getAttribute() {
        Log.i("LifeUp 属性模块", "执行[获取用户属性]操作")

        val network = retrofit.create(AttributeNetwork::class.java)
        val call = network.getAttribute(userService.getToken())

        call.enqueue(object : Callback<ResultVO<AttributionVO>> {
            override fun onFailure(call: Call<ResultVO<AttributionVO>>?, t: Throwable?) {
                Log.e("LifeUp 属性模块", "[获取用户属性]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<AttributionVO>>, response: Response<ResultVO<AttributionVO>>) {
                val responseBody = response.body()

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 属性模块", "[获取用户属性]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVAILD_TOKEN

                } else {
                    message.what = MSG_ATTR_GET_SUCCESS
                    val attributionVO = responseBody?.data

                    if (attributionVO != null) {
                        Log.i("LifeUp 属性模块", "[获取用户属性]请求成功：${attributionVO}")

                        val attributeModel = attributeService.getAttribute()
                        val gradeRemote = attributionVO.userGrade
                        if (gradeRemote != null && gradeRemote > attributeModel.gradeAttribute) {

                            attributeModel.copy(gradeRemote,
                                    attributionVO.attributeStrength
                                            ?: attributeModel.strengthAttribute,
                                    attributionVO.attributeKnowledge
                                            ?: attributeModel.knowledgeAttribute,
                                    attributionVO.attributeCharm ?: attributeModel.charmAttribute,
                                    attributionVO.attributeEndurance
                                            ?: attributeModel.enduranceAttribute,
                                    attributionVO.attributeEnergy ?: attributeModel.energyAttribute,
                                    attributionVO.attributeCreativity ?: attributeModel.creativity)
                            attributeModel.save()

                            Log.i("LifeUp 属性模块", "[更新本地属性]操作成功：${attributeModel}")
                        }
                    }
                }
                uiHandler.handleMessage(message)
            }
        })
    }


    fun updateAttribute(attributionVO: AttributionVO) {
        Log.i("LifeUp 属性模块", "执行[更新用户属性]操作")

        val network = retrofit.create(AttributeNetwork::class.java)
        val call = network.updateAttribute(userService.getToken(), attributionVO)

        call.enqueue(object : Callback<ResultVO<AttributionVO>> {
            override fun onFailure(call: Call<ResultVO<AttributionVO>>?, t: Throwable?) {
                Log.e("LifeUp 属性模块", "[更新用户属性]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<AttributionVO>>, response: Response<ResultVO<AttributionVO>>) {
                val responseBody = response.body()

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 属性模块", "[更新用户属性]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVAILD_TOKEN
                } else {
                    message.what = MSG_ATTR_UPDATE_SUCCESS
                    val attributionVO = responseBody?.data
                    Log.i("LifeUp 属性模块", "[更新用户属性]请求成功：${attributionVO}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }


}