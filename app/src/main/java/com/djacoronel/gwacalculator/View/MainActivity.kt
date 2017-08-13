package com.djacoronel.gwacalculator.View

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.Model.Course
import com.djacoronel.gwacalculator.Model.CourseRepository
import com.djacoronel.gwacalculator.Presenter.GWACalcPresenter
import com.djacoronel.gwacalculator.R

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.gwa_layout.*
import kotlinx.android.synthetic.main.input_layout.view.*
import org.jetbrains.anko.alert

class MainActivity : AppCompatActivity(), Contract.View{

    lateinit var mPresenter: Contract.Actions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mPresenter = GWACalcPresenter(this, CourseRepository(this))
        mPresenter.loadCourses()

        fab.setOnClickListener {
            showInput()
        }
    }

    override fun updateGWA(gwa: Double) {
        gwaValue.text = "%.3f".format(gwa)
    }

    override fun showInput() {
       alert {
           title = "Add Course"
           val view = layoutInflater.inflate(R.layout.input_layout, null)
           customView = view
           positiveButton("Add"){
               val courseCode = view.courseCodeInput.text.toString()
               val units = view.unitInputDropDown.selectedItem.toString().toInt()
               val grade = view.gradesInputDropDown.selectedItem.toString().toDouble()

               mPresenter.addCourse(Course(courseCode, units, grade))
           }
           negativeButton("Cancel"){}
       }.show()
    }

    override fun showDeletePrompt(course: Course) {
        alert {
            title = "Delete Course?"
            positiveButton("Cancel"){}
            negativeButton("Delete"){
                mPresenter.removeCourse(course)
            }
        }.show()
    }

    override fun showTable(courses: MutableList<Course>) {
        courseList.layoutManager = LinearLayoutManager(this)
        courseList.adapter = RecyclerAdapter(courses) {}
    }
}
