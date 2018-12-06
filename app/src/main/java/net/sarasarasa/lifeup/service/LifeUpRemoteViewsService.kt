package net.sarasarasa.lifeup.service

import android.content.Intent
import android.widget.RemoteViewsService

class LifeUpRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return LifeUpRemoteViewsFactory(this.applicationContext, intent)
    }

}