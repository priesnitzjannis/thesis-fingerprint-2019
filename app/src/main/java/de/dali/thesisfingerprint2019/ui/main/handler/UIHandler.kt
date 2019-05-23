package de.dali.thesisfingerprint2019.ui.main.handler

import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import de.dali.thesisfingerprint2019.ui.base.custom.ResultView
import java.lang.ref.WeakReference

class UIHandler : Handler() {

    private lateinit var weakRefQualityAssurance: WeakReference<ResultView>
    //private lateinit var weakRefFingerDetection: WeakReference<ResultView>
    private lateinit var weakRefFingerSegmentation: WeakReference<ResultView>
    //private lateinit var weakRefFingerRotation: WeakReference<ResultView>
    //private lateinit var weakRefEnhancement: WeakReference<ResultView>

    fun setViews(views: Array<out ResultView>) {
        weakRefQualityAssurance = WeakReference(views[0])
        //weakRefFingerDetection = WeakReference(views[1])
        weakRefFingerSegmentation = WeakReference(views[1])
        //weakRefFingerRotation = WeakReference(fingerRotation)
        //weakRefEnhancement = WeakReference(fingerEnhancement)
    }

    fun sendMessage(bmps: List<Bitmap>) {
        val msg = Message()
        msg.obj = bmps

        sendMessage(msg)
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val bitmaps = msg.obj as List<Bitmap>

        if (bitmaps.isNotEmpty()) {
            weakRefQualityAssurance.get()?.drawBitmap(bitmaps[0])
            weakRefFingerSegmentation.get()?.drawBitmap(bitmaps[1])
        }
    }

}

