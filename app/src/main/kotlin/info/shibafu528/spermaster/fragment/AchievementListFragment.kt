package info.shibafu528.spermaster.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.activeandroid.query.Select
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.model.Achievement
import kotlinx.android.synthetic.fragment_achievement_list.recyclerView
import java.text.SimpleDateFormat

/**
 * Created by shibafu on 15/07/20.
 */
public class AchievementListFragment : Fragment() {
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_achievement_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super<Fragment>.onActivityCreated(savedInstanceState)

        recyclerView.setAdapter(AchievementAdapter(getActivity(), Select().from(javaClass<Achievement>()).orderBy("Key asc").execute()))

        recyclerView.setLayoutManager(LinearLayoutManager(getActivity()))
    }

    private inner class AchievementAdapter(context: Context, val dataList: List<Achievement>) : RecyclerView.Adapter<AchievementListFragment.ViewHolder>() {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getItemCount() = dataList.size()

        override fun onCreateViewHolder(p: ViewGroup?, vt: Int) = ViewHolder(inflater.inflate(android.R.layout.simple_list_item_2, p, false))

        override fun onBindViewHolder(vh: AchievementListFragment.ViewHolder?, pos: Int) = vh?.set(dataList.get(pos))

    }

    private inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        fun set(data: Achievement) {
            (v.findViewById(android.R.id.text1) as TextView).setText(data.name)
            (v.findViewById(android.R.id.text2) as TextView).setText("${data.description}\n解除日時: ${dateFormat.format(data.unlockedDate)}")
        }
    }
}