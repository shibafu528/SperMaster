package info.shibafu528.spermaster.fragment


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.activeandroid.Cache
import com.activeandroid.query.Select
import info.shibafu528.spermaster.R
import info.shibafu528.spermaster.activity.AchievementActivity
import info.shibafu528.spermaster.model.Achievement
import info.shibafu528.spermaster.model.Ejaculation
import info.shibafu528.spermaster.util.showToast
import info.shibafu528.spermaster.util.toDateString
import kotlinx.android.synthetic.main.fragment_stats.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by shibafu on 15/07/13.
 */
class StatsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //平均, 最長, 最短, 合計の取得
        Cache.openDatabase().rawQuery(
                "select avg(Timespan) as Average, max(Timespan) as Longest, min(Timespan) as Shortest, sum(Timespan) as Total " +
                        "from (select EjaculatedDate, Before as BeforeDate, EjaculatedDate - Before as Timespan " +
                        "from Ejaculations, (select E.EjaculatedDate Before from Ejaculations E order by Before desc) " +
                        "where EjaculatedDate > Before " +
                        "group by EjaculatedDate having max(before) " +
                        "order by EjaculatedDate desc); ", null
        ).let {
            if (it.moveToFirst()) {
                average.text = it.getLong(0).toDateString()
                longest.text = it.getLong(1).toDateString()
                shortest.text = it.getLong(2).toDateString()
                totalTime.text = it.getLong(3).toDateString()
            }
        }

        //合計回数の取得
        val count = Select().from(Ejaculation::class.java).count()
        totalCount.text = "${count}回"

        //解除済み実績の個数取得
        val achievements = Select().from(Achievement::class.java).count()
        achievementsCount.text = "${achievements}個"

        //実績詳細ボタン
        achievementsMore.setOnClickListener {
            startActivity(Intent(activity, AchievementActivity::class.java))
        }

        //共有FAB
        shareFab.setOnClickListener {
            //キャプチャを取得
            val capture = captureView()
            if (capture == null) {
                showToast("共有用データの作成に失敗しました")
                return@setOnClickListener
            }

            //適当にストレージに一時出力
            val tempUri = writeToTempFile(ByteArrayOutputStream().let {
                capture.compress(Bitmap.CompressFormat.PNG, 100, it)
                it.toByteArray()
            })
            capture.recycle()

            //Intent発射
            startActivity(Intent(Intent.ACTION_SEND)
                    .setType("image/png")
                    .putExtra(Intent.EXTRA_STREAM, tempUri)
                    .putExtra(Intent.EXTRA_TEXT, getShareText()))
        }
    }

    private fun captureView(): Bitmap? {
        val view = view ?: return null
        try {
            view.isDrawingCacheEnabled = true
            view.drawingCache?.let {
                return Bitmap.createBitmap(it)
            }
            return null
        } finally {
            view.isDrawingCacheEnabled = false
        }
    }

    private fun writeToTempFile(data: ByteArray): Uri? {
        val tempPath = File(activity.externalCacheDir, "share.png")
        try {
            FileOutputStream(tempPath).use {
                it.write(data)
            }
            return Uri.fromFile(tempPath)
        } catch (e: IOException) {
            e.printStackTrace()
            showToast(e.toString())
        }
        return null
    }

    private fun getShareText(): String {
        return "My NightLife Stats. #SperMaster"
    }
}
