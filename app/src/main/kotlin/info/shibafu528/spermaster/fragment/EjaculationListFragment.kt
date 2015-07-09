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
import com.activeandroid.Model
import com.activeandroid.query.Select
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.activity.EjaculationActivity
import info.shibafu528.spermaster.model.Ejaculation
import info.shibafu528.spermaster.util.showToast
import kotlinx.android.synthetic.fragment_recycler.addFab
import kotlinx.android.synthetic.fragment_recycler.recyclerView
import java.text.SimpleDateFormat

/**
 * Created by shibafu on 15/07/04.
 */
public class EjaculationListFragment : Fragment(), SimpleAlertDialogFragment.OnDialogChoseListener {
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_recycler, container, false)
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
        }
    }

    override fun onDialogChose(requestCode: Int, extendCode: Long, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Model.delete(javaClass<Ejaculation>(), extendCode)
            showToast("削除しました")
            resetListAdapter()
        }
    }

    private fun resetListAdapter() {
        recyclerView.setAdapter(EjaculationAdapter(getActivity(), Select().from(javaClass<Ejaculation>()).orderBy("EjaculatedDate desc").execute()))
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

        fun Long.toDateString(): String {
            val day = this / 86400000;
            val time = this % 86400000;
            val hour = time / 3600000;
            val minute = time % 3600000 / 60000;
            return "${day}日 ${hour}時間 ${minute}分"
        }

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