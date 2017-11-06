package com.djacoronel.gwacalculator.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.djacoronel.gwacalculator.R
import com.djacoronel.gwacalculator.model.Course
import kotlinx.android.synthetic.main.row_layout.view.*


class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    val courses = mutableListOf<Course>()

    //extension function to simplify view inflation in an adapter
    private fun ViewGroup.inflate(layoutRes: Int): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.row_layout))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(courses[position])

    override fun getItemCount() = courses.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(course: Course) = with(itemView) {
            courseCodeText.text = course.courseCode

            val units = course.units
            val unitLabel = "$units ${if (units == 1) "unit" else "units"}"
            unitsText.text = unitLabel

            setOnLongClickListener {
                (context as MainActivity).showDeleteCoursePrompt(course)
                true
            }

            gradesEdit.text = course.grade.toString()
            gradesEdit.setOnClickListener {
                (context as MainActivity).showChangeGradePrompt(course, gradesEdit)
            }
        }
    }

    fun addNewCourses(newCourses: List<Course>) {
        courses.clear()
        courses.addAll(newCourses)
    }

    fun addCourse(course: Course) {
        courses.add(course)
    }

    fun removeCourse(course: Course) {
        courses.remove(course)
    }
}
