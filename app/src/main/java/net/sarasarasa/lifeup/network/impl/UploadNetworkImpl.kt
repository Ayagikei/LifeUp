package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_AVATAR_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_AVATAR_SUCCESS
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.network.UploadNetwork
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.PictureUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.ActivityVO
import net.sarasarasa.lifeup.vo.ResultVO
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    val userService = UserServiceImpl()
    val teamNetworkImpl = TeamNetworkImpl(uiHandler)
    val network: UploadNetwork = retrofit.create(UploadNetwork::class.java)



    fun uploadImages(images: List<String>,taskModel: TaskModel,activityVO: ActivityVO) {
        Log.i("LifeUp 上传模块", "执行[上传图片]操作")

        val parts = ArrayList<MultipartBody.Part>()

        for(item in images){
            val file = File(item)
            PictureUtils.compressBitmap(file.absolutePath, file)
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val part = MultipartBody.Part.createFormData("imageFiles", file.name, requestFile)
            parts.add(part)
        }

        val call = network.uploadImages(userService.getToken(), parts)
        call.enqueue(object : Callback<ResultVO<List<String>>> {
            override fun onFailure(call: Call<ResultVO<List<String>>>?, t: Throwable?) {
                Log.e("LifeUp 上传模块", "[上传图片]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_UPDATE_AVATAR_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<List<String>>>, response: Response<ResultVO<List<String>>>) {
                val responseBody = response.body()

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 上传模块", "[上传图片]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    message.what = MSG_UPDATE_AVATAR_FAILED
                    message.obj = responseBody.msg
                } else {
                    message.what = MSG_UPDATE_AVATAR_SUCCESS
                    val returnList = response.body()?.data
                    activityVO.activityImages = returnList

                    teamNetworkImpl.finishTeamTask(taskModel,activityVO)

                    Log.i("LifeUp 上传模块", "[上传图片]请求成功 " + returnList.toString())
                }
                uiHandler.handleMessage(message)
            }
        })
    }

}