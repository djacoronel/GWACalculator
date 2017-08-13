package com.djacoronel.gwacalculator.Model

import android.content.Context
import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.DbHelper
import org.jetbrains.anko.db.*

class CourseRepository(mContext: Context) : Contract.Repository {
    val database = DbHelper.getInstance(mContext)

    override fun getCourse(courseCode: String): Course {
        var course = Course("CC", 0.0, 0.0)
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
        if (getCourse(course.courseCode).courseCode == "CC") {
            database.use {
                insert("Course",
                        "courseCode" to course.courseCode,
                        "units" to course.units,
                        "grade" to course.grade)
            }
            return true
        }
        else
            return false
    }

    override fun updateCourse(course: Course){
        database.use {
            update("Course", "grade" to course.grade)
                    .whereArgs("courseCode = {courseCode}",
                            "courseCode" to course.courseCode)
                    .exec()
        }
    }

    override fun removeCourse(course: Course){
        database.use{
            delete("Course", "(courseCode = ${course.courseCode})")
        }
    }
}