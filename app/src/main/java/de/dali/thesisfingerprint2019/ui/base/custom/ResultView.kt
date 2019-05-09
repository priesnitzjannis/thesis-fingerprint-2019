package de.dali.thesisfingerprint2019.ui.base.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceView

class ResultView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {

    fun drawBitmap(bm: Bitmap) {
        val canvas = holder.lockCanvas()
        if (canvas != null) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR)

            val scale = calcScale(bm.height, bm.width)

            if (scale != 0f) {
                canvas.drawBitmap(
                    bm, Rect(0, 0, bm.width, bm.height),
                    Rect(
                        ((canvas.width - scale * bm.width) / 2).toInt(),
                        ((canvas.height - scale * bm.height) / 2).toInt(),
                        ((canvas.width - scale * bm.width) / 2 + scale * bm.width).toInt(),
                        ((canvas.height - scale * bm.height) / 2 + scale * bm.height).toInt()
                    ), null
                )
            } else {
                canvas.drawBitmap(
                    bm, Rect(0, 0, bm.width, bm.height),
                    Rect(
                        (canvas.width - bm.width) / 2,
                        (canvas.height - bm.height) / 2,
                        (canvas.width - bm.width) / 2 + bm.width,
                        (canvas.height - bm.height) / 2 + bm.height
                    ), null
                )
            }

            holder.unlockCanvasAndPost(canvas)
        }
    }

    fun clear() {
        val canvas = holder.lockCanvas()
        if (canvas != null) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR)
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun calcScale(height: Int, width: Int): Float {
        val mw = this.width.toFloat()
        val mh = this.height.toFloat()

        val scale = width / mh
        val scale2 = mw / height

        return if (scale2 < scale) scale2 else scale
    }

}