package info.shibafu528.spermaster.activity

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.activeandroid.query.Select
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.fragment.EjaculationListFragment
import info.shibafu528.spermaster.fragment.StatsFragment
import info.shibafu528.spermaster.model.Tag
import info.shibafu528.spermaster.util.EventBus
import info.shibafu528.spermaster.util.SelectedTagFilterEvent
import info.shibafu528.spermaster.util.UnselectedTagFilterEvent
import info.shibafu528.spermaster.util.showToast
import kotlinx.android.synthetic.activity_main.pager
import kotlinx.android.synthetic.activity_main.spinner
import kotlinx.android.synthetic.activity_main.tabLayout
import kotlinx.android.synthetic.activity_main.toolBar

public class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolBar)
        getSupportActionBar().setDisplayShowTitleEnabled(false)

        pager.setAdapter(MyFragmentPagerAdapter(getSupportFragmentManager()))
        tabLayout.setupWithViewPager(pager)

        spinner.setAdapter(ArrayAdapter<Tag>(this,
                android.R.layout.simple_spinner_dropdown_item,
                listOf(Tag("ALL")) + Select().from(javaClass<Tag>()).execute<Tag>() ))

        //タグが選択された時にFragment等に通知する
        spinner.setOnItemSelectedListener(object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    EventBus.post(UnselectedTagFilterEvent())
                } else {
                    EventBus.post(SelectedTagFilterEvent(parent!!.getSelectedItem() as Tag))
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.getItemId()

        if (id == R.id.action_settings) {
            showToast(Select().from(javaClass<Tag>()).execute<Tag>().map{ it.name }.joinToString())
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private inner class MyFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> EjaculationListFragment()
                else -> StatsFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Records"
                1 -> "Stats"
                else -> ""
            }
        }
    }
}
