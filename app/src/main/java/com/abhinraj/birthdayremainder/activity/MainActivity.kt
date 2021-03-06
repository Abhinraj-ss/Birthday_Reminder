package com.abhinraj.birthdayremainder.activity


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.abhinraj.birthdayremainder.R
import com.abhinraj.birthdayremainder.ui.home.HomeFragment
import com.abhinraj.birthdayremainder.util.NotificationService

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val extras = getIntent().getExtras()
        if(extras?.getString("clear")=="clear"){
            dismissForgroundService(this)
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val intent = Intent(this, AddNewActivity::class.java)
            startActivity(intent)
            finish()

        }
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_calendar, R.id.nav_slideshow
        ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    private fun displayHome() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
       /* supportActionBar?.title = "All Restaurants"
        navView.setCheckedItem(R.id.nav_home)*/
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onBackPressed() {
        val f = supportFragmentManager.findFragmentById(R.id.frame)
        when (f) {
            is HomeFragment -> {
                super.onBackPressed()
            }
            else -> displayHome()
            /*
            is RestaurantFragment -> {
                if (!RestaurantMenuAdapter.isCartEmpty) {
                    val builder = AlertDialog.Builder(this@DashboardActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Going back will reset cart items. Do you still want to proceed?")
                        .setPositiveButton("Yes") { _, _ ->
                            val clearCart =
                                CartActivity.ClearDBAsync(applicationContext, resId.toString()).execute().get()
                            displayHome()
                            RestaurantMenuAdapter.isCartEmpty = true
                        }
                        .setNegativeButton("No") { _, _ ->

                        }
                        .create()
                        .show()
                } else {
                    displayHome()
                }*/
        }
    }
    private fun dismissForgroundService(context: Context):Boolean{
        val intent = Intent(context, NotificationService::class.java).apply{
            putExtra("order","kill")
        }
        context.startService(intent)
        return true
    }
}