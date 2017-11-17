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
        var course = Course(0, "CC", 0, 0.0, "1st Semester")
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
                        "semester" to course.semester)
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

    override fun removeSemester(semester: String) {
        database.use {
            delete("Course",
                    "(semester = {semester})",
                    "semester" to semester)
            delete("Semester",
                    "(semester = {semester})",
                    "semester" to semester)
        }
    }

    override fun addSemester(semester: String) {
        database.use {
            insert("Semester",
                    "semester" to semester)
        }
    }

    override fun getSemesters(): MutableList<String> {
        var semesters = mutableListOf<String>()
        database.use {
            semesters = select("Semester").parseList(classParser<String>()).toMutableList()
        }
        return semesters
    }

    override fun getCourses(semester: String): MutableList<Course> {
        var courses = mutableListOf<Course>()
        database.use {
            courses = select("Course")
                    .whereArgs("(semester = {semester})",
                            "semester" to semester)
                    .parseList(classParser<Course>()).toMutableList()
        }
        return courses
    }
}
