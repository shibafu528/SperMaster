package info.shibafu528.spermaster.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.activeandroid.query.Select
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.fragment.EjaculationListFragment
import info.shibafu528.spermaster.fragment.StatsFragment
import info.shibafu528.spermaster.model.Tag
import info.shibafu528.spermaster.util.showToast
import kotlinx.android.synthetic.activity_main.pager
import kotlinx.android.synthetic.activity_main.tabLayout

public class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pager.setAdapter(MyFragmentPagerAdapter(getSupportFragmentManager()))
        tabLayout.setupWithViewPager(pager)
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
