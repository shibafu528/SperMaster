package info.shibafu528.spermaster.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.activeandroid.ActiveAndroid
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.model.Ejaculation
import info.shibafu528.spermaster.model.Tag
import info.shibafu528.spermaster.model.TagMap
import info.shibafu528.spermaster.util.*
import kotlinx.android.synthetic.activity_ejaculation.*
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by shibafu on 15/07/05.
 */
public class EjaculationActivity : AppCompatActivity(), CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd")
    val timeFormat = SimpleDateFormat("HH:mm")

    var ejaculation: Ejaculation = Ejaculation(Date(System.currentTimeMillis()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ejaculation)
        getSupportActionBar().hide()

        date.setOnClickListener {
            ejaculation.ejaculatedDate.toCalendar().let {
                CalendarDatePickerDialog.newInstance(this, it.year, it.month, it.day)
                        .show(getSupportFragmentManager(), "date")
            }
        }
        time.setOnClickListener {
            ejaculation.ejaculatedDate.toCalendar().let {
                RadialTimePickerDialog.newInstance(this, it.hourOfDay, it.minute, is24HourMode())
                        .show(getSupportFragmentManager(), "time")
            }
        }
        okButton.setOnClickListener(onSubmit())
        cancelButton.setOnClickListener { finish() }

        val ejaculationId = getIntent().getLongExtra(EXTRA_EJACULATION_ID, -1)
        if (ejaculationId > -1) {
            // Edit Mode
            ejaculation = Select().from(javaClass<Ejaculation>())
                                  .where("_id = ?", ejaculationId)
                                  .executeSingle()
        }

        date.setText(dateFormat.format(ejaculation.ejaculatedDate))
        time.setText(timeFormat.format(ejaculation.ejaculatedDate))
        editTags.setText(ejaculation.tags().map { it.name }.join(", "))
        editNote.setText(ejaculation.note)
    }

    private fun is24HourMode() = "24".equals(Settings.System.getString(getContentResolver(), Settings.System.TIME_12_24))

    override fun onTimeSet(p0: RadialTimePickerDialog?, p1: Int, p2: Int) {
        ejaculation.ejaculatedDate = ejaculation.ejaculatedDate.toCalendar().let {
            it.hourOfDay = p1
            it.minute = p2
            it.getTime()
        }
        time.setText(timeFormat.format(ejaculation.ejaculatedDate))
    }

    override fun onDateSet(p0: CalendarDatePickerDialog?, p1: Int, p2: Int, p3: Int) {
        ejaculation.ejaculatedDate = ejaculation.ejaculatedDate.toCalendar().let {
            it.year = p1
            it.month = p2
            it.day = p3
            it.getTime()
        }
        date.setText(dateFormat.format(ejaculation.ejaculatedDate))
    }

    private fun onSubmit(): (View) -> Unit = {
        ejaculation.note = editNote.getText().toString()
        ActiveAndroid.beginTransaction()
        try {
            val ejaculationId = ejaculation.save()

            //タグの再マッピング
            Delete().from(javaClass<TagMap>()).where("EjaculationId = ?", ejaculationId.toString()).execute<TagMap>()
            Tag.parseInputTags(editTags.getText().toString()).forEach {
                if (it.getId() == null) it.save()
                TagMap(ejaculation, it).save()
            }
            //未使用タグの削除
            Delete().from(javaClass<Tag>()).where("NOT EXISTS (select * from TagMap where TagMap.TagId = Tags._id)").execute<Tag>()

            ActiveAndroid.setTransactionSuccessful()
            showToast("Updated.")
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_EJACULATION_ID, ejaculationId))
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(e.toString())
        } finally {
            ActiveAndroid.endTransaction()
        }
    }

    companion object {
        private val EXTRA_EJACULATION_ID = "EjaculationId"

        fun createIntent(context: Context, id: Long = -1)
                = Intent(context, javaClass<EjaculationActivity>()).putExtra(EXTRA_EJACULATION_ID, id)

        fun getResultId(data: Intent): Long? {
            if (data.hasExtra(EXTRA_EJACULATION_ID))
                return data.getLongExtra(EXTRA_EJACULATION_ID, -1)
            else
                return null
        }
    }
}
