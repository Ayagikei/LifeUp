package net.sarasarasa.lifeup.activities

import android.os.Bundle

import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_add_category.*
import kotlinx.android.synthetic.main.content_category.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.models.CategoryModel
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils

class AddCategoryActivity : AppCompatActivity() {

    private val toDoService = TodoServiceImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_to_do_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                val text = ed_category_name.text.toString()
                if (text.isNotEmpty()) {
                    val category = CategoryModel(text, false)
                    toDoService.addCategory(category)
                    ToastUtils.showShortToast("成功增加清单")
                    finish()
                } else {
                    ToastUtils.showShortToast("清单名字不能为空")
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}