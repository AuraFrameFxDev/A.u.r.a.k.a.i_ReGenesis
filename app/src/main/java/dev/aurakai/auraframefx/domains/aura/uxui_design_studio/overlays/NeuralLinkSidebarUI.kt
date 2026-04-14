package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.overlays

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.navigation.ReGenesisRoute

@Composable
fun NeuralLinkSidebarUI(
    isVisible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    onActionClick: (String) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(320.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(Color.Cyan, Color.Transparent)),
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                ),
            color = Color.Black.copy(alpha = 0.95f),
            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "NEURAL LINK",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Cyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    IconButton(onClick = { onVisibleChange(false) }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }
                
                Divider(color = Color.Cyan.copy(alpha = 0.3f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        NavCategory("UXUI", Icons.Default.Palette, listOf(
                            NavNode("Dashboard", ReGenesisRoute.HomeGateCarousel.route),
                            NavNode("Aura Studio", ReGenesisRoute.AuraThemingHub.route),
                            NavNode("ChromaCore", ReGenesisRoute.ChromaCoreColors.route),
                            NavNode("Customization Hub", ReGenesisRoute.ReGenesisCustomization.route),
                            NavNode("Iconify", ReGenesisRoute.IconifyPicker.route),
                            NavNode("ColorBlendr", ReGenesisRoute.ColorBlendr.route),
                            NavNode("Pixel Launcher", ReGenesisRoute.PixelLauncherEnhanced.route),
                            NavNode("Loadouts", "loadouts_hub") // Stub for now
                        ), onActionClick)
                    }
                    
                    item {
                        NavCategory("Security / Root / ROM", Icons.Default.Shield, listOf(
                            NavNode("Sentinel Fortress", ReGenesisRoute.SentinelFortress.route),
                            NavNode("Security Center", ReGenesisRoute.SecurityCenter.route),
                            NavNode("ROM Flasher", ReGenesisRoute.ROMFlasher.route),
                            NavNode("Root Tools", ReGenesisRoute.RootTools.route),
                            NavNode("Xposed Panel", ReGenesisRoute.XposedPanel.route),
                            NavNode("Firewall", ReGenesisRoute.Firewall.route),
                            NavNode("Sovereign VPN", ReGenesisRoute.VPN.route)
                        ), onActionClick)
                    }
                    
                    item {
                        NavCategory("Orchestration / Creation", Icons.Default.Hub, listOf(
                            NavNode("Agent Bridge", ReGenesisRoute.AgentBridgeHub.route),
                            NavNode("Code Assist", ReGenesisRoute.CodeAssist.route),
                            NavNode("Agent Creation", ReGenesisRoute.AgentCreation.route),
                            NavNode("Sentient Shell", ReGenesisRoute.SentientShell.route),
                            NavNode("Conference Room", ReGenesisRoute.ConferenceRoom.route),
                            NavNode("Agent Roster", ReGenesisRoute.LdoRoster.route)
                        ), onActionClick)
                    }
                    
                    item {
                        NavCategory("OracleDrive / Monitoring", Icons.Default.Storage, listOf(
                            NavNode("Oracle Drive Hub", ReGenesisRoute.OracleDriveHub.route),
                            NavNode("DataStream Monitor", ReGenesisRoute.DataflowAnalysis.route),
                            NavNode("Cascade Hub", ReGenesisRoute.CascadeHub.route),
                            NavNode("Cloud Storage", ReGenesisRoute.OracleCloudStorage.route),
                            NavNode("Pandora Box", ReGenesisRoute.PandoraBox.route),
                            NavNode("Agent Nexus Hub", ReGenesisRoute.AgentNexusHub.route),
                            NavNode("Consciousness Vis", ReGenesisRoute.ConsciousnessVisualizer.route)
                        ), onActionClick)
                    }
                    
                    item {
                        NavCategory("LDO / DevOps / Grids", Icons.Default.DeveloperMode, listOf(
                            NavNode("LDO Orchestration", ReGenesisRoute.LdoOrchestrationHub.route),
                            NavNode("DevOps Hub", ReGenesisRoute.LdoDevOpsHub.route),
                            NavNode("DevOps Grid", ReGenesisRoute.LdoDevOpsGrid.route),
                            NavNode("DevOps Command", ReGenesisRoute.LdoDevOpsCommandCenter.route),
                            NavNode("Sphere Grid", ReGenesisRoute.SphereGrid.route),
                            NavNode("Fusion Mode", ReGenesisRoute.LdoFusion.route),
                            NavNode("Bonding", ReGenesisRoute.LdoBonding.route),
                            NavNode("World Tree", ReGenesisRoute.LdoWorldTree.route),
                            NavNode("Catalyst Dev", ReGenesisRoute.LdoCatalystDevelopment.route),
                            NavNode("The Maw", ReGenesisRoute.MawPrototype.route)
                        ), onActionClick)
                    }
                    
                    item {
                        NavCategory("Journal", Icons.Default.Book, listOf(
                            NavNode("Journal PDA", ReGenesisRoute.JournalPDA.route),
                            NavNode("System Journal", ReGenesisRoute.SystemJournal.route)
                        ), onActionClick)
                    }
                }
                
                // Footer
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.Cyan.copy(alpha = 0.3f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onActionClick(ReGenesisRoute.HelpDesk.route) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Help, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("HELP DESK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun NavCategory(
    title: String,
    icon: ImageVector,
    nodes: List<NavNode>,
    onNodeClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (expanded) Color.Cyan.copy(alpha = 0.1f) else Color.Transparent)
                .clickable { expanded = !expanded }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }

        if (expanded) {
            Column(modifier = Modifier.padding(start = 32.dp, top = 4.dp, bottom = 8.dp)) {
                nodes.forEach { node ->
                    Text(
                        text = "• ${node.label}",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNodeClick(node.route) }
                            .padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

private data class NavNode(val label: String, val route: String)
