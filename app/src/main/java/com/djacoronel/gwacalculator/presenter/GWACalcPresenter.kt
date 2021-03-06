package com.djacoronel.gwacalculator.presenter

import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.model.Course
import com.djacoronel.gwacalculator.model.Semester
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GWACalcPresenter @Inject constructor(val view: Contract.View, val repo: Contract.Repository) : Contract.Actions {

    override fun loadData() {
        val semesters = repo.getSemesters()
        val grades = linkedMapOf<Semester, List<Course>>()

        semesters.forEach { grades[it] = repo.getCourses(it.id) }

        view.showGrades(grades)

        if (semesters.isNotEmpty()) {
            computeGWA()
            computeSEM(semesters[0].id)
        }

        view.setMessageVisibility()
    }

    override fun updateData(grades: LinkedHashMap<String, ArrayList<Course>>) {
        val storedCourses = repo.getCourses()

        for (item in grades) {
            for (course in item.value) {
                val semester = repo.getSemester(item.key)
                val courseToUpdate = storedCourses.find {
                    it.courseCode == course.courseCode && it.semesterId == semester.id
                }

                if (courseToUpdate == null) {
                    addCourse(course)
                } else {
                    courseToUpdate.grade = course.grade
                    updateCourse(courseToUpdate)
                }
            }
        }
    }

    override fun replaceData(grades: LinkedHashMap<String, ArrayList<Course>>) {
        val storedSems = repo.getSemesters()

        for (sem in storedSems)
            removeSemester(sem)

        for (item in grades) {
            addSemester(Semester(0, item.key))
            val semester = repo.getSemester(item.key)

            for (course in item.value) {
                course.semesterId = semester.id
                addCourse(course)
            }
        }
    }

    override fun computeGWA() {
        val courses = repo.getCourses().filter { it.grade != 0.0 }
        var gwa = 0.0

        if (courses.isNotEmpty()) {
            val sum = courses.sumByDouble { it.grade * it.units }
            val totalUnits = courses.sumByDouble { it.units }
            gwa = sum / totalUnits
        }

        view.updateGWA(gwa)
    }

    override fun computeSEM(semesterId: Int) {
        val courses = repo.getCourses(semesterId).filter { it.grade != 0.0 }
        var sem = 0.0

        if (courses.isNotEmpty()) {
            val sum = courses.sumByDouble { it.grade * it.units }
            val totalUnits = courses.sumByDouble { it.units }
            sem = sum / totalUnits
        }

        view.updateSEM(sem)
    }


    override fun addCourse(course: Course) {
        repo.addCourse(course)
        view.addCourse(course)
        computeGWA()
        computeSEM(course.semesterId)

        view.setMessageVisibility()
    }

    override fun updateCourse(course: Course) {
        repo.updateCourse(course)
        view.updateCourse(course)
        computeGWA()
        computeSEM(course.semesterId)
    }

    override fun removeCourse(course: Course) {
        repo.removeCourse(course)
        view.removeCourse(course)
        computeGWA()
        computeSEM(course.semesterId)

        view.setMessageVisibility()
    }

    override fun getCourses(semester: Semester): List<Course> {
        return repo.getCourses(semester.id)
    }


    override fun addSemester(semester: Semester) {
        repo.addSemester(semester)
        view.addSemesterRecycler(repo.getSemester(semester.title))

        view.setMessageVisibility()
    }

    override fun updateSemester(semester: Semester) {
        repo.updateSemester(semester)
    }

    override fun removeSemester(semester: Semester) {
        repo.removeSemester(semester)
        view.removeSemesterRecycler(semester)
        computeGWA()

        view.setMessageVisibility()
    }

    override fun getSemesters(): List<Semester> {
        return repo.getSemesters()
    }
}