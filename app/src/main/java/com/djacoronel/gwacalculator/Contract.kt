package com.djacoronel.gwacalculator

import com.djacoronel.gwacalculator.Model.Course


class Contract{

    interface View{
        fun showTable(courses: MutableList<Course>)
        fun updateGWA(gwa: Double)
        fun showInput()
    }

    interface Actions{
        fun computeGWA(courses: MutableList<Course>)
        fun loadCourses()
        fun getCourse(courseCode: String): Course
        fun addCourse(course: Course)
        fun removeCourse(course: Course)
        fun updateCourse(course: Course)
        fun onAddCourseClick(course: Course)
        fun onChangeGrade(course: Course)
    }

    interface Repository{
        fun getCourse(courseCode: String): Course
        fun getAllCourse(): MutableList<Course>
        fun addCourse(course: Course): Boolean
        fun removeCourse(course: Course)
        fun updateCourse(course: Course)
    }
}
