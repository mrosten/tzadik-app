package com.example.webviewtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.webviewtest.ui.theme.WebViewTestTheme
import kotlinx.coroutines.launch
import android.content.Context
import android.webkit.WebView
import android.webkit.WebSettings
import java.util.Locale
import java.io.IOException

// Data class to hold information about a Tzadik
 data class Tzadik(
    val id: Int,
    val name: String,
    val description: String,
    val descriptionIsHtml: Boolean = false,
    val videoUrl: String? = null,
    val imageUrl: String? = null,
    val story: String? = null,
    val storyIsHtml: Boolean = false
)

private val initialTzadikim = listOf(
    Tzadik(1, "Baal Shem Tov", "Founder of Hasidism in 18th‑century Eastern Europe, he taught that every Jew can serve God with joy, simplicity, and heartfelt prayer. His teachings emphasized divine providence in every detail of life and the preciousness of even the simplest Jew. Through his disciples, he sparked a spiritual revival that transformed Jewish communities across the region."),
    Tzadik(2, "Rashi", "Rabbi Shlomo Yitzhaki (1040–1105), the foremost commentator on the Torah and Talmud. His lucid explanations became the foundation for all subsequent study, clarifying difficult passages with precise language and sensitivity to pshat (plain meaning). Rashi's commentary remains universally studied in traditional and academic settings alike."),
    Tzadik(3, "Rabbi Shimon bar Yochai", "2nd‑century Tanna and disciple of Rabbi Akiva, traditionally associated with the composition or transmission of the Zohar. He endured Roman persecution and taught profound mystical ideas that shaped Kabbalistic thought for centuries. His yahrzeit on Lag BaOmer is celebrated by countless Jews seeking inspiration and inner light."),
    Tzadik(4, "Menachem Mendel Schneerson", "The Lubavitcher Rebbe (1902–1994), visionary leader who galvanized global Jewish outreach, education, and social services. His talks and letters guided people across the religious spectrum, emphasizing practical mitzvah observance, unconditional love for fellow Jews, and faith in the redemptive potential of every moment."),
    Tzadik(5, "The Vilna Gaon", "Rabbi Elijah of Vilna (1720–1797), a towering Lithuanian sage and polymath whose mastery spanned Tanach, Talmud, Halacha, and Kabbalah. He championed careful text study, intellectual honesty, and personal piety, shaping the Lithuanian yeshiva tradition and leaving a vast scholarly legacy."),
    Tzadik(6, "Rabbi Nachman of Breslov", "Great‑grandson of the Baal Shem Tov (1772–1810). He taught hitbodedut (personal, conversational prayer), joyous faith, and profound allegorical stories that speak to the soul. His path encourages honesty, persistence despite setbacks, and finding God even in darkness."),
    Tzadik(7, "Rabbi Yosef Karo", "Author of the Shulchan Aruch (1488–1575), the seminal code of Jewish law synthesizing centuries of halachic rulings. Living in Safed, he combined rigorous legal analysis with spiritual aspiration, also recording mystical experiences in his work Maggid Meisharim."),
    Tzadik(8, "The Chafetz Chaim", "Rabbi Yisrael Meir Kagan (1838–1933), who devoted his life to refining Jewish speech and daily observance. His works Sefer Chafetz Chaim and Mishnah Berurah became cornerstones of ethical conduct and halachic practice, inspiring communities to speak with care and live with integrity."),
    Tzadik(9, "Rabbi Moshe Chaim Luzzatto", "Ramchal (1707–1746), brilliant thinker and kabbalist whose Mesillat Yesharim charts a clear path of moral refinement and closeness to God. He wrote dramas, ethical works, and mystical texts with striking clarity, uniting head and heart in divine service."),
    Tzadik(10, "Rabbi Ovadia Yosef", "Sephardic Chief Rabbi of Israel (1920–2013), a prodigious posek who revitalized Sephardic halachic tradition for the modern era. His encyclopedic responsa and leadership empowered communities worldwide, balancing fidelity to sources with compassion for people's needs."),
    Tzadik(11, "Rabbi Moshe Feinstein", "Preeminent 20th‑century halachic decisor whose Igrot Moshe addresses complex questions of modern life. With clarity, depth, and pastoral sensitivity, he guided communities through medical, technological, and social change while remaining firmly rooted in halacha."),
    Tzadik(12, "The Rambam (Maimonides)", "Rabbi Moshe ben Maimon (1138–1204), philosopher, physician, and halachist. He authored the Mishneh Torah, an unparalleled codification of Jewish law, and The Guide for the Perplexed, exploring faith and reason. His rationalism and method continue to influence Jewish thought profoundly."),
    Tzadik(13, "The Ramban (Nachmanides)", "Rabbi Moshe ben Nachman (1194–1270), leading commentator and kabbalist who integrated straightforward interpretation with mystical depth. A communal leader and physician, he also defended Judaism in public disputations and inspired generations with his balanced approach to text and spirit."),
    Tzadik(14, "Rabbi Yehuda HaNasi", "Redactor of the Mishnah (2nd–3rd c.) whose monumental work preserved the Oral Torah in written form. A statesman and sage, he fostered unity among scholars and ensured the transmission of Torah for all subsequent generations."),
    // Added 10 more tzadikim
    Tzadik(15, "Rabbi Isaac Luria", "The Ari (1534–1572), whose teachings revolutionized Kabbalah with concepts of tzimtzum, shevirah, and tikkun. His circle in Safed, including Rabbi Chaim Vital, systematized practices and intentions that infused mitzvot with cosmic repair and personal transformation."),
    Tzadik(16, "Rabbi Chaim of Volozhin", "Founding rosh yeshiva of Volozhin and foremost disciple of the Vilna Gaon. In Nefesh HaChaim he articulated a grand vision of Torah study as sustaining the spiritual fabric of the world, pairing rigorous learning with profound yirat Shamayim (awe of Heaven)."),
    Tzadik(17, "Rabbi Elimelech of Lizhensk", "A central Hasidic master whose Noam Elimelech shaped the idea of the tzadik as a spiritual conduit. He emphasized warmth in prayer, meticulous mitzvah observance, and uplifting the simple faith of the people."),
    Tzadik(18, "Rabbi Levi Yitzchak of Berditchev", "The beloved defender of Israel, renowned for his love of every Jew and his bold advocacy on their behalf before Heaven. His teachings radiate joy, compassion, and unshakeable trust in God's kindness."),
    Tzadik(19, "Rabbi Shneur Zalman of Liadi", "The Alter Rebbe (1745–1812), founder of Chabad. In Tanya he mapped the inner landscape of the soul, guiding avodah with mind and heart in harmony. He also authored Shulchan Aruch HaRav, a lucid halachic code."),
    Tzadik(20, "The Maggid of Mezritch", "Rabbi Dov Ber, successor to the Baal Shem Tov, who spread Hasidism through a generation of brilliant disciples. He deepened the movement's contemplative core while building networks that reshaped Jewish life across Eastern Europe."),
    Tzadik(21, "The Chozeh of Lublin", "A prophetic Hasidic leader famed for penetrating spiritual insight. As a teacher of many rebbes, he helped seed diverse courts and approaches, all rooted in the search for God's presence in daily life."),
    Tzadik(22, "The Sfat Emet", "Rabbi Yehudah Aryeh Leib Alter of Ger (1847–1905), whose teachings weave together emunah (faith), toil in Torah, and inner renewal. His discourses reveal layers of meaning in the calendar and parashiyot, calling for authenticity and courage."),
    Tzadik(23, "The Netziv", "Rabbi Naftali Zvi Yehuda Berlin (1816–1893), rosh yeshiva of Volozhin. His Ha'amek Davar commentary highlights careful textual nuance and ethical sensitivity, modeling intellectual breadth anchored in tradition."),
    Tzadik(24, "Rabbi Chaim Kanievsky", "A leading Lithuanian gadol (1928–2022) revered for extraordinary diligence and encyclopedic Torah knowledge. From his modest home he guided multitudes with brief, incisive rulings and an example of humility and unwavering commitment to learning.")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TzadikimPager()
                }
            }
        }
    }
}

@Composable
fun TzadikimPager() {
    val context = LocalContext.current
    val tzadikim = remember { mutableStateListOf<Tzadik>() }

    LaunchedEffect(Unit) {
        // Load local media and description overrides first
        val initial = initialTzadikim.map { base ->
            val candidates = slugCandidates(base.name)
            val video = candidates.firstNotNullOfOrNull { slug -> findLocalVideoAsset(context, slug) }
            val image = candidates.firstNotNullOfOrNull { slug -> findLocalImageAsset(context, slug) }
            val longDesc = candidates.firstNotNullOfOrNull { slug -> findLocalDescriptionAsset(context, slug) }
            val storyDesc = candidates.firstNotNullOfOrNull { slug -> findLocalStoriesAsset(context, slug) }
            base.copy(
                description = longDesc?.text ?: base.description,
                descriptionIsHtml = longDesc?.isHtml ?: false,
                videoUrl = video,
                imageUrl = image,
                story = storyDesc?.text,
                storyIsHtml = storyDesc?.isHtml ?: false
            )
        }
        tzadikim.clear()
        tzadikim.addAll(initial)

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
            FlippableTzadikCard(tzadik = list[page])
        }
    }
}

@Composable
fun FlippableTzadikCard(tzadik: Tzadik) {
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
            TzadikVideo(tzadik, onTap = { isFlipped = !isFlipped })
        } else {
            // Flipped view
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f // Counter-rotate the content
                    },
                contentAlignment = Alignment.Center
            ) {
                TzadikInfo(tzadik, onTap = { isFlipped = !isFlipped })
            }
        }
    }
}

@Composable
fun TzadikVideo(tzadik: Tzadik, onTap: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        val videoUrl = tzadik.videoUrl
        val imageUrl = tzadik.imageUrl
        when {
            videoUrl != null -> {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            settings.apply {
                                javaScriptEnabled = false
                                domStorageEnabled = false
                                mediaPlaybackRequiresUserGesture = false
                                cacheMode = WebSettings.LOAD_NO_CACHE
                            }
                            setBackgroundColor(android.graphics.Color.BLACK)
                        }
                    },
                    update = { webView ->
                        val html = """
                            <html>
                              <head>
                                <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
                                <style>
                                  html,body { margin:0; height:100%; background:black; }
                                  .wrap { position:fixed; inset:0; overflow:hidden; background:black; }
                                  video { position:absolute; top:50%; left:50%; transform:translate(-50%,-50%);
                                          min-width:100%; min-height:100%; width:auto; height:auto; object-fit:cover; }
                                </style>
                              </head>
                              <body>
                                <div class='wrap'>
                                  <video src='${videoUrl}' autoplay loop muted playsinline></video>
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
                            settings.apply {
                                javaScriptEnabled = false
                                domStorageEnabled = false
                                cacheMode = WebSettings.LOAD_NO_CACHE
                            }
                            setBackgroundColor(android.graphics.Color.BLACK)
                        }
                    },
                    update = { webView ->
                        val html = """
                            <html>
                              <head>
                                <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
                                <style>
                                  html,body { margin:0; height:100%; background:black; }
                                  .wrap { position:fixed; inset:0; overflow:hidden; background:black; }
                                  img { position:absolute; top:50%; left:50%; transform:translate(-50%,-50%);
                                        min-width:100%; min-height:100%; width:auto; height:auto; object-fit:cover; }
                                </style>
                              </head>
                              <body>
                                <div class='wrap'>
                                  <img src='${imageUrl}' alt='portrait'/>
                                </div>
                              </body>
                            </html>
                        """.trimIndent()
                        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
                    }
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No media available",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Gradient scrim at bottom for text readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                    )
                )
        )

        Text(
            text = tzadik.name,
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )

        // Transparent tap-capture overlay to enable flipping on top of WebView/image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onTap() })
                }
        )
    }
}

@Composable
fun TzadikInfo(tzadik: Tzadik, onTap: () -> Unit) {
    val hasStory = tzadik.story != null
    val topBarHeight = 56.dp
    var showStory by remember { mutableStateOf(false) }
    var textZoom by remember { mutableStateOf(100) } // 100% default

    if (tzadik.descriptionIsHtml || (hasStory && tzadik.storyIsHtml)) {
        // Full-screen HTML with top controls (Article/Stories, text size) and scroll buttons
        val webViewRef = remember { mutableStateOf<WebView?>(null) }
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = false
                            domStorageEnabled = false
                            cacheMode = WebSettings.LOAD_NO_CACHE
                            textZoom = textZoom
                        }
                        setBackgroundColor(android.graphics.Color.BLACK)
                    }.also { wv -> webViewRef.value = wv }
                },
                update = { webView ->
                    // Choose content based on toggle
                    val htmlContent: String = if (showStory && hasStory) {
                        val s = tzadik.story ?: ""
                        if (tzadik.storyIsHtml) s else "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0'/>" +
                                "<style>body{margin:0;padding:16px;color:#eee;background:#000;font-family:sans-serif;line-height:1.5;white-space:pre-wrap;}</style></head><body>" +
                                escapeHtml(s) + "</body></html>"
                    } else {
                        if (tzadik.descriptionIsHtml) tzadik.description else "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0'/>" +
                                "<style>body{margin:0;padding:16px;color:#eee;background:#000;font-family:sans-serif;line-height:1.5;white-space:pre-wrap;}</style></head><body>" +
                                escapeHtml(tzadik.description) + "</body></html>"
                    }
                    webView.settings.textZoom = textZoom
                    webView.loadDataWithBaseURL(
                        "file:///android_asset/",
                        htmlContent,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            )

            // Top control bar: toggle and text size
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarHeight)
                    .align(Alignment.TopCenter)
                    .background(Color(0xAA000000))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Button(
                        onClick = { showStory = false },
                        enabled = true,
                        modifier = Modifier.padding(end = 8.dp)
                    ) { Text("Article") }
                    if (hasStory) {
                        androidx.compose.material3.Button(
                            onClick = { showStory = true },
                            enabled = true
                        ) { Text("Stories") }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Button(
                        onClick = { textZoom = (textZoom - 10).coerceAtLeast(50); webViewRef.value?.settings?.textZoom = textZoom },
                        modifier = Modifier.padding(end = 8.dp)
                    ) { Text("A-") }
                    androidx.compose.material3.Button(
                        onClick = { textZoom = (textZoom + 10).coerceAtMost(250); webViewRef.value?.settings?.textZoom = textZoom }
                    ) { Text("A+") }
                }
            }

            // Top full-width scroll-up button; long-press flips the card (placed below top bar)
            androidx.compose.material3.Button(
                onClick = { webViewRef.value?.pageUp(false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = topBarHeight)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onTap() }
                        )
                    }
            ) {
                Text(text = "▲ Scroll up")
            }

            // Bottom full-width scroll-down button; long-press also flips the card
            androidx.compose.material3.Button(
                onClick = { webViewRef.value?.pageDown(false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onTap() }
                        )
                    }
            ) {
                Text(text = "▼ Scroll down")
            }
        }
    } else {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
                .pointerInput(Unit) { detectTapGestures(onTap = { onTap() }) },
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = tzadik.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = tzadik.description,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tap anywhere to flip back.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
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
    // Generate tolerant slug candidates to match typical folder names users might create.
    // We accept the canonical slug, and variants without leading "Rabbi " or "The ".
    val variants = LinkedHashSet<String>()
    fun addVariant(n: String) { variants.add(nameToSlug(n)) }

    val original = name.trim()
    val noRabbi = stripLeadingWord(original, "Rabbi ")
    val noThe = stripLeadingWord(original, "The ")
    val noRabbiThenThe = stripLeadingWord(noRabbi, "The ")
    val noTheThenRabbi = stripLeadingWord(noThe, "Rabbi ")

    addVariant(original)
    addVariant(noRabbi)
    addVariant(noThe)
    addVariant(noRabbiThenThe)
    addVariant(noTheThenRabbi)

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
        context.assets.open(path).use { input ->
            input.readBytes().toString(Charsets.UTF_8)
        }
    } catch (_: IOException) {
        null
    }
}

private data class LoadedDescription(val text: String, val isHtml: Boolean)

private fun findLocalDescriptionAsset(context: Context, slug: String): LoadedDescription? {
    val base = "tzadikim/$slug/"
    val html = "description.html"
    val md = "description.md"
    val txt = "description.txt"

    // Prefer HTML if present
    if (assetFileExists(context, base + html)) {
        val text = readAssetText(context, base + html)
        if (text != null) return LoadedDescription(text = text, isHtml = true)
    }
    // Then Markdown (treated as plain text for now)
    if (assetFileExists(context, base + md)) {
        val text = readAssetText(context, base + md)
        if (text != null) return LoadedDescription(text = text, isHtml = false)
    }
    // Then plain text
    if (assetFileExists(context, base + txt)) {
        val text = readAssetText(context, base + txt)
        if (text != null) return LoadedDescription(text = text, isHtml = false)
    }
    return null
}

private fun findLocalStoriesAsset(context: Context, slug: String): LoadedDescription? {
    val base = "tzadikim/$slug/"
    val html = "stories.html"
    val md = "stories.md"
    val txt = "stories.txt"

    if (assetFileExists(context, base + html)) {
        val text = readAssetText(context, base + html)
        if (text != null) return LoadedDescription(text = text, isHtml = true)
    }
    if (assetFileExists(context, base + md)) {
        val text = readAssetText(context, base + md)
        if (text != null) return LoadedDescription(text = text, isHtml = false)
    }
    if (assetFileExists(context, base + txt)) {
        val text = readAssetText(context, base + txt)
        if (text != null) return LoadedDescription(text = text, isHtml = false)
    }
    return null
}

private fun escapeHtml(text: String): String {
    return text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")
}

private fun findLocalVideoAsset(context: Context, slug: String): String? {
    // Look under app/src/main/assets/tzadikim/<slug>/ for these filenames
    val base = "tzadikim/$slug/"
    val candidates = listOf(
        "clip.mp4",
        "video.mp4",
        "portrait.mp4"
    )
    val found = candidates.firstOrNull { name -> assetFileExists(context, base + name) }
    return found?.let { name -> "file:///android_asset/" + base + name }
}

private fun findLocalImageAsset(context: Context, slug: String): String? {
    // If no MP4 is present, we can use a specially named image file
    // Look under app/src/main/assets/tzadikim/<slug>/ for these filenames
    val base = "tzadikim/$slug/"
    val candidates = listOf(
        "portrait.webp",
        "portrait.jpg",
        "portrait.jpeg",
        "portrait.png"
    )
    val found = candidates.firstOrNull { name -> assetFileExists(context, base + name) }
    return found?.let { name -> "file:///android_asset/" + base + name }
}

