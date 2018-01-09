package com.djacoronel.gwacalculator

import com.djacoronel.gwacalculator.model.Course

class Contract {

    interface View {
        fun showGrades(grades: Map<String, List<Course>>)
        fun updateGWA(gwa: Double)
        fun updateSEM(sem: Double)
        fun showOverwritePrompt(courses: List<Course>)
        fun showAddPrompt()
        fun showDeleteCoursePrompt(course: Course)
        fun showDeleteSemesterPrompt(semester: String)
        fun addSemesterRecycler(semester: String)
        fun removeSemesterRecycler(semester: String)
        fun addCourse(course: Course)
        fun updateCourse(course: Course)
        fun removeCourse(course: Course)
    }

    interface Actions {
        fun loadData()
        fun updateData(courses: List<Course>)
        fun replaceData(courses: List<Course>)
        fun computeGWA()
        fun computeSEM(semester: String)
        fun addCourse(course: Course)
        fun removeCourse(course: Course)
        fun updateCourse(course: Course)
        fun getCourses(semester: String): List<Course>
        fun getSemesters(): List<String>
        fun addSemester(semester: String)
        fun removeSemester(semester: String)
    }

    interface Repository {
        fun getCourse(courseCode: String): Course
        fun getAllCourse(): List<Course>
        fun addCourse(course: Course): Boolean
        fun removeCourse(course: Course)
        fun updateCourse(course: Course)
        fun getCourses(semester: String): List<Course>
        fun getSemesters(): List<String>
        fun addSemester(semester: String)
        fun removeSemester(semester: String)
    }
}
