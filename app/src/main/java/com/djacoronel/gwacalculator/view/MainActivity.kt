package com.djacoronel.gwacalculator.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputFilter
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import com.djacoronel.gwacalculator.R
import com.djacoronel.gwacalculator.model.Course
import com.djacoronel.gwacalculator.model.CourseRepository
import com.djacoronel.gwacalculator.presenter.GWACalcPresenter
import com.djacoronel.gwacalculator.utility.Contract
import com.djacoronel.gwacalculator.utility.MyUsteGradesFetcherTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.grade_selection_layout.view.*
import kotlinx.android.synthetic.main.gwa_layout.*
import kotlinx.android.synthetic.main.input_semester_layout.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity(), Contract.View {

    lateinit var mPresenter: Contract.Actions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPresenter = GWACalcPresenter(this, CourseRepository(this))
        mPresenter.computeGWA()

        fab.setOnClickListener { showAddPrompt() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewPager()
    }

    override fun addCourse(course: Course) {
        val recyclerAdapter = ((viewpager.adapter as ViewPagerAdapter)
                .getRecycler(tabs.selectedTabPosition)
                .adapter as RecyclerAdapter)

        val courses = recyclerAdapter.courses
        courses.add(course)
        recyclerAdapter.notifyItemInserted(courses.indexOf(course))
    }

    override fun removeCourse(course: Course) {
        val recyclerAdapter = ((viewpager.adapter as ViewPagerAdapter)
                .getRecycler(tabs.selectedTabPosition)
                .adapter as RecyclerAdapter)

        val courses = recyclerAdapter.courses
        recyclerAdapter.notifyItemRemoved(courses.indexOf(course))
        courses.remove(course)
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

        if (semesters.isNotEmpty())
            mPresenter.computeSEM()
        for (semester in semesters)
            adapter.addRecycler(setupRecycler(semester), semester)

        viewpager.adapter = adapter
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                mPresenter.computeSEM()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })

        tabs.setupWithViewPager(viewpager)
        setupTabLongClicks()
    }

    private fun setupRecycler(semester: String): RecyclerView {
        val courses = mPresenter.getCourses(semester)

        val semRecycler = RecyclerView(this)
        semRecycler.layoutManager = LinearLayoutManager(this)
        semRecycler.adapter = RecyclerAdapter(courses)

        val mDividerItemDecoration = DividerItemDecoration(
                semRecycler.context,
                (semRecycler.layoutManager as LinearLayoutManager).orientation
        )
        semRecycler.addItemDecoration(mDividerItemDecoration)

        val bottomPadding = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                80.toFloat(),
                resources.displayMetrics).toInt()
        semRecycler.setPadding(0, 0, 0, bottomPadding)
        semRecycler.clipToPadding = false

        return semRecycler
    }

    override fun addSemesterRecycler(semester: String) {
        (viewpager.adapter as ViewPagerAdapter).addRecycler(setupRecycler(semester), semester)
        setupTabLongClicks()

        viewpager.setCurrentItem(tabs.tabCount, true)
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

    fun showChangeGradePrompt(course: Course, gradeText: TextView) {
        val view = View.inflate(this, R.layout.grade_selection_layout, null)
        val alert = alert {
            customView = view
        }.show()

        view.grade_1.setOnClickListener { alert.dismiss(); setGrade(course, gradeText, view.grade_1.text) }
        view.grade_1_25.setOnClickListener { alert.dismiss(); setGrade(course, gradeText, view.grade_1_25.text) }
        view.grade_1_5.setOnClickListener { alert.dismiss(); setGrade(course, gradeText, view.grade_1_5.text) }
        view.grade_1_75.setOnClickListener { alert.dismiss(); setGrade(course, gradeText, view.grade_1_75.text) }
        view.grade_2.setOnClickListener { alert.dismiss(); setGrade(course, gradeText, view.grade_2.text) }
        view.grade_2_25.setOnClickListener { alert.dismiss(); setGrade(course, gradeText, view.grade_2_25.text) }
        view.grade_2_5.setOnClickListener { alert.dismiss(); setGrade(course, gradeText, view.grade_2_5.text) }
        view.grade_2_75.setOnClickListener { alert.dismiss(); setGrade(course, gradeText, view.grade_2_75.text) }
        view.grade_3.setOnClickListener { alert.dismiss(); setGrade(course, gradeText, view.grade_3.text) }
        view.grade_5.setOnClickListener { alert.dismiss(); setGrade(course, gradeText, view.grade_5.text) }
    }

    private fun setGrade(course: Course, gradeText: TextView, newGrade: CharSequence) {
        course.grade = newGrade.toString().toDouble()
        gradeText.text = newGrade
        mPresenter.updateCourse(course)
    }

    override fun showAddPrompt() {
        val fetchGradesLabel = "Fetch MyUste grades"
        val addCourseLabel = getString(R.string.add_course_label)
        val addSemesterLabel = getString(R.string.add_semester_label)
        val addChoices = listOf(fetchGradesLabel, addCourseLabel, addSemesterLabel)

        selector(getString(R.string.add_prompt_title), addChoices, { _, i ->
            if (i == addChoices.indexOf(addSemesterLabel))
                showAddSemester()
            else if (i == addChoices.indexOf(addCourseLabel))
                if (mPresenter.getSemesters().isNotEmpty())
                    showAddCourse()
                else
                    toast(getString(R.string.add_semester_warning))
            else if (i == addChoices.indexOf(fetchGradesLabel))
                showLogin()
        })
    }

    private fun showLogin() {
        val I = Intent(this, LoginActivity::class.java)
        startActivityForResult(I, 2)
    }

    override fun showAddCourse() {
        val I = Intent(this, AddCourseActivity::class.java)
        startActivityForResult(I, 1)
    }

    override fun showAddSemester() {
        val view = View.inflate(this, R.layout.input_semester_layout, null)

        //forces all cap in input
        val filters = view.semesterInput.filters.toMutableList()
        filters.add(InputFilter.AllCaps())
        view.semesterInput.filters = filters.toTypedArray()

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        alert {
            title = getString(R.string.add_semester_label)
            customView = view
            positiveButton(getString(R.string.add_button_label)) {
                imm.hideSoftInputFromWindow(view.semesterInput.windowToken, 0)
                val semester = view.semesterInput.text.toString()
                if (semester == "") {
                    toast(getString(R.string.blank_sem_label_warning))
                    showAddSemester()
                } else if (!mPresenter.addSemester(semester)) {
                    toast(getString(R.string.add_semester_label_warning))
                    showAddSemester()
                }
            }
            negativeButton(getString(R.string.cancel_button_label)) {
                imm.hideSoftInputFromWindow(view.semesterInput.windowToken, 0)
            }
        }.show()
    }

    override fun showDeleteCoursePrompt(course: Course) {
        alert {
            title = getString(R.string.delete_course_title)
            positiveButton(R.string.cancel_button_label) {}
            negativeButton(getString(R.string.delete_button_label)) {
                mPresenter.removeCourse(course)
            }
        }.show()
    }

    override fun showDeleteSemesterPrompt(semester: String) {
        alert {
            title = getString(R.string.delete_semester_title)
            positiveButton(R.string.cancel_button_label) {}
            negativeButton(R.string.delete_button_label) {
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

                mPresenter.addCourse(Course(0, courseCode, units, grade, semester))
                mPresenter.computeSEM()
            }
        } else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val studNo = data.getStringExtra("studNo")
                val password = data.getStringExtra("password")

                MyUsteGradesFetcherTask(mPresenter, this).execute(studNo, password)
            }
        }
    }
}
