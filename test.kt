import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap

fun test(d: Drawable): Bitmap {
    return d.toBitmap()
}
