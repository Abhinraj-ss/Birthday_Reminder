package com.abhinraj.birthdayremainder.ui.home

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.abhinraj.birthdayremainder.R
import com.abhinraj.birthdayremainder.activity.AddNewActivity
import com.abhinraj.birthdayremainder.activity.MainActivity
import com.abhinraj.birthdayremainder.activity.SettingsActivity
import com.abhinraj.birthdayremainder.database.BirthdayDatabase
import com.abhinraj.birthdayremainder.database.BirthdayEntity
import com.abhinraj.birthdayremainder.util.NotificationReceiver
import com.abhinraj.birthdayremainder.util.NotificationService
import com.abhinraj.birthdayremainder.util.Sorter
import java.security.cert.PKIXRevocationChecker
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var rlLoading: RelativeLayout
    private lateinit var recyclerHome: RecyclerView
    private lateinit var rlNoProfile: RelativeLayout
    private lateinit var layoutManager: LinearLayoutManager
    private val list = arrayListOf<Birthdays>()
    private lateinit var recyclerAdapter: HomeRecyclerAdapter
    @SuppressLint("SimpleDateFormat")
    val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
    private val currentDate = sdf.format(Date())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_home, container, false)



        val ex = Birthdays(1, "Abhin Raj", "06/11/2000"+" 00:00:00", 21,"Male",  "01/11/2022"+" 00:00:00")
        val ex1 = Birthdays(2, "Surabi Suresh", "06/11/2001"+" 00:00:00",20, "Female",  "01/11/2022"+" 00:00:00")
        //list.add(ex)
        //list.add(ex1)

        val backgroundList = BirthdaysAsync(activity as Context).execute().get()

        for (i in backgroundList){
            //System.out.println(i.dob)
            val dobList = i.dob.split("/"," ",":").toList()
            val currentList = currentDate.split("/"," ",":").toList()
            val diffList= arrayListOf<Int>()
            for (j in 0..5){
                diffList.add(currentList[j].toInt() -dobList[j].toInt())
            }
            var age = diffList[2]


           // println("${diffList[2]}"+" "+ "${diffList[1]}"+" "+ "${diffList[0]}"+" "+ "${diffList[3]}"+" "+ "${diffList[4]}"+" "+"${diffList[5]}")


            if (diffList[1]==0){
                if (diffList[0]==0){
                    if (diffList[3]==0){
                        if (diffList[4]==0){
                            if (diffList[5]==0&& dobList[1].toInt()==12 && dobList[0].toInt()==31){
                                age+=1
                            }
                            else if(diffList[5]<0){
                                age-=1
                            }
                        }
                        else if(diffList[4]<0){
                            age-=1
                        }
                    }
                    else if(diffList[3]<0){
                        age-=1
                    }
                }
                else if(diffList[0]<0){
                    age-=1
                }
            }
            else if(diffList[1]<0){
                age-=1
            }
            i.age = age
            HomeRecyclerAdapter.DBAsyncTask(activity as Context, i, 4).execute().get()
        }

        progressBar = root?.findViewById(R.id.progressBar) as ProgressBar
        rlLoading = root.findViewById(R.id.rlLoading) as RelativeLayout
        rlNoProfile =root.findViewById(R.id.rlNoProfiles) as RelativeLayout
        rlLoading.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        if (backgroundList.isEmpty()) {
            rlLoading.visibility = View.GONE
            progressBar.visibility = View.GONE
            rlNoProfile.visibility = View.VISIBLE

        } else {
            rlLoading.visibility = View.GONE
            progressBar.visibility = View.GONE
            for (i in backgroundList) {
                list.add(
                    Birthdays(
                        i.id,
                        i.name,
                        i.dob,
                        i.age,
                        i.gender,
                        i.notify
                    )
                )
            }
        }
        Collections.sort(list, Sorter.bdayComparator)
        recyclerHome = root.findViewById(R.id.recycler_home)
        layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = HomeRecyclerAdapter(activity as Context, list)
        recyclerHome.adapter = recyclerAdapter
        recyclerHome.layoutManager = layoutManager
        setHasOptionsMenu(true)
        return root
        }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.main, menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings ->{
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.action_sort_bday -> {
                Collections.sort(list, Sorter.bdayComparator)
            }
            R.id.action_sort_age -> {
                Collections.sort(list, Sorter.ageComparator)
            }
            R.id.action_sort_age_desc -> {
                Collections.sort(list, Sorter.ageComparator)
                list.reverse()
            }
            R.id.action_sort_name -> {
                Collections.sort(list, Sorter.nameComparator)
            }

        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
/*
    @SuppressLint("NotifyDataSetChanged")
    private fun showDialog(context: Context) {

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Sort By?")
        builder.setSingleChoiceItems(R.array.filters, checkedItem) { _, isChecked ->
            checkedItem = isChecked
        }
        builder.setPositiveButton("Ok") { _, _ ->

            when (checkedItem) {
                0 -> {
                    Collections.sort(list, Sorter.ageComparator)
                }
                1 -> {
                    Collections.sort(list, Sorter.ageComparator)
                    list.reverse()
                }
                2 -> {
                    Collections.sort(list, Sorter.nameComparator)
                    list.reverse()
                }
            }
            recyclerAdapter.notifyDataSetChanged()
            builder.setNegativeButton("Cancel") { _, _ ->

            }
            builder.create()
            builder.show()
        }
    }*/

    class BirthdaysAsync(context: Context) : AsyncTask<Void, Void, List<BirthdayEntity>>() {

            private val db = Room.databaseBuilder(context, BirthdayDatabase::class.java, "bday-db").build()


            override fun doInBackground(vararg params: Void?): List<BirthdayEntity> {
                val allBirthdays:List<BirthdayEntity> =db.birthdayDao().getAllBirthdays()
                db.close()
                return  allBirthdays
            }

        }


}