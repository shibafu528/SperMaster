package info.shibafu528.spermaster.fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.bindView
import com.activeandroid.ActiveAndroid
import com.activeandroid.Model
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import com.squareup.otto.Subscribe
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.activity.EjaculationActivity
import info.shibafu528.spermaster.model.Achievement
import info.shibafu528.spermaster.model.Ejaculation
import info.shibafu528.spermaster.model.Tag
import info.shibafu528.spermaster.model.TagMap
import info.shibafu528.spermaster.util.EventBus
import info.shibafu528.spermaster.util.MemoizeDelayed
import info.shibafu528.spermaster.util.SelectedTagFilterEvent
import info.shibafu528.spermaster.util.UnselectedTagFilterEvent
import info.shibafu528.spermaster.util.showToast
import info.shibafu528.spermaster.util.toDateString
import kotlinx.android.synthetic.main.fragment_ejaculation_list.*
import java.text.SimpleDateFormat

/**
 * Created by shibafu on 15/07/04.
 */
public class EjaculationListFragment : Fragment(), SimpleAlertDialogFragment.OnDialogChoseListener {
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
    private var filterTag: Tag? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_ejaculation_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        resetListAdapter()

        addFab.setOnClickListener {
            startActivityForResult(Intent(activity, EjaculationActivity::class.java), REQUEST_ADD)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode in (REQUEST_ADD..REQUEST_UPDATE) && resultCode == Activity.RESULT_OK) {
            resetListAdapter()

            //実績解除判定
            if (data != null && EjaculationActivity.getResultId(data) != null) {
                val unlocked = Achievement.unlock(Select().from(Ejaculation::class.java)
                                                          .where("_id = ?",
                                                                  EjaculationActivity.getResultId(data))
                                                          .executeSingle())
                //TODO: もっとまともなものを出したい気もする
                unlocked.forEach { showToast("[実績解除]\n${it.name}を解除しました！") }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        EventBus.register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.unregister(this)
    }

    override fun onDialogChose(requestCode: Int, extendCode: Long, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            ActiveAndroid.beginTransaction()
            try {
                Delete().from(TagMap::class.java).where("EjaculationId = ?", extendCode.toString()).execute<TagMap>()
                Model.delete(Ejaculation::class.java, extendCode)
                ActiveAndroid.setTransactionSuccessful()
                MemoizeDelayed.purge()
                showToast("削除しました")
                resetListAdapter()
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("削除に失敗しました\n${e.toString()}")
            } finally {
                ActiveAndroid.endTransaction()
            }
        }
    }

    private fun resetListAdapter() {
        val query = Select().from(Ejaculation::class.java)
                            .orderBy("EjaculatedDate desc")

        //フィルタリング指定がある場合
        filterTag?.let { query.where("EXISTS (select * from TagMap where TagMap.EjaculationId = Ejaculations._id and TagMap.TagId = ?)", it.id) }

        recyclerView.adapter = EjaculationAdapter(activity, query.execute())

        val lastEjaculation = Select().from(Ejaculation::class.java)
                                      .orderBy("EjaculatedDate desc")
                                      .limit(1)
                                      .executeSingle<Ejaculation>()
        if (lastEjaculation == null) {
            currentTimespan.text = "まだ記録がありません"
            currentSince.text = "最初の記録を付けましょう!\n画面右下の + ボタンを押して下さい"
            currentSinceLeft.visibility = View.GONE
            currentSinceRight.visibility = View.GONE
        } else {
            currentTimespan.text = (System.currentTimeMillis() - lastEjaculation.ejaculatedDate.time).toDateString()
            currentSince.text = dateFormat.format(lastEjaculation.ejaculatedDate)
            currentSinceLeft.visibility = View.VISIBLE
            currentSinceRight.visibility = View.VISIBLE
        }
    }

    @Subscribe fun onSelectedTag(event: SelectedTagFilterEvent) {
        filterTag = event.tag
        resetListAdapter()
    }

    @Subscribe fun onUnselectedTag(event: UnselectedTagFilterEvent) {
        filterTag = null
        resetListAdapter()
    }

    private inner class EjaculationAdapter(context: Context, val dataList: List<Ejaculation>) : RecyclerView.Adapter<ViewHolder>() {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getItemCount() = dataList.size

        override fun onCreateViewHolder(p: ViewGroup?, vt: Int) = ViewHolder(inflater.inflate(R.layout.row_ejaculation, p, false))

        override fun onBindViewHolder(vh: ViewHolder?, pos: Int) {
            vh?.set(dataList[pos])
        }

    }

    inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        val totalTime: TextView by bindView(R.id.totalTime)
        val timeSpan: TextView by bindView(R.id.timeSpan)
        val tags: TextView by bindView(R.id.tags)
        val note: TextView by bindView(R.id.note)

        fun set(data: Ejaculation) {
            val beginDate = data.before()?.let { dateFormat.format(it.ejaculatedDate) + "\n~ " } ?: ""

            timeSpan.text = beginDate + dateFormat.format(data.ejaculatedDate)
            totalTime.text = data.timeSpan.toDateString()
            tags.text = data.tags().map { it.name }.joinToString(", ")

            if (TextUtils.isEmpty(data.note)) {
                note.visibility = View.GONE
            } else {
                note.visibility = View.VISIBLE
                note.text = data.note
            }

            v.setOnClickListener {
                startActivityForResult(EjaculationActivity.createIntent(activity, data.id), REQUEST_UPDATE)
            }
            v.setOnLongClickListener {
                SimpleAlertDialogFragment.newInstance(
                        extendCode = data.id,
                        message = "このデータを削除しますか?",
                        positive = "OK",
                        negative = "キャンセル"
                ).show(childFragmentManager, FRAGMENT_TAG_DELETE)
                true
            }
        }
    }

    companion object {
        protected val REQUEST_ADD: Int = 1
        protected val REQUEST_UPDATE: Int = 2

        protected val FRAGMENT_TAG_DELETE: String = "delete"
    }
}