package com.djacoronel.gwacalculator.Presenter

import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.Model.Course


class GWACalcPresenter(val view: Contract.View, val repo: Contract.Repository) : Contract.Actions {

    override fun computeGWA() {
        val courses = repo.getAllCourse()
        val sum = courses.sumByDouble { it.grade * it.units }
        val totalUnits = courses.sumBy { it.units }
        var gwa = sum / totalUnits

        if (gwa.isNaN())
            gwa = 0.0

        view.updateGWA(gwa)
    }

    override fun computeSEM(position: Int) {
        val semesters = repo.getSemesters()
        val courses = repo.getCourses(semesters[position])
        val sum = courses.sumByDouble { it.grade * it.units }
        val totalUnits = courses.sumBy { it.units }
        var sem = sum / totalUnits

        if (sem.isNaN())
            sem = 0.0

        view.updateSEM(sem)
    }

    override fun onChangeGrade(course: Course) {
        updateCourse(course)
    }

    override fun onAddCourseClick(course: Course) {
        view.showAddCourse()
    }

    override fun getCourses(semester: String): MutableList<Course> {
        return repo.getCourses(semester)
    }

    override fun addCourse(course: Course) {
        repo.addCourse(course)
        view.reloadCourseList(getCourses(course.semester))
        computeGWA()
    }

    override fun removeCourse(course: Course) {
        repo.removeCourse(course)
        view.reloadCourseList(getCourses(course.semester))
        computeGWA()
    }

    override fun getCourse(courseCode: String): Course {
        return repo.getCourse(courseCode)
    }

    override fun updateCourse(course: Course) {
        repo.updateCourse(course)
    }

    override fun getSemesters(): MutableList<String> {
        return repo.getSemesters()
    }

    override fun addSemester(semester: String) {
        repo.addSemester(semester)
        view.addSemesterRecycler(semester)
    }

    override fun removeSemester(semester: String) {
        repo.removeSemester(semester)
        view.removeSemesterRecycler(semester)
        computeGWA()
    }

}