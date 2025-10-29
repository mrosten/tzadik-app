package com.example.webviewtest

import android.content.Context
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.webviewtest.ui.theme.WebViewTestTheme
import java.io.IOException
import java.util.*

// Data class to hold information about a Tzadik
data class Tzadik(
    val id: Int,
    val name: String,
    val nameHe: String,
    var description: String,
    var descriptionHe: String,
    var descriptionIsHtml: Boolean = false,
    var videoUrl: String? = null,
    var imageUrl: String? = null,
    var story: String? = null,
    var storyHe: String? = null,
    var storyIsHtml: Boolean = false
)

private val initialTzadikim = listOf(
    Tzadik(1, "Baal Shem Tov", "בעל שם טוב", "", ""),
    Tzadik(2, "Rashi", "רש\"י", "", ""),
    Tzadik(3, "Rabbi Shimon bar Yochai", "רבי שמעון בר יוחאי", "", ""),
    Tzadik(4, "Menachem Mendel Schneerson", "הרבי מליובאוויטש", "", ""),
    Tzadik(5, "The Vilna Gaon", "הגאון מווילנה", "", ""),
    Tzadik(6, "Rabbi Nachman of Breslov", "רבי נחמן מברסלב", "", ""),
    Tzadik(7, "Rabbi Yosef Karo", "רבי יוסף קארו", "", ""),
    Tzadik(8, "The Chafetz Chaim", "החפץ חיים", "", ""),
    Tzadik(9, "Rabbi Moshe Chaim Luzzatto", "רמח\"ל", "", ""),
    Tzadik(10, "Rabbi Ovadia Yosef", "הרב עובדיה יוסף", "", ""),
    Tzadik(11, "Rabbi Moshe Feinstein", "הרב משה פיינשטיין", "", ""),
    Tzadik(12, "The Rambam (Maimonides)", "רמב\"ם", "", ""),
    Tzadik(13, "The Ramban (Nachmanides)", "רמב\"ן", "", ""),
    Tzadik(14, "Rabbi Yehuda HaNasi", "רבי יהודה הנשיא", "", ""),
    Tzadik(15, "Rabbi Isaac Luria", "האר\"י", "", ""),
    Tzadik(16, "Rabbi Chaim of Volozhin", "רבי חיים מוולוז'ין", "", ""),
    Tzadik(17, "Rabbi Elimelech of Lizhensk", "רבי אלימלך מליז'נסק", "", ""),
    Tzadik(18, "Rabbi Levi Yitzchak of Berditchev", "רבי לוי יצחק מברדיטשב", "", ""),
    Tzadik(19, "Rabbi Shneur Zalman of Liadi", "רבי שניאור זלמן מליאדי", "", ""),
    Tzadik(20, "The Maggid of Mezritch", "המגיד ממזריטש", "", ""),
    Tzadik(21, "The Chozeh of Lublin", "החוזה מלובלין", "", ""),
    Tzadik(22, "The Sfat Emet", "השפת אמת", "", ""),
    Tzadik(23, "The Netziv", "הנצי\"ב", "", ""),
    Tzadik(24, "Rabbi Chaim Kanievsky", "הרב חיים קניבסקי", "", "")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewTestTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var language by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (language == null) {
            LanguageSelectionScreen { selectedLang ->
                language = selectedLang
            }
        } else {
            TzadikimPager(language = language!!)
        }
    }
}

@Composable
fun LanguageSelectionScreen(onLanguageSelected: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Language / בחר שפה", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { onLanguageSelected("en") }) {
            Text("English", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onLanguageSelected("he") }) {
            Text("עברית", fontSize = 20.sp)
        }
    }
}


@Composable
fun TzadikimPager(language: String) {
    val context = LocalContext.current
    val tzadikim = remember { mutableStateListOf<Tzadik>() }
    val isHebrew = language == "he"
    var textZoom by remember { mutableStateOf(100) } // State is now lifted here

    LaunchedEffect(language) {
        // Load local media and description overrides first
        val loadedTzadikim = initialTzadikim.map { base ->
            val candidates = slugCandidates(base.name)
            val video = candidates.firstNotNullOfOrNull { slug -> findLocalVideoAsset(context, slug) }
            val image = candidates.firstNotNullOfOrNull { slug -> findLocalImageAsset(context, slug) }
            val longDesc = candidates.firstNotNullOfOrNull { slug -> findLocalDescriptionAsset(context, slug, isHebrew) }
            val story = candidates.firstNotNullOfOrNull { slug -> findLocalStoriesAsset(context, slug, isHebrew) }

            base.apply {
                val currentDesc = longDesc?.text ?: if (isHebrew) base.descriptionHe else base.description
                val currentStory = story?.text ?: if (isHebrew) base.storyHe else base.story
                description = currentDesc
                descriptionHe = currentDesc
                descriptionIsHtml = longDesc?.isHtml ?: story?.isHtml ?: false
                videoUrl = video
                imageUrl = image
                this.story = currentStory
                this.storyHe = currentStory
            }
        }
        tzadikim.clear()
        tzadikim.addAll(loadedTzadikim)
    }

    val list = tzadikim
    val pagerState = rememberPagerState(pageCount = { list.size })

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) { page ->
            FlippableTzadikCard(
                tzadik = list[page],
                isHebrew = isHebrew,
                textZoom = textZoom,
                onTextZoomChange = { newZoom -> textZoom = newZoom }
            )
        }
    }
}

@Composable
fun FlippableTzadikCard(
    tzadik: Tzadik,
    isHebrew: Boolean,
    textZoom: Int,
    onTextZoomChange: (Int) -> Unit
) {
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { isFlipped = !isFlipped }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        if (rotation < 90f) {
            TzadikVideo(tzadik = tzadik, isHebrew = isHebrew, onTap = { isFlipped = !isFlipped })
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
                contentAlignment = Alignment.Center
            ) {
                TzadikInfo(
                    tzadik = tzadik,
                    isHebrew = isHebrew,
                    textZoom = textZoom,
                    onTextZoomChange = onTextZoomChange,
                    onTap = { isFlipped = !isFlipped }
                )
            }
        }
    }
}

@Composable
fun TzadikVideo(tzadik: Tzadik, isHebrew: Boolean, onTap: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        val videoUrl = tzadik.videoUrl
        val imageUrl = tzadik.imageUrl
        when {
            videoUrl != null -> {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = false
                            settings.mediaPlaybackRequiresUserGesture = false
                            setBackgroundColor(android.graphics.Color.BLACK)
                        }
                    },
                    update = { webView ->
                        val html = """
                            <html>
                              <head>
                                <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'/>
                                <style>
                                  html, body { margin: 0; padding: 0; height: 100%; background: #000; }
                                  .wrap { display: flex; align-items: center; justify-content: center; width: 100vw; height: 100vh; background: #000; }
                                  video { width: 100%; height: 100%; object-fit: contain; display: block; }
                                </style>
                              </head>
                              <body>
                                <div class='wrap'>
                                  <video src='$videoUrl' autoplay loop muted playsinline></video>
                                </div>
                              </body>
                            </html>
                        """.trimIndent()
                        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
                    }
                )
            }
            imageUrl != null -> {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = false
                            setBackgroundColor(android.graphics.Color.BLACK)
                        }
                    },
                    update = { webView ->
                        val html = """
                            <html><head><meta name='viewport' content='width=device-width, initial-scale=1.0'/><style>html,body{margin:0;height:100%;background:black;}img{position:absolute;top:50%;left:50%;transform:translate(-50%,-50%);min-width:100%;min-height:100%;width:auto;height:auto;object-fit:cover;}</style></head>
                            <body><img src='$imageUrl' alt='portrait'/></body></html>
                        """.trimIndent()
                        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
                    }
                )
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No media available", color = Color.White)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(120.dp)
                .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))))
        )

        Text(
            text = if (isHebrew) tzadik.nameHe else tzadik.name,
            style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp),
            textAlign = if (isHebrew) TextAlign.End else TextAlign.Start,
            modifier = Modifier
                .align(if (isHebrew) Alignment.BottomEnd else Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp)
        )

        Box(
            modifier = Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures(onTap = { onTap() }) }
        )
    }
}

@Composable
fun TzadikInfo(
    tzadik: Tzadik,
    isHebrew: Boolean,
    textZoom: Int,
    onTextZoomChange: (Int) -> Unit,
    onTap: () -> Unit
) {
    val content = if (isHebrew) tzadik.descriptionHe else tzadik.description
    val isHtml = tzadik.descriptionIsHtml
    val topBarHeight = 56.dp
    val bottomBarHeight = 48.dp

    if (isHtml) {
        val webViewRef = remember { mutableStateOf<WebView?>(null) }
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(
                modifier = Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures(onTap = { onTap() }) },
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = false
                        setBackgroundColor(android.graphics.Color.BLACK)
                    }.also { wv -> webViewRef.value = wv }
                },
                update = { webView ->
                    webView.settings.textZoom = textZoom
                    val dir = if (isHebrew) "rtl" else "ltr"
                    val textAlign = if (isHebrew) "right" else "left"
                    val css = "body{margin:0;padding:${topBarHeight.value}px 16px ${bottomBarHeight.value}px 16px;color:#eee;background:#000;font-family:sans-serif;line-height:1.6;text-align:$textAlign;}"
                    val wrappedContent = "<html dir=\"$dir\"><head><meta name='viewport' content='width=device-width, initial-scale=1.0'/><style>$css</style></head><body>$content</body></html>"
                    webView.loadDataWithBaseURL("file:///android_asset/", wrappedContent, "text/html", "UTF-8", null)
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarHeight)
                    .align(Alignment.TopCenter)
                    .background(Color(0xAA000000))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { onTextZoomChange((textZoom - 10).coerceAtLeast(50)) }, modifier = Modifier.padding(end = 8.dp)) { Text("A-") }
                Button(onClick = { onTextZoomChange((textZoom + 10).coerceAtMost(250)) }) { Text("A+") }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomBarHeight)
                    .align(Alignment.BottomCenter)
                    .background(Color(0xAA000000))
            )
        }
    } else {
        // Render plain text/markdown-like content as simple HTML
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(
                modifier = Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures(onTap = { onTap() }) },
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = false
                        setBackgroundColor(android.graphics.Color.BLACK)
                    }
                },
                update = { webView ->
                    webView.settings.textZoom = textZoom
                    val dir = if (isHebrew) "rtl" else "ltr"
                    val textAlign = if (isHebrew) "right" else "left"
                    val css = "body{margin:0;padding:${topBarHeight.value}px 16px ${bottomBarHeight.value}px 16px;color:#eee;background:#000;font-family:sans-serif;line-height:1.6;text-align:$textAlign;} pre{white-space:pre-wrap;}"
                    val safe = escapeHtml(content)
                    val wrappedContent = "<html dir=\"$dir\"><head><meta name='viewport' content='width=device-width, initial-scale=1.0'/><style>$css</style></head><body><pre>$safe</pre></body></html>"
                    webView.loadDataWithBaseURL("file:///android_asset/", wrappedContent, "text/html", "UTF-8", null)
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarHeight)
                    .align(Alignment.TopCenter)
                    .background(Color(0xAA000000))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { onTextZoomChange((textZoom - 10).coerceAtLeast(50)) }, modifier = Modifier.padding(end = 8.dp)) { Text("A-") }
                Button(onClick = { onTextZoomChange((textZoom + 10).coerceAtMost(250)) }) { Text("A+") }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomBarHeight)
                    .align(Alignment.BottomCenter)
                    .background(Color(0xAA000000))
            )
        }
    }
}

private fun nameToSlug(name: String): String {
    val lowered = name.lowercase(Locale.US)
    return lowered
        .replace("&", "and")
        .replace(Regex("[^a-z0-9]+"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')
}

private fun stripLeadingWord(input: String, word: String): String {
    val lowered = input.lowercase(Locale.US)
    val w = word.lowercase(Locale.US)
    return if (lowered.startsWith(w)) input.substring(word.length).trimStart() else input
}

private fun slugCandidates(name: String): List<String> {
    val variants = LinkedHashSet<String>()
    fun addVariant(n: String) { variants.add(nameToSlug(n)) }

    val original = name.trim()
    addVariant(original)
    addVariant(stripLeadingWord(original, "Rabbi "))
    addVariant(stripLeadingWord(original, "The "))
    return variants.toList()
}

private fun assetFileExists(context: Context, path: String): Boolean {
    return try {
        context.assets.open(path).close()
        true
    } catch (_: IOException) {
        false
    }
}

private fun readAssetText(context: Context, path: String): String? {
    return try {
        context.assets.open(path).use { it.readBytes().toString(Charsets.UTF_8) }
    } catch (_: IOException) {
        null
    }
}

private data class LoadedDescription(val text: String, val isHtml: Boolean)

private fun findLocalDescriptionAsset(context: Context, slug: String, isHebrew: Boolean): LoadedDescription? {
    val lang = if (isHebrew) "he" else "en"
    val base = "tzadikim/$slug/"

    // Try language-specific HTML first
    val htmlLang = "description_${lang}.html"
    if (assetFileExists(context, base + htmlLang)) {
        readAssetText(context, base + htmlLang)?.let { return LoadedDescription(it, true) }
    }
    // Then language-specific Markdown
    val mdLang = "description_${lang}.md"
    if (assetFileExists(context, base + mdLang)) {
        readAssetText(context, base + mdLang)?.let { return LoadedDescription(it, false) }
    }
    // Then generic HTML
    val htmlGeneric = "description.html"
    if (assetFileExists(context, base + htmlGeneric)) {
        readAssetText(context, base + htmlGeneric)?.let { return LoadedDescription(it, true) }
    }
    // Then generic Markdown
    val mdGeneric = "description.md"
    if (assetFileExists(context, base + mdGeneric)) {
        readAssetText(context, base + mdGeneric)?.let { return LoadedDescription(it, false) }
    }
    return null
}

private fun findLocalStoriesAsset(context: Context, slug: String, isHebrew: Boolean): LoadedDescription? {
    val lang = if (isHebrew) "he" else "en"
    val base = "tzadikim/$slug/"
    val htmlFileName = "stories_${lang}.html"

    if (assetFileExists(context, base + htmlFileName)) {
        readAssetText(context, base + htmlFileName)?.let {
            return LoadedDescription(text = it, isHtml = true)
        }
    }
    return null
}

private fun escapeHtml(text: String): String {
    return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")
}

private fun findLocalVideoAsset(context: Context, slug: String): String? {
    val base = "tzadikim/$slug/"
    listOf(
        "clip.mp4",
        "clip2.mp4",
        "video.mp4",
        "portrait.mp4"
    ).firstOrNull { assetFileExists(context, base + it) }?.let {
        return "file:///android_asset/$base$it"
    }
    return null
}

private fun findLocalImageAsset(context: Context, slug: String): String? {
    val base = "tzadikim/$slug/"
    listOf("portrait.webp", "portrait.jpg", "portrait.png").firstOrNull { assetFileExists(context, base + it) }?.let {
        return "file:///android_asset/$base$it"
    }
    return null
}
