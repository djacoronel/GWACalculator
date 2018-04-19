package com.djacoronel.gwacalculator.model

data class Course(var id: Int,
                  val courseCode: String,
                  val units: Double,
                  var grade: Double,
                  var semesterId: Int)