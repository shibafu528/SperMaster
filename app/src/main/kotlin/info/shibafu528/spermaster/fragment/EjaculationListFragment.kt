package info.shibafu528.spermaster.fragment

import android.content.Context
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
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.model.Ejaculation
import info.shibafu528.spermaster.model.Means
import java.util.Date

/**
 * Created by shibafu on 15/07/04.
 */
public class EjaculationListFragment : Fragment() {
    val recyclerView: RecyclerView by bindView(R.id.recyclerView)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.setLayoutManager(LinearLayoutManager(getActivity()))
        recyclerView.setAdapter(EjaculationAdapter(getActivity(), (1..10).map { Ejaculation(Date(System.currentTimeMillis()), Means.MASTURBATION) }))
    }

    private class EjaculationAdapter(context: Context, val dataList: List<Ejaculation>) : RecyclerView.Adapter<ViewHolder>() {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getItemCount() = dataList.size()

        override fun onCreateViewHolder(p: ViewGroup?, vt: Int) = ViewHolder(inflater.inflate(R.layout.row_ejaculation, p, false))

        override fun onBindViewHolder(vh: ViewHolder?, pos: Int) = vh?.set(dataList.get(pos))

    }

    private class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val totalTime : TextView by bindView(R.id.totalTime)
        val timeSpan : TextView by bindView(R.id.timeSpan)
        val means : TextView by bindView(R.id.means)
        val note : TextView by bindView(R.id.note)

        fun set(data: Ejaculation) {
            //TODO: 時間周りどうしよう 設計うっかりしていた
            means.setText(data.means.label)
            if (TextUtils.isEmpty(data.note)) {
                note.setVisibility(View.GONE)
            } else {
                note.setVisibility(View.VISIBLE)
                note.setText(data.note)
            }
        }
    }
}