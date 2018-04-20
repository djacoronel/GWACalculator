package com.djacoronel.gwacalculator.view

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.djacoronel.gwacalculator.model.Semester


class ViewPagerAdapter : PagerAdapter() {
    private val mRecyclerList = mutableListOf<RecyclerView>()
    private val mRecyclerTitleList = mutableListOf<Semester>()

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view === `object`
    }

    fun addRecycler(recyclerView: RecyclerView, semester: Semester): Int {
        return addRecycler(recyclerView, semester, mRecyclerList.size)
    }

    private fun addRecycler(recyclerView: RecyclerView, semester: Semester, position: Int): Int {
        mRecyclerTitleList.add(semester)
        mRecyclerList.add(position, recyclerView)
        notifyDataSetChanged()
        return position
    }

    fun removeRecycler(pager: ViewPager, semester: Semester): Int {
        val sem = mRecyclerTitleList.find { it.id == semester.id }
        return removeRecycler(pager, mRecyclerTitleList.indexOf(sem))
    }

    private fun removeRecycler(pager: ViewPager, position: Int): Int {
        val nextPosition = if (position == mRecyclerList.lastIndex) position - 1 else position

        pager.adapter = null
        mRecyclerList.removeAt(position)
        mRecyclerTitleList.removeAt(position)
        pager.adapter = this

        pager.setCurrentItem(nextPosition, true)
        notifyDataSetChanged()
        return nextPosition
    }

    fun getRecyclerAdapter(position: Int): RecyclerAdapter {
        return mRecyclerList[position].adapter as RecyclerAdapter
    }

    fun getPageSemester(position: Int): Semester {
        return mRecyclerTitleList[position]
    }

    override fun getCount(): Int {
        return mRecyclerList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mRecyclerTitleList[position].title
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val recyclerView = mRecyclerList[position]
        container.addView(recyclerView)
        return recyclerView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(mRecyclerList[position])
    }

    override fun getItemPosition(`object`: Any?): Int {
        return if (!mRecyclerList.contains(`object` as RecyclerView)) {
            POSITION_NONE
        } else {
            mRecyclerList.indexOf(`object`)
        }
    }
}