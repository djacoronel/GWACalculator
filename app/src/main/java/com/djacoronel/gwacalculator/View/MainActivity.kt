package com.djacoronel.gwacalculator.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.Model.Course
import com.djacoronel.gwacalculator.Model.CourseRepository
import com.djacoronel.gwacalculator.Presenter.GWACalcPresenter
import com.djacoronel.gwacalculator.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.gwa_layout.*
import kotlinx.android.synthetic.main.input_semester_layout.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.selector

class MainActivity : AppCompatActivity(), Contract.View {

    lateinit var mPresenter: Contract.Actions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPresenter = GWACalcPresenter(this, CourseRepository(this))

        fab.setOnClickListener { showAddPrompt() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewPager()
    }

    override fun reloadCourseList(courses: MutableList<Course>) {
        val recyclerAdapter = RecyclerAdapter(courses) {}
        (viewpager.adapter as ViewPagerAdapter)
                .getRecycler(tabs.selectedTabPosition)
                .adapter = recyclerAdapter
    }

    private fun setupTabLongClicks() {
        val tabStrip = tabs.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setOnLongClickListener {
                showDeleteSemesterPrompt(
                        viewpager.adapter.getPageTitle(i) as String
                )
                false
            }
        }
    }

    override fun setupViewPager() {
        val adapter = ViewPagerAdapter()

        val semesters = mPresenter.getSemesters()
        for (semester in semesters) {
            adapter.addRecycler(setupRecycler(semester), semester)
        }

        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                mPresenter.computeSEM(position)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })

        viewpager.adapter = adapter
        mPresenter.computeSEM(viewpager.currentItem)
        tabs.setupWithViewPager(viewpager, true)
        setupTabLongClicks()
    }

    fun setupRecycler(semester: String): RecyclerView {
        val courses = mPresenter.getCourses(semester)
        val semRecycler = RecyclerView(this)
        semRecycler.layoutManager = LinearLayoutManager(this)
        semRecycler.adapter = RecyclerAdapter(courses) {}

        return semRecycler
    }

    override fun addSemesterRecycler(semester: String) {
        (viewpager.adapter as ViewPagerAdapter).addRecycler(setupRecycler(semester), semester)
        setupTabLongClicks()
    }

    override fun removeSemesterRecycler(semester: String) {
        (viewpager.adapter as ViewPagerAdapter).removeRecycler(viewpager, semester)
        setupTabLongClicks()
    }

    override fun updateGWA(gwa: Double) {
        gwaValue.text = getString(R.string.gwa_format).format(gwa)
    }

    override fun updateSEM(sem: Double) {
        semValue.text = getString(R.string.gwa_format).format(sem)
    }

    override fun showAddPrompt() {
        val addChoices = listOf("Add Semester", "Add Course")
        selector("Which do you want to add?", addChoices, { _, i ->
            if (i == addChoices.indexOf("Add Semester"))
                showAddSemester()
            else if (i == addChoices.indexOf("Add Course"))
                showAddCourse()
        })
    }

    override fun showAddCourse() {
        val I = Intent(this, AddCourseActivity::class.java)
        startActivityForResult(I, 1)
    }

    override fun showAddSemester() {
        alert {
            title = "Add Semester"
            val view = layoutInflater.inflate(R.layout.input_semester_layout, null)
            customView = view
            positiveButton("Add") {
                val semester = view.semesterInput.text.toString()

                mPresenter.addSemester(semester)
            }
            negativeButton("Cancel") {}
        }.show()
    }

    override fun showDeleteCoursePrompt(course: Course) {
        alert {
            title = "Delete Course?"
            positiveButton("Cancel") {}
            negativeButton("Delete") {
                mPresenter.removeCourse(course)
            }
        }.show()
    }

    override fun showDeleteSemesterPrompt(semester: String) {
        alert {
            title = "Delete Semester?"
            positiveButton("Cancel") {}
            negativeButton("Delete") {
                mPresenter.removeSemester(semester)
            }
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val courseCode = data.getStringExtra("courseCodeInput")
                val units = data.getIntExtra("unitsInput", 0)
                val grade = data.getDoubleExtra("gradeInput", 0.0)
                val semester = viewpager.adapter.getPageTitle(viewpager.currentItem) as String

                mPresenter.addCourse(Course(courseCode, units, grade, semester))
                mPresenter.computeSEM(viewpager.currentItem)
            }
        } else if (requestCode == 2) {

        }
    }
}
