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
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.activity.EjaculationActivity
import info.shibafu528.spermaster.model.Achievement
import info.shibafu528.spermaster.model.Ejaculation
import info.shibafu528.spermaster.model.Tag
import info.shibafu528.spermaster.model.TagMap
import info.shibafu528.spermaster.util.*
import kotlinx.android.synthetic.fragment_ejaculation_list.*
import java.text.SimpleDateFormat
import com.squareup.otto.Subscribe as subscribe

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
        super<Fragment>.onActivityCreated(savedInstanceState)
        recyclerView.setLayoutManager(LinearLayoutManager(getActivity()))

        resetListAdapter()

        addFab.setOnClickListener {
            startActivityForResult(Intent(getActivity(), javaClass<EjaculationActivity>()), REQUEST_ADD)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<Fragment>.onActivityResult(requestCode, resultCode, data)

        if (requestCode in (REQUEST_ADD..REQUEST_UPDATE) && resultCode == Activity.RESULT_OK) {
            resetListAdapter()

            //実績解除判定
            if (data != null && EjaculationActivity.getResultId(data) != null) {
                val unlocked = Achievement.unlock(Select().from(javaClass<Ejaculation>())
                                                          .where("_id = ?",
                                                                  EjaculationActivity.getResultId(data))
                                                          .executeSingle())
                //TODO: もっとまともなものを出したい気もする
                unlocked.forEach { showToast("[実績解除]\n${it.name}を解除しました！") }
            }
        }
    }

    override fun onResume() {
        super<Fragment>.onResume()
        EventBus.register(this)
    }

    override fun onPause() {
        super<Fragment>.onPause()
        EventBus.unregister(this)
    }

    override fun onDialogChose(requestCode: Int, extendCode: Long, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            ActiveAndroid.beginTransaction()
            try {
                Delete().from(javaClass<TagMap>()).where("EjaculationId = ?", extendCode.toString()).execute<TagMap>()
                Model.delete(javaClass<Ejaculation>(), extendCode)
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
        val query = Select().from(javaClass<Ejaculation>())
                            .orderBy("EjaculatedDate desc")

        //フィルタリング指定がある場合
        filterTag?.let { query.where("EXISTS (select * from TagMap where TagMap.EjaculationId = Ejaculations._id and TagMap.TagId = ?)", it.getId()) }

        recyclerView.setAdapter(EjaculationAdapter(getActivity(), query.execute()))

        val lastEjaculation = Select().from(javaClass<Ejaculation>())
                                      .orderBy("EjaculatedDate desc")
                                      .limit(1)
                                      .executeSingle<Ejaculation>()
        if (lastEjaculation == null) {
            currentTimespan.setText("まだ記録がありません")
            currentSince.setText("最初の記録を付けましょう!\n画面右下の + ボタンを押して下さい")
            currentSinceLeft.setVisibility(View.GONE)
            currentSinceRight.setVisibility(View.GONE)
        } else {
            currentTimespan.setText((System.currentTimeMillis() - lastEjaculation.ejaculatedDate.getTime()).toDateString())
            currentSince.setText(dateFormat.format(lastEjaculation.ejaculatedDate))
            currentSinceLeft.setVisibility(View.VISIBLE)
            currentSinceRight.setVisibility(View.VISIBLE)
        }
    }

    subscribe fun onSelectedTag(event: SelectedTagFilterEvent) {
        filterTag = event.tag
        resetListAdapter()
    }

    subscribe fun onUnselectedTag(event: UnselectedTagFilterEvent) {
        filterTag = null
        resetListAdapter()
    }

    private inner class EjaculationAdapter(context: Context, val dataList: List<Ejaculation>) : RecyclerView.Adapter<ViewHolder>() {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getItemCount() = dataList.size()

        override fun onCreateViewHolder(p: ViewGroup?, vt: Int) = ViewHolder(inflater.inflate(R.layout.row_ejaculation, p, false))

        override fun onBindViewHolder(vh: ViewHolder?, pos: Int) = vh?.set(dataList.get(pos))

    }

    private inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        val totalTime: TextView by bindView(R.id.totalTime)
        val timeSpan: TextView by bindView(R.id.timeSpan)
        val tags: TextView by bindView(R.id.tags)
        val note: TextView by bindView(R.id.note)

        fun set(data: Ejaculation) {
            val beginDate = data.before()?.let { dateFormat.format(it.ejaculatedDate) + "\n~ " } ?: ""

            timeSpan.setText(beginDate + dateFormat.format(data.ejaculatedDate))
            totalTime.setText(data.timeSpan.toDateString())
            tags.setText(data.tags().map { it.name }.join(", "))

            if (TextUtils.isEmpty(data.note)) {
                note.setVisibility(View.GONE)
            } else {
                note.setVisibility(View.VISIBLE)
                note.setText(data.note)
            }

            v.setOnClickListener {
                startActivityForResult(EjaculationActivity.createIntent(getActivity(), data.getId()), REQUEST_UPDATE)
            }
            v.setOnLongClickListener {
                SimpleAlertDialogFragment.newInstance(
                        extendCode = data.getId(),
                        message = "このデータを削除しますか?",
                        positive = "OK",
                        negative = "キャンセル"
                ).show(getChildFragmentManager(), FRAGMENT_TAG_DELETE)
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