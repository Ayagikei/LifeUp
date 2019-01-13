package net.sarasarasa.lifeup.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.foot_view_to_do.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.ExpAdapter
import net.sarasarasa.lifeup.models.ExpModel
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl


class ExpActivity : AppCompatActivity() {


    private val attributeServiceImpl = AttributeServiceImpl()
    private val mList: MutableList<ExpModel> = attributeServiceImpl.listExpDetail(100, 0).toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ExpAdapter
    private var currentOffset = 0
    private var maxOffset = attributeServiceImpl.countExpDetail() - 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = rv
        mAdapter = ExpAdapter(R.layout.item_exp_detail, mList)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter
        mAdapter.emptyView = getEmptyView()

        mAdapter.setOnLoadMoreListener({
            getNewList()
        }, mRecyclerView)

    }


    private fun getEmptyView(): View {
        val view = layoutInflater.inflate(R.layout.foot_view_to_do, null)
        view.textView11.text = "暂时没有经验值收支情况"
        return view
    }

    private fun getNewList() {
        currentOffset += 100
        mAdapter.addData(attributeServiceImpl.listExpDetail(100, currentOffset))

        if (currentOffset >= maxOffset || currentOffset < 0) {
            mAdapter.loadMoreEnd()
        } else {
            mAdapter.loadMoreComplete()
            mAdapter.setEnableLoadMore(true)
        }

        mAdapter.notifyDataSetChanged()
    }

}
