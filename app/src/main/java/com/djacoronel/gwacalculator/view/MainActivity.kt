package com.djacoronel.gwacalculator.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputFilter
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.R
import com.djacoronel.gwacalculator.model.Course
import com.djacoronel.gwacalculator.model.Semester
import com.djacoronel.gwacalculator.utility.MyUsteGradesFetcherTask
import com.google.android.gms.ads.AdRequest
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.grade_selection_layout.view.*
import kotlinx.android.synthetic.main.gwa_layout.*
import kotlinx.android.synthetic.main.input_semester_layout.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import javax.inject.Inject


class MainActivity : AppCompatActivity(), Contract.View {
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    @Inject
    lateinit var mPresenter: Contract.Actions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidInjection.inject(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp)
        fab.setOnClickListener { showAddPrompt() }
        setupNavView()
        setupViewPager()
        setupAds()

        mPresenter.loadData()
    }


    private fun showAddPrompt() {
        val fetchGradesLabel = "Fetch MyUste grades"
        val addCourseLabel = getString(R.string.add_course_label)
        val addSemesterLabel = getString(R.string.add_semester_label)
        val addChoices = listOf(fetchGradesLabel, addCourseLabel, addSemesterLabel)

        selector(getString(R.string.add_prompt_title), addChoices, { _, i ->
            if (i == addChoices.indexOf(fetchGradesLabel))
                showLogin()
            else if (i == addChoices.indexOf(addSemesterLabel))
                showAddSemester()
            else if (i == addChoices.indexOf(addCourseLabel))
                if (mPresenter.getSemesters().isNotEmpty())
                    showAddCourse()
                else
                    toast(getString(R.string.add_semester_warning))
        })
    }

    private fun showAddSemester() {
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
                } else {
                    if (mPresenter.getSemesters().find { it.title == semester } == null) {
                        mPresenter.addSemester(Semester(0, semester))
                        setupNavView()
                    } else
                        toast(getString(R.string.add_semester_label_warning))
                }
            }
            negativeButton(getString(R.string.cancel_button_label)) {
                imm.hideSoftInputFromWindow(view.semesterInput.windowToken, 0)
            }
        }.show()
    }

    private fun showLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, 2)
    }

    private fun showAddCourse() {
        val intent = Intent(this, AddCourseActivity::class.java)
        startActivityForResult(intent, 1)
    }



    private fun setupNavView() {
        val menu = nav_view.menu
        val semesters = mPresenter.getSemesters()
        menu.clear()

        for (i in semesters.indices) {
            val item = menu.add(semesters[i].title)
            item.setActionView(R.layout.menu_image)
            item.setOnMenuItemClickListener {
                viewpager.setCurrentItem(i, true)
                drawer_layout.closeDrawers()
                false
            }
            item.actionView.setOnClickListener { showEditPrompt(semesters[i]) }
        }
    }

    private fun showEditPrompt(semester: Semester) {
        alert {
            title = "Rename or delete semester"
            positiveButton("Rename") { showRenameSemPrompt(semester) }
            negativeButton("Delete") { showDeleteSemesterPrompt(semester) }
        }.show()
    }

    private fun showRenameSemPrompt(semester: Semester) {
        val view = View.inflate(this, R.layout.input_semester_layout, null)
        view.semesterInput.setText(semester.title)

        //forces all cap in input
        val filters = view.semesterInput.filters.toMutableList()
        filters.add(InputFilter.AllCaps())
        view.semesterInput.filters = filters.toTypedArray()

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        alert {
            title = "Rename semester?"
            customView = view
            positiveButton("Save") {
                imm.hideSoftInputFromWindow(view.semesterInput.windowToken, 0)
                val newSemesterLabel = view.semesterInput.text.toString()
                if (newSemesterLabel == "") {
                    toast(getString(R.string.blank_sem_label_warning))
                    showAddSemester()
                } else {
                    if (mPresenter.getSemesters().find { it.title == newSemesterLabel } == null) {
                        semester.title = newSemesterLabel
                        mPresenter.updateSemester(semester)

                        tabs.getTabAt(mPresenter.getSemesters().indexOf(semester))?.text = semester.title
                        setupNavView()
                    } else
                        toast(getString(R.string.add_semester_label_warning))
                }
            }
            negativeButton(getString(R.string.cancel_button_label)) {
                imm.hideSoftInputFromWindow(view.semesterInput.windowToken, 0)
            }
        }.show()
    }

    private fun showDeleteSemesterPrompt(semester: Semester) {
        alert {
            title = getString(R.string.delete_semester_title)
            positiveButton(R.string.cancel_button_label) {}
            negativeButton(R.string.delete_button_label) {
                mPresenter.removeSemester(semester)
            }
        }.show()
    }



    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter()
        viewpager.adapter = viewPagerAdapter
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                this@MainActivity.onPageSelected(position)
            }
        })

        tabs.setupWithViewPager(viewpager)
    }

    private fun onPageSelected(position: Int) {
        if (position == -1)
            mPresenter.computeSEM(-1)
        else {
            val sem = (viewpager.adapter as ViewPagerAdapter)
                    .getPageSemester(viewpager.currentItem)

            mPresenter.computeSEM(sem.id)
        }
        setMessageVisibility()
    }

    override fun setMessageVisibility() {
        if (mPresenter.getSemesters().isEmpty()) {
            message.visibility = View.VISIBLE
            tiger_no_sems.visibility = View.VISIBLE
            tiger_no_course.visibility = View.INVISIBLE
        } else {
            message.visibility = View.INVISIBLE
            tiger_no_sems.visibility = View.INVISIBLE

            val sem = (viewpager.adapter as ViewPagerAdapter)
                    .getPageSemester(viewpager.currentItem)

            if (mPresenter.getCourses(sem).isEmpty())
                tiger_no_course.visibility = View.VISIBLE
            else
                tiger_no_course.visibility = View.INVISIBLE
        }
    }


    override fun showGrades(grades: Map<Semester, List<Course>>) {
        for (semester in grades.keys) {
            val adapter = RecyclerAdapter()
            grades[semester]?.let { adapter.addNewCourses(it) }

            val recycler = createRecycler()
            recycler.adapter = adapter
            viewPagerAdapter.addRecycler(recycler, semester)
        }
    }

    private fun createRecycler(): RecyclerView {
        val semRecycler = RecyclerView(this)
        semRecycler.layoutManager = LinearLayoutManager(this)
        semRecycler.adapter = RecyclerAdapter()

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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val courseCode = data.getStringExtra("courseCodeInput")
                val units = data.getDoubleExtra("unitsInput", 0.0)
                val grade = data.getDoubleExtra("gradeInput", 0.0)
                val semester = (viewpager.adapter as ViewPagerAdapter).getPageSemester(viewpager.currentItem)

                mPresenter.addCourse(Course(0, courseCode, units, grade, semester.id))
                mPresenter.computeSEM(semester.id)
            }
        } else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val cookie = data.getStringExtra("cookie")
                MyUsteGradesFetcherTask(this, this).execute(cookie)
            }
        }
    }

    override fun showOverwritePrompt(grades: LinkedHashMap<String, ArrayList<Course>>) {
        val storedSems = mPresenter.getSemesters()
        if (storedSems.isEmpty()) {
            mPresenter.replaceData(grades)
        } else {
            alert {
                title = "Update or replace old data?"
                positiveButton("Update") {
                    mPresenter.updateData(grades)
                    toast("Data updated!")
                }
                negativeButton("Replace") {
                    mPresenter.replaceData(grades)
                    toast("Data replaced!")
                }
            }.show()
        }
    }


    override fun updateGWA(gwa: Double) {
        gwaValue.text = getString(R.string.gwa_format).format(gwa)
    }

    override fun updateSEM(sem: Double) {
        semValue.text = getString(R.string.gwa_format).format(sem)
    }

    override fun addCourse(course: Course) {
        val recyclerAdapter = viewPagerAdapter
                .getRecyclerAdapter(tabs.selectedTabPosition)

        recyclerAdapter.addCourse(course)
    }

    override fun updateCourse(course: Course) {
        val recyclerAdapter = viewPagerAdapter
                .getRecyclerAdapter(tabs.selectedTabPosition)

        recyclerAdapter.updateCourse(course)
    }

    override fun removeCourse(course: Course) {
        val recyclerAdapter = viewPagerAdapter
                .getRecyclerAdapter(tabs.selectedTabPosition)

        recyclerAdapter.removeCourse(course)
    }

    override fun addSemesterRecycler(semester: Semester) {
        viewPagerAdapter.addRecycler(createRecycler(), semester)
        viewpager.setCurrentItem(viewPagerAdapter.count, true)
        setupNavView()
    }

    override fun removeSemesterRecycler(semester: Semester) {
        val nextPosition = viewPagerAdapter.removeRecycler(viewpager, semester)
        onPageSelected(nextPosition)
        setupNavView()
    }


    fun showDeleteCoursePrompt(course: Course) {
        alert {
            title = getString(R.string.delete_course_title)
            positiveButton(R.string.cancel_button_label) {}
            negativeButton(getString(R.string.delete_button_label)) {
                mPresenter.removeCourse(course)
            }
        }.show()
    }


    fun showChangeGradePrompt(course: Course) {
        val view = View.inflate(this, R.layout.grade_selection_layout, null)
        val alert = alert { customView = view }.show()

        val gradeViews = listOf(view.grade_1, view.grade_1_25, view.grade_1_5, view.grade_1_75, view.grade_2,
                view.grade_2_25, view.grade_2_5, view.grade_2_75, view.grade_3, view.grade_5)

        for (gradeView in gradeViews) {
            gradeView.setOnClickListener { alert.dismiss(); setGrade(course, gradeView.text) }
        }
    }


    private fun setGrade(course: Course, newGrade: CharSequence) {
        course.grade = newGrade.toString().toDouble()
        mPresenter.updateCourse(course)
    }

    private fun setupAds() {
        val adRequest = AdRequest.Builder()
                .addTestDevice("CEA54CA528FB019B75536189748EAF7E")
                .addTestDevice("4CCC112819318A806ADC4807B6A0C444")
                .build()
        main_adView.loadAd(adRequest)
    }

    public override fun onPause() {
        super.onPause()
        main_adView.pause()
    }

    public override fun onResume() {
        super.onResume()
        main_adView.resume()
    }

    public override fun onDestroy() {
        super.onDestroy()
        main_adView.destroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
