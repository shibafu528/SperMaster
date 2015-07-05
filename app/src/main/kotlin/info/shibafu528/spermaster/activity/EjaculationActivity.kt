package info.shibafu528.spermaster.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.activeandroid.query.Select
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.model.Ejaculation
import kotlinx.android.synthetic.activity_ejaculation.*
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by shibafu on 15/07/05.
 */
public class EjaculationActivity : AppCompatActivity() {
    val sdf = SimpleDateFormat("yyyy/MM/dd\nHH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ejaculation)
        getSupportActionBar().hide()

        cancelButton.setOnClickListener { finish() }

        val ejaculationId = getIntent().getLongExtra(EXTRA_EJACULATION_ID, -1)
        val ejaculation = if (ejaculationId < 0) {
            // New
            Ejaculation(Date(System.currentTimeMillis()))
        } else {
            // Edit
            Select().from(javaClass<Ejaculation>()).where("_id = ?", ejaculationId).executeSingle()
        }

        date.setText(sdf.format(ejaculation.ejaculatedDate))
        editTags.setText(ejaculation.tags().map { it.name }.join(", "))
        editNote.setText(ejaculation.note)
    }

    companion object {
        private val EXTRA_EJACULATION_ID = "EjaculationId"

        fun createIntent(context: Context, id: Long = -1)
                = Intent(context, javaClass<EjaculationActivity>()).putExtra(EXTRA_EJACULATION_ID, id)
    }
}
