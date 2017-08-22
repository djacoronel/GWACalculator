package com.djacoronel.gwacalculator.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

        val addCourseButton = add_course_button
        addCourseButton.setOnClickListener { getInputValues() }

        textViewBindings()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
    }

    fun getInputValues() {
        val courseCode = semesterInput.text.toString()

        if (selectedUnits != 0 && selectedGrade != 0.0 && courseCode != "") {
            val returnIntent = Intent()
            returnIntent.putExtra("courseCodeInput", courseCode)
            returnIntent.putExtra("unitsInput", selectedUnits)
            returnIntent.putExtra("gradeInput", selectedGrade)

            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        } else {
            var userWarning = "Select course "

            if (selectedUnits == 0 && selectedGrade == 0.0 && courseCode == "")
                userWarning = "Provide course info"
            else if (courseCode == "")
                userWarning = "Input course code"
            else if (selectedUnits == 0)
                userWarning += "units"
            else if (selectedGrade == 0.0)
                userWarning += "grade"

            toast(userWarning)
        }
    }

    var selectedUnits = 0
    var selectedGrade = 0.0

    fun setSelectedUnits(selected: TextView) {
        selected.setBackgroundResource(R.drawable.circle_highlight)
        selectedUnits = selected.text.toString().toInt()

        if (selected != units_1) units_1.setBackgroundResource(0)
        if (selected != units_2) units_2.setBackgroundResource(0)
        if (selected != units_3) units_3.setBackgroundResource(0)
        if (selected != units_4) units_4.setBackgroundResource(0)
        if (selected != units_5) units_5.setBackgroundResource(0)
    }

    fun setSelectedGrade(selected: TextView) {
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

    fun textViewBindings() {
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
