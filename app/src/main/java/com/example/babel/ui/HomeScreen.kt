package com.example.babel.ui

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.babel.data.BookLoader
import com.example.babel.models.Book
import kotlinx.coroutines.delay
import kotlin.random.Random


@Composable
fun HomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { TopBar(navController, containerColor = Color.Transparent) },
        bottomBar = { BottomBar(navController, containerColor = Color.Transparent) },
        containerColor = Color.Transparent // keep your background visible
    ) { paddingValues ->
        // Preserve your current layout exactly
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedBackground()

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                GreetingSection()
                Spacer(modifier = Modifier.height(24.dp))
                FeaturedCarousel(navController = navController)
                Spacer(modifier = Modifier.height(32.dp))
                CategoryGrid(navController = navController)
            }
        }
    }
}

// -------------------- Top Bar ----------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, containerColor : Color) {
    val colorScheme = MaterialTheme.colorScheme
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor
        ),
        title = {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search books...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 60.dp)
                    .height(48.dp),
                // Corrected line:
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surfaceVariant,
                    unfocusedContainerColor = colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        },
        actions = {
            Text(
                text = "Profile",
                color = colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        navController.navigate("profile")
                    },
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    )
}

// --------------- Bottom Bar ----------------------
@Composable
fun BottomBar(navController: NavController, containerColor: Color) {
    val items = listOf("Home", "My Library", "Explore", "Journal", "Stats", "Settings")

    NavigationBar(containerColor = containerColor) {
        items.forEach { label ->
            NavigationBarItem(
                selected = false, // you can hook this up with current route if you wish
                onClick = {
                    when (label) {
                        "Home" -> navController.navigate("home")
                        "My Library" -> navController.navigate("library")
                        "Explore" -> navController.navigate("explore")
                        "Journal" -> navController.navigate("journal")
                        "Stats" -> navController.navigate("stats")
                        "Settings" -> navController.navigate("settings")
                    }
                },
                icon = {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(4.dp)
                    )
                },
                label = { Text(text = label, fontSize = 12.sp) }
            )
        }
    }
}

// -------------------- Greeting Section --------------------

@Composable
fun GreetingSection() {
    val colorScheme = MaterialTheme.colorScheme
    val messages = listOf(
        "Welcome back, Wanderer",
        "The library awaits your return",
        "Another story calls your name...",
        "The pages whisper secrets tonight..."
    )
    var currentMessage by remember { mutableStateOf(messages.random()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentMessage = messages.random()
        }
    }

    // Fixed height and fade animation
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp), // holds consistent space
        contentAlignment = Alignment.CenterStart
    ) {
        AnimatedContent(
            targetState = currentMessage,
            transitionSpec = {
                fadeIn(animationSpec = tween(2000)) togetherWith fadeOut(animationSpec = tween(2000, delayMillis = 50))
            }, label = ""
        ) { message ->
            Column {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "What shall we discover today?",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = colorScheme.secondary
                    )
                )
            }
        }
    }
}

// -------------------- Featured Carousel --------------------

@Composable
fun FeaturedCarousel(navController: NavController) {
    val context = LocalContext.current
    val sampleBooks by remember {  mutableStateOf(BookLoader.loadSampleBooks(context)) }

    Text(
        text = "Featured",
        style = MaterialTheme.typography.titleLarge.copy(
            color = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier.padding(bottom = 16.dp)
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(sampleBooks) { book ->
            CarouselCard(book, navController)
        }
    }
}

@Composable
fun CarouselCard(book: Book, navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val scale = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .scale(scale.value)
            .size(width = 180.dp, height = 260.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(colorScheme.surfaceVariant)
            .pointerInput(book.title) {
                detectTapGestures(
                    onTap = {
                        Log.d("NavTest", "Clicked ${book.id}")
                        navController.navigate("bookDetail/${book.id}")
                    },
                    onPress = {
                        scale.animateTo(0.95f, tween(100))
                        tryAwaitRelease()
                        scale.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
                    }
                )
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        // --- Book Cover ---
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(book.coverImage)
                .crossfade(true)
                .build(),
            contentDescription = book.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // --- Subtle gradient overlay for readability ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colorScheme.surfaceVariant.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // --- Text Info ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = book.title,
                color = colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
            Text(
                text = book.authors.joinToString(", "),
                color = colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}


// -------------------- Category Grid --------------------

@Composable
fun CategoryGrid(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val categories = listOf(
        "Your Library",
        "Discover",
        "Stats",
        "Nearby Readers"
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        for (category in categories) {
            CategoryCard(title = category, colorScheme = colorScheme) {
                // TODO: navigate to respective screen
            }
        }
    }
}

@Composable
fun CategoryCard(title: String, colorScheme: ColorScheme, onClick: () -> Unit) {
    val gradient = Brush.horizontalGradient(
        listOf(
            colorScheme.tertiaryContainer,
            colorScheme.primaryContainer
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            color = colorScheme.onPrimaryContainer,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// -------------------- Animated Background --------------------

@Composable
fun AnimatedBackground() {
    val colorScheme = MaterialTheme.colorScheme
    val particles = remember {
        List(25) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 6 + 2,
                speed = Random.nextFloat() * 0.002f + 0.0005f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        particles.forEach { p ->
            val newY = (p.y + p.speed) % 1f
            drawCircle(
                color = colorScheme.secondary.copy(alpha = 0.08f),
                radius = p.size,
                center = Offset(p.x * width, newY * height)
            )
            p.y = newY
        }
    }
}

data class Particle(
    var x: Float,
    var y: Float,
    var size: Float,
    var speed: Float
)
