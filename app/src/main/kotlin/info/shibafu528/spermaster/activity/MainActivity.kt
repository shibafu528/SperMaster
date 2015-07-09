package info.shibafu528.spermaster.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.activeandroid.query.Select
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.fragment.EjaculationListFragment
import info.shibafu528.spermaster.model.Tag
import info.shibafu528.spermaster.util.showToast

public class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, EjaculationListFragment())
                    .commit()
        }
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
}
