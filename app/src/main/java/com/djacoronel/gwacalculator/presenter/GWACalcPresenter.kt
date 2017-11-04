package com.djacoronel.gwacalculator.presenter

import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.model.Course


class GWACalcPresenter(val view: Contract.View, private val repo: Contract.Repository) : Contract.Actions {

    override fun computeGWA() {
        val courses = repo.getAllCourse().filter { it.grade != 0.0 }
        var gwa = 0.0

        if (courses.isNotEmpty()) {
            val sum = courses.sumByDouble { it.grade * it.units }
            val totalUnits = courses.sumBy { it.units }
            gwa = sum / totalUnits
        }

        view.updateGWA(gwa)
    }

    override fun computeSEM(semester: String) {
        var sem = 0.0


        val courses = repo.getCourses(semester).filter { it.grade != 0.0 }

        if (courses.isNotEmpty()) {
            val sum = courses.sumByDouble { it.grade * it.units }
            val totalUnits = courses.sumBy { it.units }
            sem = sum / totalUnits
        }

        view.updateSEM(sem)
    }

    override fun getCourses(semester: String): MutableList<Course> {
        return repo.getCourses(semester)
    }

    override fun addCourse(course: Course) {
        repo.addCourse(course)
        view.addCourse(course)
        computeGWA()
        computeSEM(course.semester)
    }

    override fun removeCourse(course: Course) {
        repo.removeCourse(course)
        view.removeCourse(course)
        computeGWA()
        computeSEM(course.semester)
    }

    override fun updateCourse(course: Course) {
        repo.updateCourse(course)
        computeGWA()
        computeSEM(course.semester)
    }

    override fun getSemesters(): MutableList<String> {
        return repo.getSemesters()
    }

    override fun addSemester(semester: String): Boolean {
        val semesters = getSemesters()

        return if (semester in semesters)
            false
        else {
            repo.addSemester(semester)
            view.addSemesterRecycler(semester)
            true
        }
    }

    override fun removeSemester(semester: String) {
        repo.removeSemester(semester)
        view.removeSemesterRecycler(semester)
        computeGWA()
    }
}