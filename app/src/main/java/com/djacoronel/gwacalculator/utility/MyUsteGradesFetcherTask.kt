package com.djacoronel.gwacalculator.utility

import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import com.djacoronel.gwacalculator.Contract
import com.djacoronel.gwacalculator.model.Course
import org.jetbrains.anko.toast
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.lang.ref.WeakReference


class MyUsteGradesFetcherTask(
        private val mPresenter: Contract.Actions, activity: Activity) : AsyncTask<String, Void, ArrayList<Course>>() {

    private lateinit var mProgressDialog: ProgressDialog
    private var weakActivity = WeakReference<Activity>(activity)
    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36"

    override fun onPreExecute() {
        super.onPreExecute()
        mProgressDialog = ProgressDialog(weakActivity.get())
        mProgressDialog.setTitle("Fetching grades from MyUste")
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.isIndeterminate = false
        mProgressDialog.show()


    }

    override fun doInBackground(vararg params: String): ArrayList<Course> {
        val studNo = params[0]
        val password = params[1]
        val courses = arrayListOf<Course>()

        try {
            HttpsTrustManager.allowAllSSL()
            val cookies = getCookies()
            val semLinks = loginAndGetLinksToSemGrades(studNo, password, cookies)

            for (semLink in semLinks.reversed()) {
                val rows = getRowsFromGradesTable(semLink, cookies)
                val semName = generateSemName(semLink)
                rows.filter { isRelevantRow(it) }.mapTo(courses) { createCourse(it, semName) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return courses
    }

    private fun getCookies(): MutableMap<String, String> {
        val url = "https://myuste.ust.edu.ph/student"
        val response = Jsoup.connect(url).userAgent(userAgent)
                .method(Connection.Method.GET)
                .execute()

        return response.cookies()
    }

    private fun loginAndGetLinksToSemGrades(studNo: String, password: String,
                                            cookies: MutableMap<String, String>): Elements {
        Jsoup.connect("https://myuste.ust.edu.ph/student/loginProcess")
                .cookies(cookies)
                .data("txtUsername", studNo)
                .data("txtPassword", password)
                .userAgent(userAgent)
                .method(Connection.Method.POST)
                .followRedirects(true)
                .execute()

        val doc = Jsoup.connect("https://myuste.ust.edu.ph/student/myGrades.jsp")
                .cookies(cookies)
                .userAgent(userAgent)
                .get()
        return doc.select("a#link_style1")
    }

    private fun getRowsFromGradesTable(semLink: Element, cookies: MutableMap<String, String>): Elements {
        val href = semLink.attr("href")

        val doc = Jsoup.connect("https://myuste.ust.edu.ph/student/" + href)
                .cookies(cookies)
                .userAgent(userAgent)
                .get()

        val table = doc.select("table#grades_table")
        return table[0].select("tr")
    }

    private fun generateSemName(semLink: Element): String {
        val href = semLink.attr("href")

        val startYear = href.substring(17, 21)
        val endYear = (startYear.toInt() + 1).toString().substring(2, 4)
        val semCode = href[27].toString().toInt()
        var semNum = ""

        when (semCode) {
            0 -> semNum = "1st Sem"
            1 -> semNum = "2nd Sem"
            3 -> semNum = "3rd Sem"
        }

        return "SY$startYear-$endYear, $semNum"
    }

    private fun isRelevantRow(row: Element): Boolean {
        val pe = "PE"
        val nstp = "NSTP"
        val header = "Subject Name"
        val courseCode = row.select("td")[0].text()

        return !courseCode.contains(pe) and
                !courseCode.contains(nstp) and
                !courseCode.contains(header)
    }

    private fun createCourse(row: Element, semName: String): Course {
        val courseCode = row.select("td")[0].text()
        val courseName = row.select("td")[1].text()
        var units = row.select("td")[2].text().toInt()
        units += row.select("td")[3].text().toInt()

        var grade = 0.0
        val gradeText = row.select("td")[5].text()

        if (gradeText.trim().isNotEmpty())
            grade = gradeText.toDouble()

        return Course(0, courseCode, units, grade, semName)
    }

    override fun onPostExecute(courses: ArrayList<Course>) {
        if (courses.isEmpty())
            weakActivity.get()?.toast("Failed to fetch grades")
        else {
            val storedSems = mPresenter.getSemesters()
            for (sem in storedSems)
                mPresenter.removeSemester(sem)

            for (course in courses) {
                if (!mPresenter.getSemesters().contains(course.semester))
                    mPresenter.addSemester(course.semester)
                mPresenter.addCourse(course)
            }
        }
        mProgressDialog.dismiss()
    }
}
