package com.djacoronel.gwacalculator

import com.djacoronel.gwacalculator.model.Course
import com.djacoronel.gwacalculator.model.Semester

class Contract {

    interface View {
        fun showGrades(grades: Map<Semester, List<Course>>)
        fun updateGWA(gwa: Double)
        fun updateSEM(sem: Double)
        fun showOverwritePrompt(grades: LinkedHashMap<String, ArrayList<Course>>)
        fun showAddPrompt()
        fun showDeleteCoursePrompt(course: Course)
        fun showDeleteSemesterPrompt(semester: Semester)
        fun addSemesterRecycler(semester: Semester)
        fun removeSemesterRecycler(semester: Semester)
        fun addCourse(course: Course)
        fun updateCourse(course: Course)
        fun removeCourse(course: Course)
        fun setMessageVisibility()
    }

    interface Actions {
        fun loadData()
        fun updateData(grades: LinkedHashMap<String, ArrayList<Course>>)
        fun replaceData(grades: LinkedHashMap<String, ArrayList<Course>>)
        fun computeGWA()
        fun computeSEM(semesterId: Int)
        fun addCourse(course: Course)
        fun removeCourse(course: Course)
        fun updateCourse(course: Course)
        fun getCourses(semester: Semester): List<Course>
        fun getSemesters(): List<Semester>
        fun addSemester(semester: Semester)
        fun updateSemester(semester: Semester)
        fun removeSemester(semester: Semester)
    }

    interface Repository {
        fun getCourse(courseCode: String): Course
        fun getAllCourse(): List<Course>
        fun addCourse(course: Course): Boolean
        fun removeCourse(course: Course)
        fun updateCourse(course: Course)
        fun getCourses(semesterId: Int): List<Course>
        fun getSemester(semTitle: String): Semester
        fun getSemesters(): List<Semester>
        fun addSemester(semester: Semester)
        fun updateSemester(semester: Semester)
        fun removeSemester(semester: Semester)
    }
}
