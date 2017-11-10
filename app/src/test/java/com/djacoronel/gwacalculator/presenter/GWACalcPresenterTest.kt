package com.djacoronel.gwacalculator.presenter

import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.model.Course
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
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

    private val semesters = mutableListOf("sem1", "sem2", "sem3")
    private val courses = listOf(
            Course(0, "course1", 1, 1.0, "sem1"),
            Course(0, "course2", 1, 1.0, "sem2"),
            Course(0, "course3", 1, 1.0, "sem3")
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
        `when`(mockRepository.getAllCourse()).thenReturn(courses)
        `when`(mockRepository.getSemesters()).thenReturn(semesters)
        semesters.forEach { `when`(mockRepository.getCourses(it)).thenReturn(data[it]) }

        presenter.loadData()
        verify(mockRepository).getSemesters()
        verify(mockView).showGrades(data)
        verify(mockView).updateGWA(ArgumentMatchers.anyDouble())
        verify(mockView).updateSEM(ArgumentMatchers.anyDouble())
    }

    @Test
    fun updateGwaGradeInView() {
        `when`(mockRepository.getAllCourse()).thenReturn(courses)

        presenter.computeGWA()
        verify(mockRepository).getAllCourse()
        verify(mockView).updateGWA(1.0)
    }

    @Test
    fun updateSemGradeInView() {
        val testSemester = semesters[0]

        `when`(mockRepository.getCourses(testSemester)).thenReturn(data[testSemester])

        presenter.computeSEM(testSemester)
        verify(mockRepository).getCourses(testSemester)
        verify(mockView).updateSEM(1.0)
    }
}