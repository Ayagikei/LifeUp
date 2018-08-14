package net.sarasarasa.lifeup.activities

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.activity_main.*
import net.sarasarasa.lifeup.R


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var dialogView: View? = null
    var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    fun initToolBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, TeamActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_history -> {
                showDialogLifeUp()
            }
            R.id.nav_achievement -> {
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_about -> {
                val intent = Intent(this, AddTeamActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun login(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun play(view: View) {
        if (view is LottieAnimationView) {
            view.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    showDialogAbbr()
                }

                override fun onAnimationCancel(p0: Animator?) {
                    showDialogAbbr()
                }

                override fun onAnimationStart(p0: Animator?) {
                }
            })

            view.playAnimation()
        }
    }

    private fun showDialogAbbr() {
        if (dialog != null)
            return

        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_abbr, null)
        dialog = AlertDialog.Builder(this).create()

        with(dialog) {
            this?.setTitle("你获得了经验值")
            this?.setIcon(R.drawable.ic_award_exp)
            this?.setButton(AlertDialog.BUTTON_POSITIVE, "确定") { _, _ ->
                dismiss()
                dialog = null
            }
            this?.setView(dialogView)
            this?.show()
        }
    }

    private fun showDialogLifeUp() {
        if (dialog != null)
            return

        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_lifeup, null)
        dialog = AlertDialog.Builder(this).create()

        with(dialog) {
            this?.setButton(AlertDialog.BUTTON_POSITIVE, "确定") { _, _ ->
                dismiss()
                dialog = null
            }
            this?.setView(dialogView)
            this?.show()
        }
    }
}
