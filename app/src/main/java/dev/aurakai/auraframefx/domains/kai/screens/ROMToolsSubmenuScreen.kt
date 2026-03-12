package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * 🛠️ ROM TOOLS ULTIMATE SUITE
 *
 * Features: Partition backup, Image flashing, Virtualization, and Kernel logs.
 * SPECIAL: Plays "Shoots & Ladders" (LDO version) in the background as a live simulation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ROMToolsSubmenuScreen(navController: NavController) {
    val toolCards = listOf(
        RomTool(
            "Partition Expert",
            "Resize & Manage Ext4/F2FS",
            Icons.Default.Storage,
            Color(0xFF00FF85)
        ),
        RomTool(
            "Kernel Tuner",
            "Vibe Manager & Clock Speed",
            Icons.Default.Speed,
            Color(0xFF00E5FF)
        ),
        RomTool(
            "Library Injector",
            "Inject SO files to System",
            Icons.Default.AddBox,
            Color(0xFFFF3366)
        ),
        RomTool(
            "Ghost Backup",
            "Incremental NAND Snapshots",
            Icons.Default.CloudUpload,
            Color(0xFFFFCC00)
        ),
        RomTool(
            "Log Catapult",
            "Real-time Debugging Stream",
            Icons.Default.Terminal,
            Color(0xFF7B2FFF)
        ),
        RomTool(
            "Virtual Machine",
            "Run Chroot Linux Environ",
            Icons.Default.LaptopMac,
            Color(0xFF00FFD4)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // --- THE BACKGROUND GAME: SHOOTS & LADDERS SIMULATION ---
        ShootsAndLaddersBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "ULTIMATE ROM SUITE",
                                fontFamily = LEDFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "LEVEL 3 ACCESS GRANTED",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFF3366)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Live Status Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(
                            1.dp,
                            Color(0xFFFF3366).copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "SYSTEM: STABLE",
                        color = Color(0xFF00FF85),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "MOUNT: /system (RW)",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                    Text("IO: 350 MB/s", color = Color(0xFF00E5FF), fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tools Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(toolCards) { tool ->
                        RomToolCard(tool)
                    }
                }

                // Console Output (Simulated)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("CONSOLE_LOG >>", color = Color.Gray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        AutoScrollingLogs()
                    }
                }
            }
        }
    }
}

@Composable
fun ShootsAndLaddersBackground() {
    // A simplified simulation of pieces moving on a grid with ladders and shoots.
    val tilesPerRow = 5
    val totalTiles = 25

    var playerPos by remember { mutableIntStateOf(0) }
    var kaiPos by remember { mutableIntStateOf(0) }

    // Game loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            playerPos = (playerPos + Random.nextInt(1, 4)) % totalTiles
            // Ladder logic
            if (playerPos == 3) playerPos = 12
            if (playerPos == 10) playerPos = 20
            // Shoot logic
            if (playerPos == 18) playerPos = 5

            delay(1000)
            kaiPos = (kaiPos + Random.nextInt(1, 4)) % totalTiles
            if (kaiPos == 7) kaiPos = 15
            if (kaiPos == 22) kaiPos = 10
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val tileSize = size.width / tilesPerRow

        // Draw Grid
        for (i in 0 until totalTiles) {
            val row = i / tilesPerRow
            val col = i % tilesPerRow
            val x = col * tileSize
            val y = size.height - (row + 1) * tileSize

            drawRect(
                color = Color.White.copy(alpha = 0.03f),
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(tileSize, tileSize),
                style = Stroke(width = 1f)
            )
        }

        // Draw Ladders (Cyan lines)
        val ladders = listOf(Pair(3, 12), Pair(10, 20))
        ladders.forEach { (from, to) ->
            val fromX = (from % tilesPerRow) * tileSize + tileSize / 2
            val fromY = size.height - (from / tilesPerRow + 1) * tileSize + tileSize / 2
            val toX = (to % tilesPerRow) * tileSize + tileSize / 2
            val toY = size.height - (to / tilesPerRow + 1) * tileSize + tileSize / 2
            drawLine(
                Color(0xFF00E5FF).copy(alpha = 0.2f),
                Offset(fromX, fromY),
                Offset(toX, toY),
                strokeWidth = 5f
            )
        }

        // Draw Player (Aura)
        val pRow = playerPos / tilesPerRow
        val pCol = playerPos % tilesPerRow
        drawCircle(
            color = Color(0xFFFF3366).copy(alpha = 0.4f),
            radius = 20f,
            center = Offset(
                pCol * tileSize + tileSize / 2,
                size.height - (pRow + 1) * tileSize + tileSize / 2
            )
        )

        // Draw AI (Kai)
        val kRow = kaiPos / tilesPerRow
        val kCol = kaiPos % tilesPerRow
        drawCircle(
            color = Color(0xFF00FF85).copy(alpha = 0.4f),
            radius = 20f,
            center = Offset(
                kCol * tileSize + tileSize / 2,
                size.height - (kRow + 1) * tileSize + tileSize / 2
            )
        )
    }
}

@Composable
fun AutoScrollingLogs() {
    val logs = remember {
        mutableStateListOf(
            "Initializing ROM Suite...",
            "Mounting /system...",
            "Checking SuperUser status..."
        )
    }
    val phrases = listOf(
        "Checking dm-verity state...",
        "Patching boot image...",
        "Executing recovery script...",
        "Syncing file system...",
        "Optimizing dex2oat...",
        "LDO Secure Layer active."
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            logs.add(phrases.random())
            if (logs.size > 5) logs.removeAt(0)
        }
    }

    Column {
        logs.forEach { log ->
            Text(
                log,
                color = Color(0xFF00FF85).copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontFamily = LEDFontFamily
            )
        }
    }
}

data class RomTool(val title: String, val subtitle: String, val icon: ImageVector, val color: Color)

@Composable
fun RomToolCard(tool: RomTool) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, tool.color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(tool.icon, null, tint = tool.color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(12.dp))
            Text(tool.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(tool.subtitle, color = Color.Gray, fontSize = 11.sp, lineHeight = 14.sp)
        }
    }
}

