package com.djacoronel.gwacalculator.presenter

import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.model.Course


class GWACalcPresenter(val view: Contract.View, private val repo: Contract.Repository) : Contract.Actions {

    override fun loadData() {
        val semesters = repo.getSemesters()
        val grades = linkedMapOf<String, List<Course>>()

        semesters.forEach { grades.put(it, repo.getCourses(it)) }

        view.showGrades(grades)
        computeGWA()
    }

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

    override fun getCourses(semester: String): List<Course> {
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

    override fun getSemesters(): List<String> {
        return repo.getSemesters()
    }

    override fun addSemester(semester: String) {
        val semesters = getSemesters()

        if (semester !in semesters) {
            repo.addSemester(semester)
            view.addSemesterRecycler(semester)
        }
    }

    override fun removeSemester(semester: String) {
        repo.removeSemester(semester)
        view.removeSemesterRecycler(semester)
        computeGWA()
    }
}