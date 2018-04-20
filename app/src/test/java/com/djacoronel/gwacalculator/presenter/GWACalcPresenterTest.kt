package com.djacoronel.gwacalculator.presenter

import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.model.Course
import com.djacoronel.gwacalculator.model.Semester
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Created by djacoronel on 11/6/17.
 */
class GWACalcPresenterTest {
    @Mock private
    lateinit var mockView: Contract.View

    @Mock private
    lateinit var mockRepository: Contract.Repository

    private lateinit var presenter: GWACalcPresenter

    private val semesters = listOf(
            Semester(0, "sem1"),
            Semester(1, "sem2"),
            Semester(2, "sem3"))
    private val courses = listOf(
            Course(0, "course1", 1.0, 1.0, 0),
            Course(1, "course2", 1.0, 1.0, 1),
            Course(2, "course3", 1.0, 1.0, 2)
    )
    private val data = linkedMapOf(
            Pair(semesters[0], listOf(courses[0])),
            Pair(semesters[1], listOf(courses[1])),
            Pair(semesters[2], listOf(courses[2]))
    )

    @Before
    fun setupGWACalcPresenter() {
        MockitoAnnotations.initMocks(this)
        presenter = GWACalcPresenter(mockView, mockRepository)
    }

    @Test
    fun loadGradesFromRepositoryAndLoadIntoView() {
        `when`(mockRepository.getCourses()).thenReturn(courses)
        `when`(mockRepository.getSemesters()).thenReturn(semesters)
        semesters.forEach { `when`(mockRepository.getCourses(it.id)).thenReturn(data[it]) }

        presenter.loadData()
        verify(mockRepository).getSemesters()
        verify(mockView).showGrades(data)
        verify(mockView).updateGWA(ArgumentMatchers.anyDouble())
        verify(mockView).updateSEM(ArgumentMatchers.anyDouble())
    }

    @Test
    fun updateGwaGradeInView() {
        `when`(mockRepository.getCourses()).thenReturn(courses)

        presenter.computeGWA()
        verify(mockRepository).getCourses()
        verify(mockView).updateGWA(1.0)
    }

    @Test
    fun updateSemGradeInView() {
        val testSemester = semesters[0]

        `when`(mockRepository.getCourses(testSemester.id)).thenReturn(data[testSemester])

        presenter.computeSEM(testSemester.id)
        verify(mockRepository).getCourses(testSemester.id)
        verify(mockView).updateSEM(1.0)
    }

    @Test
    fun addCourseTest() {
        val testCourse = courses[0]

        presenter.addCourse(testCourse)
        verify(mockRepository).addCourse(testCourse)
        verify(mockView).addCourse(testCourse)
    }

    @Test
    fun addSemesterTest() {
        `when`(mockRepository.getSemesters()).thenReturn(semesters)

        val existingSemester = semesters[0]
        val newSemester = Semester(0, "sem4")

        presenter.addSemester(existingSemester)
        verify(mockRepository, never()).addSemester(existingSemester)
        verify(mockView, never()).addSemesterRecycler(existingSemester)

        presenter.addSemester(newSemester)
        verify(mockRepository).addSemester(newSemester)
        verify(mockView).addSemesterRecycler(newSemester)
    }
}