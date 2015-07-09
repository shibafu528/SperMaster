package info.shibafu528.spermaster.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.activeandroid.ActiveAndroid
import com.activeandroid.query.Delete
import com.activeandroid.query.From
import com.activeandroid.query.Select
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.model.Ejaculation
import info.shibafu528.spermaster.model.Tag
import info.shibafu528.spermaster.model.TagMap
import info.shibafu528.spermaster.util.putDebugLog
import info.shibafu528.spermaster.util.showToast
import kotlinx.android.synthetic.activity_ejaculation.*
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by shibafu on 15/07/05.
 */
public class EjaculationActivity : AppCompatActivity() {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd")
    val timeFormat = SimpleDateFormat("HH:mm")

    var ejaculation: Ejaculation = Ejaculation(Date(System.currentTimeMillis()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ejaculation)
        getSupportActionBar().hide()

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
            setResult(Activity.RESULT_OK)
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
    }
}
