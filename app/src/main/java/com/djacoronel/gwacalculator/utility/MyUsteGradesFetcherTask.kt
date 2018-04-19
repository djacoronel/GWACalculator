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
import java.util.regex.Pattern


class MyUsteGradesFetcherTask(
        private val mView: Contract.View, activity: Activity) : AsyncTask<String, Void, LinkedHashMap<String, ArrayList<Course>>>() {

    private var progressDialog = ProgressDialog(activity)
    private var weakActivity = WeakReference<Activity>(activity)
    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36"

    override fun onPreExecute() {
        super.onPreExecute()
        progressDialog.setTitle("Fetching grades from MyUste")
        progressDialog.setMessage("Loading...")
        progressDialog.isIndeterminate = false
        progressDialog.show()
    }

    override fun doInBackground(vararg params: String): LinkedHashMap<String, ArrayList<Course>> {
        val studNo = params[0]
        val password = params[1]
        val grades = linkedMapOf<String, ArrayList<Course>>()

        try {
            HttpsTrustManager.allowAllSSL()
            val cookies = getCookies()
            val semLinks = loginAndGetLinksToSemGrades(studNo, password, cookies)

            for (semLink in semLinks.reversed()) {
                val rows = getRowsFromGradesTable(semLink, cookies)
                val semName = generateSemName(semLink)
                val courses = arrayListOf<Course>()

                rows.filter { isRelevantRow(it) }.mapTo(courses) { createCourse(it, semName) }
                grades[semName] = courses
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return grades
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
        var units = row.select("td")[2].text().toDouble()
        units += row.select("td")[3].text().toDouble()

        var grade = 0.0
        val gradeText = row.select("td").last().text()

        // used regular expression to find and extract double values in grades
        // because some grades are encoded with letters which makes the app crash
        val pattern = Pattern.compile("-?\\d+(\\.\\d+)?")
        val matcher = pattern.matcher(gradeText)
        if (matcher.find()) {
            grade = matcher.group().toDouble()
        }

        return Course(0, courseCode, units, grade, 0)
    }

    override fun onPostExecute(grades: LinkedHashMap<String, ArrayList<Course>>) {
        if (grades.isEmpty())
            weakActivity.get()?.toast("Failed to fetch grades. Check your internet connection.")
        else {
            mView.showOverwritePrompt(grades)
        }

        weakActivity.get()?.let {
            if (it.isFinishing || it.isDestroyed) {
                return
            }
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
            }
        }
    }
}
