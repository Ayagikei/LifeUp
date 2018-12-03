package net.sarasarasa.lifeup.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import net.sarasarasa.lifeup.models.TaskModel

class WidgetAdapter(context: Context, resource: Int, private val resourceId: Int, objects: Array<TaskModel>) : ArrayAdapter<TaskModel>(context, resource, resourceId, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getView(position, convertView, parent)
    }
}
