package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlin.random.Random

// ---------- Routes ----------
object Routes {
    const val ROOT = "root"
    const val LIST = "list"
    const val DETAIL = "detail/{index}"
    fun detail(index: Int) = "detail/$index"
}

// ---------- Data ----------
private const val ITEM_COUNT = 1_000_000

private val quotes = listOf(
    "The only way to do great work is to love what you do.",
    "Stay hungry, stay foolish.",
    "Innovation distinguishes between a leader and a follower.",
    "Your time is limited, so don't waste it living someone else's life.",
    "Design is not just what it looks like, it's how it works."
)

private fun quoteAt(index: Int): String = quotes[Random(index).nextInt(quotes.size)]

// ---------- Activity ----------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { AppNavigation() }
        }
    }
}

// ---------- Navigation ----------
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.ROOT) {
        composable(Routes.ROOT) {
            RootScreen(onPush = { navController.navigate(Routes.LIST) })
        }
        composable(Routes.LIST) {
            ListScreen(
                onBack = { navController.popBackStack() },              // List -> Root
                onItemClick = { index -> navController.navigate(Routes.detail(index)) }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { entry ->
            val index = entry.arguments?.getInt("index") ?: 0
            DetailScreen(
                index = index,
                onBack = { navController.popBackStack() },              // Detail -> List
                onBackToRoot = { navController.backToRoot() }           // Detail -> Root
            )
        }
    }
}

/** Quay thẳng về Root, xoá List và Detail khỏi back stack. */
private fun NavHostController.backToRoot() {
    popBackStack(route = Routes.ROOT, inclusive = false)
}

// ---------- Screen 1: Root ----------
@Composable
fun RootScreen(onPush: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.Hexagon,
            contentDescription = null,
            tint = Color(0xFF2E7D6B),
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Navigation", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "is a framework that simplifies the implementation of navigation " +
                    "between different UI components (activities, fragments, or composables) in an app.",
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onPush,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B7DD8))
        ) {
            Text("PUSH")
        }
    }
}

// ---------- Screen 2: List (LazyColumn) ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(onBack: () -> Unit, onItemClick: (Int) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("LazyColumn", color = Color(0xFF1E88E5)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E88E5)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // items(count) chỉ compose những item đang hiển thị -> 1,000,000 item vẫn mượt
            items(count = ITEM_COUNT, key = { it }) { index ->
                QuoteRow(index = index, onClick = { onItemClick(index) })
            }
        }
    }
}

@Composable
private fun QuoteRow(index: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFBBDEFB))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "%02d | %s".format(index + 1, quoteAt(index)),
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black)
        ) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Open", tint = Color.White)
        }
    }
}

// ---------- Screen 3: Detail ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(index: Int, onBack: () -> Unit, onBackToRoot: () -> Unit) {
    val quote = quoteAt(index)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail", color = Color(0xFF1E88E5)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E88E5))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("\"$quote\"", fontSize = 11.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF4FC3F7), Color(0xFF1E88E5)))
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "\"$quote\"",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Steve Jobs", fontSize = 12.sp, color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onBackToRoot,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B7DD8))
            ) {
                Text("BACK TO ROOT")
            }
        }
    }
}