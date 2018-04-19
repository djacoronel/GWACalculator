package com.djacoronel.gwacalculator.model

import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.utility.DbHelper
import org.jetbrains.anko.db.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor() : Contract.Repository {
    @Inject lateinit var database: DbHelper

    override fun getCourse(courseCode: String): Course {
        var course = Course(0, "CC", 0.0, 0.0, 0)
        database.use {
            select("Course")
                    .whereArgs("(courseCode = {courseCode})",
                            "courseCode" to courseCode)
                    .parseOpt(classParser<Course>())
                    ?.let {course = it}
        }
        return course
    }

    override fun getAllCourse(): MutableList<Course> {
        var courses = mutableListOf<Course>()
        database.use {
            courses = select("Course")
                    .parseList(classParser<Course>()).toMutableList()
        }
        return courses
    }

    override fun addCourse(course: Course): Boolean {
            database.use {
                val id = insert("Course",
                        "courseCode" to course.courseCode,
                        "units" to course.units,
                        "grade" to course.grade,
                        "semester" to course.semesterId)
                course.id = id.toInt()
            }
            return true
    }

    override fun updateCourse(course: Course){
        database.use {
            update("Course", "grade" to course.grade)
                    .whereArgs("id = {id}",
                            "id" to course.id)
                    .exec()
        }
    }

    override fun removeCourse(course: Course){
        database.use{
            delete("Course",
                    "(courseCode = {courseCode})",
                    "courseCode" to course.courseCode)
        }
    }

    override fun removeSemester(semester: Semester) {
        database.use {
            delete("Course",
                    "(semester = {semesterId})",
                    "semesterId" to semester.id)
            delete("Semester",
                    "(id = {semesterId})",
                    "semesterId" to semester.id)
        }
    }

    override fun addSemester(semester: Semester) {
        database.use {
            insert("Semester",
                    "semester" to semester.title)
        }
    }

    override fun getSemesters(): MutableList<Semester> {
        var semesters = mutableListOf<Semester>()
        database.use {
            semesters = select("Semester").parseList(classParser<Semester>()).toMutableList()
        }
        return semesters
    }

    override fun getCourses(semesterId: Int): MutableList<Course> {
        var courses = mutableListOf<Course>()
        database.use {
            courses = select("Course")
                    .whereArgs("(semester = {semesterId})",
                            "semesterId" to semesterId)
                    .parseList(classParser<Course>()).toMutableList()
        }
        return courses
    }

    override fun getSemester(semTitle: String): Semester {
        var semester = Semester(0, "semester")
        database.use {
            select("Semester")
                    .whereArgs("(semester = {semester})",
                            "semester" to semTitle)
                    .parseOpt(classParser<Semester>())
                    ?.let { semester = it }
        }
        return semester
    }
}
