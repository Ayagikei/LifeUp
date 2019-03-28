package net.sarasarasa.lifeup.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ToDoItemDetailActivity : AppCompatActivity() {

    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_to_do_item_detail)
        val intent = intent
        id = intent.getLongExtra("id", -1)

        if (id != -1L) {
            initStatus(id)
        }

    }

    private fun initStatus(id: Long) {

    }

}