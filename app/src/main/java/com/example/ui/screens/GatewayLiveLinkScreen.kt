package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.ParkingViewModel
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GatewayLiveLinkScreen(
    viewModel: ParkingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gatewayConfig by viewModel.gatewayConfig.collectAsState()
    val parkingState by viewModel.parkingState.collectAsState()

    var customUrlInput by remember(gatewayConfig.endpointUrl) {
        mutableStateOf(gatewayConfig.endpointUrl)
    }

    var publicWebUrl by remember {
        mutableStateOf("https://your-username.github.io/smart-parking/")
    }

    var showInAppWebPreview by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Gateway Toggle Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, if (gatewayConfig.enabled) ElectricCyan.copy(alpha = 0.5f) else DarkSurfaceBorder, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (gatewayConfig.enabled) ElectricCyan.copy(alpha = 0.2f) else DarkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Gateway",
                                tint = if (gatewayConfig.enabled) ElectricCyan else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "IoT Phone Gateway Sync",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (gatewayConfig.enabled) "Relaying Bluetooth to Cloud in real-time" else "Gateway paused",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (gatewayConfig.enabled) NeonEmerald else TextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = gatewayConfig.enabled,
                        onCheckedChange = { viewModel.toggleGateway(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ElectricCyan
                        ),
                        modifier = Modifier.testTag("gateway_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Gateway Telemetry KPIs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurface)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "PACKETS SENT", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(text = "${gatewayConfig.totalPacketsSent}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = ElectricCyan)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurface)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "STATUS", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (gatewayConfig.lastStatusCode > 0) "HTTP ${gatewayConfig.lastStatusCode}" else "Active",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonEmerald
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurface)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "SYNC FEEDBACK", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(
                                text = gatewayConfig.lastStatusMessage,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Public Live Link (GitHub Pages) Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, NeonEmerald.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Public Link",
                            tint = NeonEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Public Live Link (GitHub Pages)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Anyone can view live parking status in browser",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Editable Live URL Box
                OutlinedTextField(
                    value = publicWebUrl,
                    onValueChange = { publicWebUrl = it },
                    label = { Text("Public Dashboard Web Link") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("public_url_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy Link Button
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Smart Parking Live Link", publicWebUrl)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Live Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = DarkBg
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("copy_link_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Share Link Button
                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Check out the live 3-slot parking status: $publicWebUrl")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Smart Parking Link"))
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("share_link_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle In-App Web Preview
                OutlinedButton(
                    onClick = { showInAppWebPreview = !showInAppWebPreview },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("toggle_preview_btn")
                ) {
                    Icon(
                        imageVector = if (showInAppWebPreview) Icons.Default.OpenInBrowser else Icons.Default.Language,
                        contentDescription = "Preview",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showInAppWebPreview) "Hide Live Web Preview" else "Preview Live Web Page In App",
                        fontSize = 12.sp
                    )
                }

                // In-App Web Preview Container
                if (showInAppWebPreview) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    webViewClient = WebViewClient()
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    // Load the embedded web dashboard HTML
                                    loadUrl("file:///android_asset/web_dashboard.html")
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Firebase Cloud Endpoint Settings Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Firebase Database REST Endpoint",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Phone sends JSON payload to this endpoint via HTTP PUT",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customUrlInput,
                    onValueChange = {
                        customUrlInput = it
                        viewModel.updateGatewayEndpoint(it)
                    },
                    label = { Text("Firebase URL") },
                    modifier = Modifier.fillMaxWidth().testTag("firebase_endpoint_field"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.testCloudSync() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan.copy(alpha = 0.2f),
                            contentColor = ElectricCyan
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("test_sync_btn")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Test", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Force Test Sync", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "Last: ${if (gatewayConfig.lastSyncTime > 0) "${(System.currentTimeMillis() - gatewayConfig.lastSyncTime) / 1000}s ago" else "Never"}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // GitHub Pages 2-Minute Deployment Guide Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "How to Host on GitHub Pages (Free)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "The web app file 'docs/index.html' is already prepared in your project!\n\n" +
                            "1. Create a GitHub repository (e.g. 'smart-parking')\n" +
                            "2. Push your project or upload 'docs/index.html'\n" +
                            "3. Go to GitHub Repo > Settings > Pages\n" +
                            "4. Under Branch, select 'main' and folder '/docs' (or root)\n" +
                            "5. Click Save — Your public URL will be live!",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Live JSON Payload Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Live Payload Preview (JSON)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF070A0F))
                        .padding(10.dp)
                ) {
                    val payload = if (gatewayConfig.lastPayloadJson.isNotEmpty()) {
                        gatewayConfig.lastPayloadJson
                    } else {
                        viewModel.gatewaySyncManager.createJsonPayload(parkingState).toString(2)
                    }
                    Text(
                        text = payload,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = ElectricCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
