package com.djacoronel.gwacalculator.View

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.djacoronel.gwacalculator.Model.Course
import com.djacoronel.gwacalculator.R
import kotlinx.android.synthetic.main.row_layout.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener


class RecyclerAdapter(val courses: MutableList<Course>, val listener: (Course) -> Unit) :
        RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    //extension function to simplify view inflation in an adapter
    fun ViewGroup.inflate(layoutRes: Int): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.row_layout))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(courses[position], listener)

    override fun getItemCount() = courses.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(course: Course, listener: (Course) -> Unit) = with(itemView) {
            courseCodeText.text = course.courseCode

            val units = course.units
            unitsText.text = "${units.toString()} ${if (units == 1) "unit" else "units"}"

            setOnLongClickListener {
                (context as MainActivity).showDeletePrompt(course)
                true
            }

            val grades = resources.getStringArray(R.array.grade_array)
            gradesDropDown.setSelection(grades.indexOf(course.grade.toString()))
            gradesDropDown.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(parentView: AdapterView<*>, view: View,
                                            position: Int, id: Long) {
                    course.grade = parentView.getItemAtPosition(position).toString().toDouble()
                    (context as MainActivity).mPresenter.updateCourse(course)
                    (context as MainActivity).mPresenter.computeGWA()
                }
            }
        }
    }
}
