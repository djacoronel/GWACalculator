package com.djacoronel.gwacalculator.View

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup


class ViewPagerAdapter : PagerAdapter() {
    private val mRecyclerList = mutableListOf<RecyclerView>()
    private val mRecyclerTitleList = mutableListOf<String>()

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view === `object`
    }

    fun addRecycler(recyclerView: RecyclerView, semester: String): Int {
        return addRecycler(recyclerView, semester, mRecyclerList.size)
    }

    fun addRecycler(recyclerView: RecyclerView, semester: String, position: Int): Int {
        mRecyclerTitleList.add(semester)
        mRecyclerList.add(position, recyclerView)
        notifyDataSetChanged()
        return position
    }

    fun removeRecycler(pager: ViewPager, semester: String): Int {
        return removeRecycler(pager, mRecyclerTitleList.indexOf(semester))
    }

    fun removeRecycler(pager: ViewPager, position: Int): Int {
        pager.adapter = null
        mRecyclerList.removeAt(position)
        pager.adapter = this
        notifyDataSetChanged()
        return position
    }

    fun getRecycler(position: Int): RecyclerView {
        return mRecyclerList[position]
    }

    override fun getCount(): Int {
        return mRecyclerList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mRecyclerTitleList[position]
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