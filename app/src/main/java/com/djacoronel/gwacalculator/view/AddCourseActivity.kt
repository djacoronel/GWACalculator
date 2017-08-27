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
import kotlinx.android.synthetic.main.activity_add_course.*
import kotlinx.android.synthetic.main.grade_selection_layout.*
import kotlinx.android.synthetic.main.units_selection_layout.*
import org.jetbrains.anko.toast

class AddCourseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        //forces all cap in input
        val filters = courseCodeInput.filters.toMutableList()
        filters.add(InputFilter.AllCaps())
        courseCodeInput.filters = filters.toTypedArray()

        val addCourseButton = add_course_button
        addCourseButton.setOnClickListener { onAddButtonPressed() }

        courseCodeInput.setOnKeyListener({ _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                onAddButtonPressed()
            }
            false
        })

        textViewBindings()
        units_1.setBackgroundResource(R.drawable.circle_highlight)
        grade_1.setBackgroundResource(R.drawable.circle_highlight)

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
    }

    private fun onAddButtonPressed() {
        val courseCode = courseCodeInput.text.toString()

        if (courseCode != "") {
            val returnIntent = Intent()
            returnIntent.putExtra("courseCodeInput", courseCode)
            returnIntent.putExtra("unitsInput", selectedUnits)
            returnIntent.putExtra("gradeInput", selectedGrade)

            //Hide the keyboard on finish activity
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(courseCodeInput.windowToken, 0)

            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        } else
            toast("Input course code")
    }

    private var selectedUnits = 1
    private var selectedGrade = 1.0

    private fun setSelectedUnits(selected: TextView) {
        selected.setBackgroundResource(R.drawable.circle_highlight)
        selectedUnits = selected.text.toString().toInt()

        if (selected != units_1) units_1.setBackgroundResource(0)
        if (selected != units_2) units_2.setBackgroundResource(0)
        if (selected != units_3) units_3.setBackgroundResource(0)
        if (selected != units_4) units_4.setBackgroundResource(0)
        if (selected != units_5) units_5.setBackgroundResource(0)
    }

    private fun setSelectedGrade(selected: TextView) {
        selected.setBackgroundResource(R.drawable.circle_highlight)
        selectedGrade = selected.text.toString().toDouble()

        if (grade_1 != selected) grade_1.setBackgroundResource(0)
        if (grade_1_25 != selected) grade_1_25.setBackgroundResource(0)
        if (grade_1_5 != selected) grade_1_5.setBackgroundResource(0)
        if (grade_1_75 != selected) grade_1_75.setBackgroundResource(0)
        if (grade_2 != selected) grade_2.setBackgroundResource(0)
        if (grade_2_25 != selected) grade_2_25.setBackgroundResource(0)
        if (grade_2_5 != selected) grade_2_5.setBackgroundResource(0)
        if (grade_2_75 != selected) grade_2_75.setBackgroundResource(0)
        if (grade_3 != selected) grade_3.setBackgroundResource(0)
        if (grade_5 != selected) grade_5.setBackgroundResource(0)
    }

    private fun textViewBindings() {
        units_1.setOnClickListener { setSelectedUnits(units_1) }
        units_2.setOnClickListener { setSelectedUnits(units_2) }
        units_3.setOnClickListener { setSelectedUnits(units_3) }
        units_4.setOnClickListener { setSelectedUnits(units_4) }
        units_5.setOnClickListener { setSelectedUnits(units_5) }

        grade_1.setOnClickListener { setSelectedGrade(grade_1) }
        grade_1_25.setOnClickListener { setSelectedGrade(grade_1_25) }
        grade_1_5.setOnClickListener { setSelectedGrade(grade_1_5) }
        grade_1_75.setOnClickListener { setSelectedGrade(grade_1_75) }
        grade_2.setOnClickListener { setSelectedGrade(grade_2) }
        grade_2_25.setOnClickListener { setSelectedGrade(grade_2_25) }
        grade_2_5.setOnClickListener { setSelectedGrade(grade_2_5) }
        grade_2_75.setOnClickListener { setSelectedGrade(grade_2_75) }
        grade_3.setOnClickListener { setSelectedGrade(grade_3) }
        grade_5.setOnClickListener { setSelectedGrade(grade_5) }
    }
}
