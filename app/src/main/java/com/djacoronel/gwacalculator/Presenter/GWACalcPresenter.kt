package com.djacoronel.gwacalculator.Presenter

import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.Model.Course


class GWACalcPresenter(val view: Contract.View, val repo: Contract.Repository) : Contract.Actions{

    override fun computeGWA(){
        val courses = repo.getAllCourse()
        val sum = courses.sumByDouble { it.grade * it.units }
        val totalUnits = courses.sumBy { it.units }
        view.updateGWA(sum/totalUnits)
    }

    override fun loadCourses() {
        val courses = repo.getAllCourse()
        view.showTable(courses)
    }

    override fun onAddCourseClick(course: Course) {
        view.showInput()
    }

    override fun addCourse(course: Course) {
        repo.addCourse(course)
        loadCourses()
    }

    override fun getCourse(courseCode: String): Course {
        return repo.getCourse(courseCode)
    }

    override fun removeCourse(course: Course) {
        repo.removeCourse(course)
        loadCourses()
        computeGWA()
    }

    override fun updateCourse(course: Course) {
        repo.updateCourse(course)
    }

    override fun onChangeGrade(course: Course) {
        updateCourse(course)
    }
}