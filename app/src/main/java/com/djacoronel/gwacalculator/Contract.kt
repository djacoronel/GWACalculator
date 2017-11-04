package com.djacoronel.gwacalculator

import com.djacoronel.gwacalculator.model.Course

class Contract {

    interface View {
        fun updateGWA(gwa: Double)
        fun updateSEM(sem: Double)
        fun showAddPrompt()
        fun showDeleteCoursePrompt(course: Course)
        fun showDeleteSemesterPrompt(semester: String)
        fun addSemesterRecycler(semester: String)
        fun removeSemesterRecycler(semester: String)
        fun addCourse(course: Course)
        fun removeCourse(course: Course)
    }

    interface Actions {
        fun computeGWA()
        fun computeSEM()
        fun addCourse(course: Course)
        fun removeCourse(course: Course)
        fun updateCourse(course: Course)
        fun getCourses(semester: String): MutableList<Course>
        fun getSemesters(): MutableList<String>
        fun addSemester(semester: String): Boolean
        fun removeSemester(semester: String)
    }

    interface Repository {
        fun getCourse(courseCode: String): Course
        fun getAllCourse(): MutableList<Course>
        fun addCourse(course: Course): Boolean
        fun removeCourse(course: Course)
        fun updateCourse(course: Course)
        fun getCourses(semester: String): MutableList<Course>
        fun getSemesters(): MutableList<String>
        fun addSemester(semester: String)
        fun removeSemester(semester: String)
    }
}
