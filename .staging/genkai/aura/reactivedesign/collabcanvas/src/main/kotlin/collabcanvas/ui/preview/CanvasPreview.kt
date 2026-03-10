package collabcanvas.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import collabcanvas.ui.CanvasScreen
import collabcanvas.ui.theme.AuraFrameFXTheme

@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewCanvasScreen() {
    AuraFrameFXTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            CanvasScreen()
        }
    }
}
