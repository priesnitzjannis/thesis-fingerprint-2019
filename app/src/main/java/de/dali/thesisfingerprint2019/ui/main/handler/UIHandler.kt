package de.dali.thesisfingerprint2019.ui.main.handler

import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import android.widget.ImageView
import de.dali.thesisfingerprint2019.ui.base.custom.ResultView
import java.lang.ref.WeakReference

class UIHandler : Handler() {

    private lateinit var weakRefQualityAssurance: WeakReference<ResultView>
    private lateinit var weakRefFingerDetection: WeakReference<ResultView>
    //private lateinit var weakRefFingerSegmentation: WeakReference<ImageView>
    //private lateinit var weakRefFingerRotation: WeakReference<ImageView>
    //private lateinit var weakRefEnhancement: WeakReference<ImageView>

    fun setViews(views: Array<out ResultView>) {
        weakRefQualityAssurance = WeakReference(views[0])
        weakRefFingerDetection = WeakReference(views[1])
        //weakRefFingerSegmentation = WeakReference(fingerSegmentation)
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
            weakRefFingerDetection.get()?.drawBitmap(bitmaps[1])
        }else{
            weakRefQualityAssurance.get()?.clear()
            weakRefFingerDetection.get()?.clear()
        }
    }

}

