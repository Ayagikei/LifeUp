package net.sarasarasa.lifeup.activities

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_category.*
import kotlinx.android.synthetic.main.content_category.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.models.CategoryModel
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.ClickUtils
import net.sarasarasa.lifeup.utils.ToastUtils

class AddCategoryActivity : AppCompatActivity() {

    private val toDoService = TodoServiceImpl()
    private var categoryId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryId = intent.getLongExtra("categoryId", 0)
        if (categoryId != 0L) {
            ed_category_name.setText(toDoService.getCategoryNameById(categoryId))
            ed_category_name.setSelection(ed_category_name.text.toString().length)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_to_do_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                if (ClickUtils.isNotFastClick()) {
                    val text = ed_category_name.text.toString()
                    if (text.isNotEmpty()) {
                        // 新增
                        if (categoryId == 0L) {
                            val category = CategoryModel(text, false)
                            val newCategoryId = toDoService.addCategory(category)
                            val optionSharedPreferences = LifeUpApplication.getLifeUpApplication().getSharedPreferences("options", Context.MODE_PRIVATE)
                            val editor = optionSharedPreferences?.edit()
                            editor?.putLong("categoryId", newCategoryId ?: 0L)
                            editor?.commit()
                            ToastUtils.showShortToast(getString(R.string.category_add_success))
                            finish()
                        }
                        // 修改
                        else {
                            if (toDoService.renameCategory(categoryId, text)) {
                                ToastUtils.showShortToast(getString(R.string.category_rename_success))
                                finish()
                            } else ToastUtils.showShortToast(getString(R.string.category_rename_fail))
                        }
                    } else {
                        ToastUtils.showShortToast(getString(R.string.category_edittext_empty))
                    }
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}