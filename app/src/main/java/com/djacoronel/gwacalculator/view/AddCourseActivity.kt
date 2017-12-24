package com.djacoronel.gwacalculator.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.djacoronel.gwacalculator.R
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_add_course.*
import kotlinx.android.synthetic.main.grade_selection_layout.*
import kotlinx.android.synthetic.main.units_selection_layout.*
import org.jetbrains.anko.toast


class AddCourseActivity : AppCompatActivity() {

    private lateinit var gradeViews: List<TextView>
    private lateinit var unitViews: List<TextView>
    private var selectedUnits = 0
    private var selectedGrade = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        add_course_button.setOnClickListener { saveInputsAndReturn() }

        addViewsToList()
        setupTextViewBindings()
        setDefaultSelected()
        setupAds()
        addAllCapsFilterInInput()
        setAddCourseOnEnter()
        showKeyBoard()
    }

    private fun addViewsToList() {
        unitViews = listOf(units_1, units_2, units_3, units_4, units_5)
        gradeViews = listOf(grade_1, grade_1_25, grade_1_5, grade_1_75, grade_2,
                grade_2_25, grade_2_5, grade_2_75, grade_3, grade_5)
    }

    private fun setDefaultSelected() {
        setSelectedUnits(units_1)
        setSelectedGrade(grade_1)
    }

    private fun setupAds() {
        val adRequest = AdRequest.Builder()
                .addTestDevice("CEA54CA528FB019B75536189748EAF7E")
                .build()
        adView.loadAd(adRequest)
    }

    private fun addAllCapsFilterInInput() {
        val filters = course_code_input.filters.toMutableList()
        filters.add(InputFilter.AllCaps())
        course_code_input.filters = filters.toTypedArray()
    }

    private fun setAddCourseOnEnter() {
        course_code_input.setOnKeyListener({ _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                saveInputsAndReturn()
            }
            false
        })
    }

    private fun showKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun setupTextViewBindings() {
        for (unitView in unitViews) {
            unitView.setOnClickListener { setSelectedUnits(unitView) }
        }

        for (gradeView in gradeViews) {
            gradeView.setOnClickListener { setSelectedGrade(gradeView) }
        }
    }

    private fun setSelectedUnits(selected: TextView) {
        selected.setBackgroundResource(R.drawable.circle_highlight)
        selectedUnits = selected.text.toString().toInt()

        unitViews
                .filter { it != selected }
                .forEach { it.setBackgroundResource(0) }
    }

    private fun setSelectedGrade(selected: TextView) {
        selected.setBackgroundResource(R.drawable.circle_highlight)
        selectedGrade = selected.text.toString().toDouble()

        gradeViews
                .filter { it != selected }
                .forEach { it.setBackgroundResource(0) }
    }

    private fun saveInputsAndReturn() {
        val courseCode = course_code_input.text.toString()

        if (courseCode != "") {
            val returnIntent = Intent()
            returnIntent.putExtra("courseCodeInput", courseCode)
            returnIntent.putExtra("unitsInput", selectedUnits)
            returnIntent.putExtra("gradeInput", selectedGrade)

            //Hide the keyboard on finish activity
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(course_code_input.windowToken, 0)

            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        } else
            toast("Input course code")
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
    }

    public override fun onPause() {
        super.onPause()
        adView.pause()
    }

    public override fun onResume() {
        super.onResume()
        adView.resume()

    }

    public override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }
}
