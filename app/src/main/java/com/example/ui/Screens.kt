@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.util.*

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InvitationEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import kotlin.math.roundToInt

// --- 1. SPLASH SCREEN ---

@Composable
fun SplashScreen(onNavigateToNext: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
        delay(800)
        onNavigateToNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MidnightSlate, RoyalCrimson, MidnightSlate)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Infinity Logo Card
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .rotate(rotation.value)
                    .drawBehind {
                        // Elegant background circle
                        drawCircle(
                            Brush.radialGradient(
                                colors = listOf(RoyalGold, Color.Transparent),
                                radius = size.minDimension * 0.7f
                            ),
                            radius = size.minDimension * 0.5f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AllInclusive,
                    contentDescription = "Infinity Logo",
                    tint = RoyalGold,
                    modifier = Modifier.size(96.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "INFINITY INVITATION",
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    letterSpacing = 2.sp,
                    color = RoyalGold,
                    shadow = Shadow(color = Color.Black, offset = Offset(2f, 2f), blurRadius = 4f)
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Create Beautiful Invitations in Minutes",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = CreamWhite.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            CircularProgressIndicator(color = RoyalGold, strokeWidth = 3.dp)
        }
    }
}

// --- 2. ONBOARDING SCREEN ---

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var currentSlide by remember { mutableStateOf(0) }
    val slides = listOf(
        OnboardingSlideData(
            title = "Beautiful Templates",
            titleBn = "সুন্দর টেমপ্লেট লাইব্রেরি",
            desc = "Choose from hundreds of premium wedding, birthday, and festival designs crafted for every occasion.",
            descBn = "জন্মদিন, বিয়ে এবং যেকোনো উৎসবের জন্য চমৎকার সব প্রি-ডিজাইন টেমপ্লেট থেকে বেছে নিন নিমেষেই।",
            icon = Icons.Default.Style
        ),
        OnboardingSlideData(
            title = "Realtime Customizer",
            titleBn = "রিয়েলটাইম এডিটর",
            desc = "Drag, scale, and rotate stickers or text. Customize gradients, background presets, and fonts with ease.",
            descBn = "স্টিকার এবং টেক্সট ড্র্যাগ করে সাজান। নিজের পছন্দমতো ফন্ট, কালার গ্রেডিয়েন্ট এবং সাইজ পরিবর্তন করুন সহজেই।",
            icon = Icons.Default.Gesture
        ),
        OnboardingSlideData(
            title = "Smart Offline Access",
            titleBn = "স্মার্ট অফলাইন এডিটিং",
            desc = "Create, save, and export high-resolution invitations in PNG or PDF. Works 100% offline without internet.",
            descBn = "সম্পূর্ণ অফলাইনে ইন্টারনেট ছাড়াই কার্ড ডিজাইন করুন, ড্রাফট সেভ করুন এবং হাই-কোয়ালিটি ইমেজ বা পিডিএফ এক্সপোর্ট করুন।",
            icon = Icons.Default.WifiOff
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightSlate)
            .padding(24.dp)
            .navigationBarsPadding()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Skip Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onFinished) {
                    Text("Skip", color = RoyalGold, fontSize = 16.sp)
                }
            }

            // Slide Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(DeepNavy, CircleShape)
                        .border(2.dp, RoyalGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = slides[currentSlide].icon,
                        contentDescription = null,
                        tint = RoyalGold,
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = slides[currentSlide].title,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = RoyalGold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = slides[currentSlide].desc,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        color = CreamWhite.copy(alpha = 0.8f),
                        lineHeight = 24.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Bottom Navigation Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Slide Indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    slides.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (index == currentSlide) 24.dp else 8.dp, height = 8.dp)
                                .background(
                                    color = if (index == currentSlide) RoyalGold else CoolGray,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Button
                Button(
                    onClick = {
                        if (currentSlide < slides.size - 1) {
                            currentSlide++
                        } else {
                            onFinished()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_next_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (currentSlide == slides.size - 1) "Get Started" else "Next",
                        color = MidnightSlate,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

data class OnboardingSlideData(
    val title: String,
    val titleBn: String,
    val desc: String,
    val descBn: String,
    val icon: ImageVector
)

// --- 3. HOME SCREEN ---

@Composable
fun HomeScreen(
    viewModel: InvitationViewModel,
    onNavigateToTemplates: () -> Unit,
    onNavigateToEditor: () -> Unit,
    onNavigateToEventManager: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val drafts by viewModel.allInvitations.collectAsState()
    val favorites by viewModel.favoriteInvitations.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    
    val isBn = viewModel.language == "bn"

    val categories = listOf(
        HomeCategory("Wedding", "বিয়ে", Icons.Default.Favorite, RoyalCrimson),
        HomeCategory("Birthday", "জন্মদিন", Icons.Default.Cake, VibrantGold),
        HomeCategory("Anniversary", "বার্ষিকী", Icons.Default.Star, SoftPink),
        HomeCategory("Baby Shower", "বেবি শাওয়ার", Icons.Default.ChildCare, Color(0xFF4DABF7)),
        HomeCategory("Eid", "ঈদ মোবারক", Icons.Default.Brightness3, Color(0xFF2B8A3E)),
        HomeCategory("Diwali", "দীপাবলি", Icons.Default.Light, Color(0xFFE8590C)),
        HomeCategory("Christmas", "ক্রিসমাস", Icons.Default.Celebration, Color(0xFFC92A2A)),
        HomeCategory("Ramadan Iftar", "ইফতার", Icons.Default.MenuBook, Color(0xFF0C8599)),
        HomeCategory("Business", "বিজনেস ইভেন্ট", Icons.Default.BusinessCenter, CoolGray)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (viewModel.isDarkMode) MidnightSlate else CreamWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "ইনফিনিটি ইনভিটেশন" else "Infinity Invitation",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = RoyalGold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleLanguage() }) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Language",
                            tint = RoyalGold
                        )
                    }
                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (viewModel.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme Toggle",
                            tint = RoyalGold
                        )
                    }
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About App",
                            tint = RoyalGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (viewModel.isDarkMode) MidnightSlate else CreamWhite
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.startNewBlank()
                    onNavigateToEditor()
                },
                containerColor = RoyalGold,
                contentColor = LightGold,
                modifier = Modifier.testTag("create_blank_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New Invitation")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(colors = listOf(RoseBlush, LightGold)))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.65f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isBn) "আই অ্যাসিস্ট্যান্ট রাইটার" else "AI Assisted Designer",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MidnightSlate
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBn) "সহজে ও দ্রুত নিখুঁত আমন্ত্রণ বাণী তৈরি করুন" else "Compose beautiful templates powered by smart fallbacks.",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = MidnightSlate.copy(alpha = 0.75f)
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onNavigateToTemplates,
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    if (isBn) "শুরু করুন" else "Get Started",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        // Decorative Background image placeholder / sticker icon
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = RoyalGold.copy(alpha = 0.15f),
                            modifier = Modifier
                                .size(120.dp)
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                        )
                    }
                }
            }

            // Categories Header
            item {
                Text(
                    text = if (isBn) "ইভেন্ট ক্যাটাগরি" else "Event Categories",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (viewModel.isDarkMode) RoyalGold else MidnightSlate
                    )
                )
            }

            // Categories horizontal slider
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    categories.forEach { cat ->
                        Card(
                            modifier = Modifier
                                .width(110.dp)
                                .clickable {
                                    // Navigate to templates with category
                                    onNavigateToTemplates()
                                },
                            colors = CardDefaults.cardColors(containerColor = cat.color.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, cat.color.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(cat.color, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = cat.name,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (isBn) cat.nameBn else cat.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (viewModel.isDarkMode) CreamWhite else MidnightSlate,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Saved Projects Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "আমার ড্রাফট ও ডিজাইন সমূহ" else "My Saved Projects",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (viewModel.isDarkMode) RoyalGold else MidnightSlate
                        )
                    )
                    Badge(containerColor = RoyalGold) {
                        Text("${drafts.size}", color = MidnightSlate, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
                    }
                }
            }

            // Drafts listing
            if (drafts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = DeepNavy.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = CoolGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isBn) "কোনো সংরক্ষিত ড্রাফট পাওয়া যায়নি" else "No saved drafts yet",
                                fontWeight = FontWeight.Medium,
                                color = CoolGray,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isBn) "পছন্দের টেমপ্লেট বেছে নিয়ে শুরু করুন" else "Select a template above to start customizing!",
                                color = CoolGray.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                items(drafts) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.loadInvitationForEditing(item)
                                onNavigateToEditor()
                            },
                        colors = CardDefaults.cardColors(containerColor = if (viewModel.isDarkMode) DeepNavy else Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(0.5.dp, if (viewModel.isDarkMode) CoolGray else Color.LightGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Category Icon representation
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(
                                        Brush.sweepGradient(
                                            colors = listOf(RoyalGold, RoyalCrimson, RoyalGold)
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (item.category) {
                                        "Wedding" -> Icons.Default.Favorite
                                        "Birthday" -> Icons.Default.Cake
                                        "Anniversary" -> Icons.Default.Star
                                        "Baby Shower" -> Icons.Default.ChildCare
                                        "Eid" -> Icons.Default.Brightness3
                                        "Diwali" -> Icons.Default.Light
                                        "Christmas" -> Icons.Default.Celebration
                                        else -> Icons.Default.EventNote
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (viewModel.isDarkMode) CreamWhite else MidnightSlate,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${item.category} • ${item.date}",
                                    fontSize = 12.sp,
                                    color = CoolGray,
                                    maxLines = 1
                                )
                            }

                            // Interactive Row Actions
                            Row {
                                IconButton(onClick = { viewModel.toggleFavorite(item) }) {
                                    Icon(
                                        imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Favorite",
                                        tint = if (item.isFavorite) RoyalGold else CoolGray
                                    )
                                }
                                
                                IconButton(onClick = { viewModel.duplicateInvitation(item) }) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Duplicate",
                                        tint = CoolGray
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteInvitation(item) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = RoyalCrimson
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Event Planner quick link
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (drafts.isNotEmpty()) {
                                viewModel.loadInvitationForEditing(drafts.first())
                                onNavigateToEventManager()
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        if (isBn) "প্রথমে একটি আমন্ত্রণ কার্ড তৈরি করুন!" else "Create an invitation draft first!",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = RoyalGold.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, RoyalGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = "Event Manager",
                            tint = RoyalGold,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = if (isBn) "ইভেন্ট ও অতিথি ম্যানেজার" else "Event & Guest Manager",
                                fontWeight = FontWeight.Bold,
                                color = if (viewModel.isDarkMode) CreamWhite else MidnightSlate,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isBn) "অতিথি তালিকা, বাজেট ট্র্যাকার এবং কাজ সমূহ" else "Manage RSVP tracker, checklist & budget",
                                color = CoolGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

data class HomeCategory(
    val name: String,
    val nameBn: String,
    val icon: ImageVector,
    val color: Color
)

// --- 4. TEMPLATE LIBRARY SCREEN ---

@Composable
fun TemplateLibraryScreen(
    viewModel: InvitationViewModel,
    onNavigateToEditor: () -> Unit,
    onBack: () -> Unit
) {
    val isBn = viewModel.language == "bn"
    var selectedCategory by remember { mutableStateOf("All") }
    
    val categories = listOf("All", "Wedding", "Birthday", "Anniversary", "Baby Shower", "Eid", "Diwali", "Ramadan Iftar")
    
    val filteredTemplates = if (selectedCategory == "All") {
        Templates.list
    } else {
        Templates.list.filter { it.category == selectedCategory }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (viewModel.isDarkMode) MidnightSlate else CreamWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "ডিজাইন টেমপ্লেট লাইব্রেরি" else "Invitation Templates",
                        color = RoyalGold,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RoyalGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (viewModel.isDarkMode) MidnightSlate else CreamWhite
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category scroll bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalGold,
                            selectedLabelColor = MidnightSlate,
                            containerColor = DeepNavy.copy(alpha = 0.5f),
                            labelColor = CreamWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Templates list vertical grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTemplates) { template ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.startNewFromTemplate(template)
                                onNavigateToEditor()
                            },
                        colors = CardDefaults.cardColors(containerColor = if (viewModel.isDarkMode) DeepNavy else Color.White),
                        border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Template Visual Representation box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .background(
                                        when (template.presetBg) {
                                            "Luxury" -> Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFF3B0918),
                                                    Color(0xFF26040F)
                                                )
                                            )
                                            "Golden" -> Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFF2A1E08),
                                                    Color(0xFF121417)
                                                )
                                            )
                                            "Watercolor" -> Brush.radialGradient(
                                                listOf(
                                                    Color(0xFFF5EFFF),
                                                    Color(0xFFE6F4F8)
                                                )
                                            )
                                            "Floral" -> Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFFFFF5F5),
                                                    Color(0xFFFFE4E6)
                                                )
                                            )
                                            else -> Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFF1E293B),
                                                    Color(0xFF0F172A)
                                                )
                                            )
                                        }
                                    )
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Frame border representation
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(1.dp, RoyalGold, RoundedCornerShape(4.dp))
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = if (isBn) template.titleBn else template.titleEn,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = RoyalGold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Icon(
                                            imageVector = when (template.category) {
                                                "Wedding" -> Icons.Default.Favorite
                                                "Birthday" -> Icons.Default.Cake
                                                "Anniversary" -> Icons.Default.Star
                                                "Baby Shower" -> Icons.Default.ChildCare
                                                "Eid" -> Icons.Default.Brightness3
                                                "Diwali" -> Icons.Default.Light
                                                "Christmas" -> Icons.Default.Celebration
                                                else -> Icons.Default.EventNote
                                            },
                                            contentDescription = null,
                                            tint = RoyalGold.copy(alpha = 0.5f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // Template details info
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Text(
                                    text = if (isBn) template.nameBn else template.nameEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (viewModel.isDarkMode) CreamWhite else MidnightSlate,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = template.category,
                                    fontSize = 11.sp,
                                    color = CoolGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 5. INVITATION EDITOR SCREEN ---

@Composable
fun EditorScreen(
    viewModel: InvitationViewModel,
    onNavigateToEventManager: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isBn = viewModel.language == "bn"
    val scope = rememberCoroutineScope()

    val currentInvitation = viewModel.currentInvitation

    if (currentInvitation == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active invitation loaded.")
        }
        return
    }

    // Tab categories
    var activeTab by remember { mutableStateOf("Fields") } // Fields, Design, Layers, Add
    
    // Popup states
    var showRenameDialog by remember { mutableStateOf(false) }
    var tempTitle by remember { mutableStateOf(currentInvitation.title) }

    var showShareQualitySheet by remember { mutableStateOf(false) }
    var showAddTextDialog by remember { mutableStateOf(false) }
    var tempCustomText by remember { mutableStateOf("") }

    var showQrDialog by remember { mutableStateOf(false) }
    var tempQrLink by remember { mutableStateOf(viewModel.qrCodeData ?: "") }

    var showAiAssistantDialog by remember { mutableStateOf(false) }
    var aiHost by remember { mutableStateOf(currentInvitation.hostName) }
    var aiDetails by remember { mutableStateOf(currentInvitation.notes) }
    var aiSelectedCategory by remember { mutableStateOf(currentInvitation.category) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MidnightSlate,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentInvitation.title,
                        color = RoyalGold,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        modifier = Modifier.clickable {
                            tempTitle = currentInvitation.title
                            showRenameDialog = true
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.saveDraftForce()
                        onBack()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RoyalGold)
                    }
                },
                actions = {
                    // AI Assistant Trigger
                    IconButton(onClick = { showAiAssistantDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant Writer",
                            tint = RoyalGold
                        )
                    }
                    
                    // Event planner shortcut
                    IconButton(onClick = onNavigateToEventManager) {
                        Icon(imageVector = Icons.Default.GroupAdd, contentDescription = "Guest Manager", tint = RoyalGold)
                    }

                    // Save force
                    IconButton(onClick = {
                        viewModel.saveDraftForce()
                        Toast.makeText(context, if (isBn) "ড্রাফট সফলভাবে সেভ করা হয়েছে" else "Draft saved successfully", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save Draft", tint = RoyalGold)
                    }

                    // Share Quality popup trigger
                    IconButton(onClick = { showShareQualitySheet = true }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Export Card", tint = RoyalGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightSlate)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MidnightSlate)
        ) {
            // Interactive Designer Canvas Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.55f)
                    .background(Color(0xFF070A0F))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // The card visual container
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(0.68f) // 2:3 approx proportions
                        .background(
                            brush = if (viewModel.bgPreset != "None") {
                                when (viewModel.bgPreset) {
                                    "Luxury" -> Brush.verticalGradient(listOf(Color(0xFF3B0918), Color(0xFF26040F)))
                                    "Golden" -> Brush.verticalGradient(listOf(Color(0xFF2A1E08), Color(0xFF121417)))
                                    "Watercolor" -> Brush.radialGradient(listOf(Color(0xFFF5EFFF), Color(0xFFE6F4F8)))
                                    "Floral" -> Brush.verticalGradient(listOf(Color(0xFFFFF5F5), Color(0xFFFFE4E6)))
                                    "Nature" -> Brush.verticalGradient(listOf(Color(0xFFF0F4F1), Color(0xFFDDE7E1)))
                                    "Marble" -> Brush.verticalGradient(listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC)))
                                    else -> Brush.verticalGradient(listOf(Color(0xFFFAFAFA), Color(0xFFFAFAFA)))
                                }
                            } else if (viewModel.bgGradientStart != null && viewModel.bgGradientEnd != null) {
                                Brush.verticalGradient(
                                    listOf(
                                        Color(viewModel.bgGradientStart!! or 0xFF000000),
                                        Color(viewModel.bgGradientEnd!! or 0xFF000000)
                                    )
                                )
                            } else {
                                Brush.verticalGradient(listOf(Color(0xFF121824), Color(0xFF121824)))
                            }
                        )
                        .border(2.dp, RoyalGold, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .pointerInput(Unit) {
                            // De-select on click canvas empty area
                            val canvasSize = this.size
                            detectDragGestures(
                                onDragStart = { },
                                onDragEnd = { },
                                onDragCancel = { },
                                onDrag = { change, dragAmount ->
                                    // Move currently selected text layer or sticker
                                    val selTextId = viewModel.selectedTextLayerId
                                    val selStickerId = viewModel.selectedStickerLayerId
                                    if (selTextId != null) {
                                        viewModel.updateSelectedTextLayer { current ->
                                            // Scale x/y offset according to drag bounds
                                            val newX = (current.x + dragAmount.x / canvasSize.width).coerceIn(0f, 1f)
                                            val newY = (current.y + dragAmount.y / canvasSize.height).coerceIn(0f, 1f)
                                            current.copy(x = newX, y = newY)
                                        }
                                    } else if (selStickerId != null) {
                                        viewModel.updateSelectedStickerLayer { current ->
                                            val newX = (current.x + dragAmount.x / canvasSize.width).coerceIn(0f, 1f)
                                            val newY = (current.y + dragAmount.y / canvasSize.height).coerceIn(0f, 1f)
                                            current.copy(x = newX, y = newY)
                                        }
                                    }
                                }
                            )
                        }
                ) {
                    val cardWidth = maxWidth
                    val cardHeight = maxHeight

                    // Border illustration vector lines
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .border(1.dp, RoyalGold.copy(alpha = 0.5f))
                    )

                    // Draw Sticker Layers
                    viewModel.stickerLayers.forEach { st ->
                        val isSelected = st.id == viewModel.selectedStickerLayerId
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(st.id) {
                                    val stickerContainerSize = this.size
                                    detectDragGestures(
                                        onDragStart = { viewModel.selectStickerLayer(st.id) },
                                        onDrag = { change, dragAmount ->
                                            viewModel.updateSelectedStickerLayer { current ->
                                                val newX = (current.x + dragAmount.x / stickerContainerSize.width).coerceIn(0f, 1f)
                                                val newY = (current.y + dragAmount.y / stickerContainerSize.height).coerceIn(0f, 1f)
                                                current.copy(x = newX, y = newY)
                                            }
                                        }
                                    )
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(
                                        x = cardWidth * st.x - 24.dp,
                                        y = cardHeight * st.y - 24.dp
                                    )
                                    .rotate(st.rotation)
                                    .size((48 * st.scale).dp)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.dp,
                                        color = if (isSelected) RoyalGold else Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        viewModel.selectStickerLayer(st.id)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Render matching vectors beautifully
                                RenderStickerDesign(type = st.type, modifier = Modifier.fillMaxSize(), opacity = st.opacity)
                            }
                        }
                    }

                    // Draw Text Layers
                    viewModel.textLayers.forEach { tl ->
                        val isSelected = tl.id == viewModel.selectedTextLayerId
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(tl.id) {
                                    val textContainerSize = this.size
                                    detectDragGestures(
                                        onDragStart = { viewModel.selectTextLayer(tl.id) },
                                        onDrag = { change, dragAmount ->
                                            viewModel.updateSelectedTextLayer { current ->
                                                val newX = (current.x + dragAmount.x / textContainerSize.width).coerceIn(0f, 1f)
                                                val newY = (current.y + dragAmount.y / textContainerSize.height).coerceIn(0f, 1f)
                                                current.copy(x = newX, y = newY)
                                            }
                                        }
                                    )
                                }
                        ) {
                            Text(
                                text = tl.text,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(
                                        x = cardWidth * tl.x - (cardWidth * 0.5f),
                                        y = cardHeight * tl.y
                                    )
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) RoyalGold else Color.Transparent,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                                    .clickable {
                                        viewModel.selectTextLayer(tl.id)
                                    },
                                style = TextStyle(
                                    color = Color(tl.color).copy(alpha = tl.opacity),
                                    fontSize = tl.size.sp,
                                    fontFamily = when (tl.fontFamily) {
                                        "SansSerif" -> FontFamily.SansSerif
                                        "Monospace" -> FontFamily.Monospace
                                        else -> FontFamily.Serif
                                    },
                                    fontWeight = if (tl.isBold) FontWeight.Bold else FontWeight.Normal,
                                    fontStyle = if (tl.isItalic) FontStyle.Italic else FontStyle.Normal,
                                    textDecoration = if (tl.isUnderline) TextDecoration.Underline else TextDecoration.None,
                                    textAlign = when (tl.textAlignment) {
                                        "Left" -> TextAlign.Left
                                        "Right" -> TextAlign.Right
                                        else -> TextAlign.Center
                                    },
                                    shadow = Shadow(color = Color.Black, offset = Offset(1f, 1f), blurRadius = 2f)
                                )
                            )
                        }
                    }

                    // Render QR Code if enabled
                    val qrDataState = viewModel.qrCodeData
                    if (!qrDataState.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 36.dp)
                                .size(64.dp)
                                .background(Color.White)
                                .padding(4.dp)
                                .clickable {
                                    showQrDialog = true
                                }
                        ) {
                            RenderQrMatrixVisual(text = qrDataState)
                        }
                    }
                }
            }

            // Bottom Controller Panel Tab headers
            TabRow(
                selectedTabIndex = when (activeTab) {
                    "Fields" -> 0
                    "Design" -> 1
                    "Layers" -> 2
                    else -> 3
                },
                containerColor = DeepNavy,
                contentColor = RoyalGold
            ) {
                Tab(selected = activeTab == "Fields", onClick = { activeTab = "Fields" }) {
                    Text(if (isBn) "তথ্য" else "Fields", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == "Design", onClick = { activeTab = "Design" }) {
                    Text(if (isBn) "ব্যাকগ্রাউন্ড" else "Design", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == "Layers", onClick = { activeTab = "Layers" }) {
                    Text(if (isBn) "লেয়ার" else "Layers", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == "Add", onClick = { activeTab = "Add" }) {
                    Text(if (isBn) "যুক্ত করুন" else "Add Item", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }

            // Bottom Tab Contents
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.45f)
                    .background(DeepNavy)
                    .padding(16.dp)
            ) {
                when (activeTab) {
                    "Fields" -> {
                        // Editable data values column
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                OutlinedTextField(
                                    value = currentInvitation.eventName,
                                    onValueChange = { newVal ->
                                        viewModel.loadInvitationForEditing(
                                            currentInvitation.copy(eventName = newVal)
                                        )
                                        // update the title text layer dynamically if matching
                                        val idx = viewModel.textLayers.indexOfFirst { it.id == "title" }
                                        if (idx != -1) {
                                            viewModel.textLayers[idx] = viewModel.textLayers[idx].copy(text = newVal)
                                        }
                                        viewModel.saveCurrentEditorStateToDb()
                                    },
                                    label = { Text("Event Name / শিরোনাম", color = RoyalGold) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = RoyalGold,
                                        unfocusedBorderColor = CoolGray,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = currentInvitation.date,
                                    onValueChange = { newVal ->
                                        viewModel.loadInvitationForEditing(
                                            currentInvitation.copy(date = newVal)
                                        )
                                        // Update dateTime text layer dynamically
                                        val idx = viewModel.textLayers.indexOfFirst { it.id == "dateTime" }
                                        if (idx != -1) {
                                            val parts = viewModel.textLayers[idx].text.split("|")
                                            val timePart = parts.getOrNull(1)?.trim() ?: currentInvitation.time
                                            viewModel.textLayers[idx] = viewModel.textLayers[idx].copy(text = "$newVal | $timePart")
                                        }
                                        viewModel.saveCurrentEditorStateToDb()
                                    },
                                    label = { Text("Event Date / তারিখ", color = RoyalGold) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = RoyalGold,
                                        unfocusedBorderColor = CoolGray,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = currentInvitation.time,
                                    onValueChange = { newVal ->
                                        viewModel.loadInvitationForEditing(
                                            currentInvitation.copy(time = newVal)
                                        )
                                        // Update dateTime text layer dynamically
                                        val idx = viewModel.textLayers.indexOfFirst { it.id == "dateTime" }
                                        if (idx != -1) {
                                            val parts = viewModel.textLayers[idx].text.split("|")
                                            val datePart = parts.getOrNull(0)?.trim() ?: currentInvitation.date
                                            viewModel.textLayers[idx] = viewModel.textLayers[idx].copy(text = "$datePart | $newVal")
                                        }
                                        viewModel.saveCurrentEditorStateToDb()
                                    },
                                    label = { Text("Event Time / সময়", color = RoyalGold) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = RoyalGold,
                                        unfocusedBorderColor = CoolGray,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = currentInvitation.venue,
                                    onValueChange = { newVal ->
                                        viewModel.loadInvitationForEditing(
                                            currentInvitation.copy(venue = newVal)
                                        )
                                        // Update venue text layer dynamically
                                        val idx = viewModel.textLayers.indexOfFirst { it.id == "venue" }
                                        if (idx != -1) {
                                            viewModel.textLayers[idx] = viewModel.textLayers[idx].copy(text = "At: $newVal\n${currentInvitation.address}")
                                        }
                                        viewModel.saveCurrentEditorStateToDb()
                                    },
                                    label = { Text("Venue / স্থান", color = RoyalGold) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = RoyalGold,
                                        unfocusedBorderColor = CoolGray,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = currentInvitation.address,
                                    onValueChange = { newVal ->
                                        viewModel.loadInvitationForEditing(
                                            currentInvitation.copy(address = newVal)
                                        )
                                        // Update venue text layer dynamically
                                        val idx = viewModel.textLayers.indexOfFirst { it.id == "venue" }
                                        if (idx != -1) {
                                            viewModel.textLayers[idx] = viewModel.textLayers[idx].copy(text = "At: ${currentInvitation.venue}\n$newVal")
                                        }
                                        viewModel.saveCurrentEditorStateToDb()
                                    },
                                    label = { Text("Address / ঠিকানা", color = RoyalGold) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = RoyalGold,
                                        unfocusedBorderColor = CoolGray,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    "Design" -> {
                        // Background preset chooser & Gradients
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Text(
                                    if (isBn) "ব্যাকগ্রাউন্ড প্রিসেট বেছে নিন" else "Background Presets",
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalGold,
                                    fontSize = 14.sp
                                )
                            }
                            
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val presets = listOf("Luxury", "Floral", "Golden", "Watercolor", "Minimal", "Nature", "Marble")
                                    presets.forEach { pr ->
                                        Button(
                                            onClick = { viewModel.setBackgroundPreset(pr) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (viewModel.bgPreset == pr) RoyalGold else CoolGray
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(pr, color = if (viewModel.bgPreset == pr) MidnightSlate else Color.White)
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    if (isBn) "গ্রেডিয়েন্ট ব্যাকগ্রাউন্ড সেট করুন" else "Or Dynamic Gradients",
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalGold,
                                    fontSize = 14.sp
                                )
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    val gradients = listOf(
                                        Pair(0xFF0F172A, 0xFF1E293B), // Midnight Blue
                                        Pair(0xFF3B0918, 0xFF581C2B), // Crimson Dark
                                        Pair(0xFF1B1F24, 0xFF2A2E35), // Dark Gray
                                        Pair(0xFFFCE7F3, 0xFFFBCFE8), // Cute Pink Blush
                                        Pair(0xFFF1F5F9, 0xFFCBD5E1)  // Clean Slate
                                    )
                                    gradients.forEach { pair ->
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(Color(pair.first), Color(pair.second))
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                                                .clickable {
                                                    viewModel.setBackgroundGradient(pair.first, pair.second)
                                                }
                                        )
                                    }
                                }
                            }

                            item {
                                Divider(color = CoolGray.copy(alpha = 0.5f))
                            }

                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            if (isBn) "ইভেন্ট কিউআর কোড (QR Code)" else "Event QR Code Layer",
                                            fontWeight = FontWeight.Bold,
                                            color = RoyalGold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            if (isBn) "অতিথিদের জন্য গুগল ম্যাপস বা ওয়েবসাইট লিংক" else "Let guests scan maps directions or link",
                                            color = CoolGray,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Switch(
                                        checked = viewModel.qrCodeData != null,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                showQrDialog = true
                                            } else {
                                                viewModel.updateQrCode(null)
                                            }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = RoyalGold)
                                    )
                                }
                            }
                        }
                    }

                    "Layers" -> {
                        // Edit parameters of currently selected text or sticker layer
                        val selTextId = viewModel.selectedTextLayerId
                        val selStickerId = viewModel.selectedStickerLayerId

                        if (selTextId == null && selStickerId == null) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (isBn) "সম্পাদনা করতে ক্যানভাসে যেকোনো টেক্সট বা স্টিকার স্পর্শ করুন" else "Tap any text or sticker layer on card canvas to modify",
                                    color = CoolGray,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Layer Actions Toolbar
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (selTextId != null) "Modify Text / টেক্সট পরিবর্তন" else "Modify Sticker / স্টিকার পরিবর্তন",
                                            color = RoyalGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            IconButton(onClick = { viewModel.bringSelectedToFront() }) {
                                                Icon(imageVector = Icons.Default.FlipToFront, contentDescription = "Bring to front", tint = RoyalGold)
                                            }
                                            IconButton(onClick = { viewModel.deleteSelectedLayer() }) {
                                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Layer", tint = RoyalCrimson)
                                            }
                                        }
                                    }
                                }

                                if (selTextId != null) {
                                    // Custom text field editors
                                    val textLayerObj = viewModel.textLayers.find { it.id == selTextId }
                                    if (textLayerObj != null) {
                                        item {
                                            OutlinedTextField(
                                                value = textLayerObj.text,
                                                onValueChange = { newVal ->
                                                    viewModel.updateSelectedTextLayer { it.copy(text = newVal) }
                                                },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = RoyalGold,
                                                    unfocusedBorderColor = CoolGray,
                                                    focusedTextColor = Color.White
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                        item {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                // Size
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Font Size: ${textLayerObj.size.toInt()}sp", color = CreamWhite, fontSize = 12.sp)
                                                    Slider(
                                                        value = textLayerObj.size,
                                                        onValueChange = { newVal ->
                                                            viewModel.updateSelectedTextLayer { it.copy(size = newVal) }
                                                        },
                                                        valueRange = 10f..50f,
                                                        colors = SliderDefaults.colors(thumbColor = RoyalGold, activeTrackColor = RoyalGold)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                // Opacity
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Opacity: ${(textLayerObj.opacity * 100).toInt()}%", color = CreamWhite, fontSize = 12.sp)
                                                    Slider(
                                                        value = textLayerObj.opacity,
                                                        onValueChange = { newVal ->
                                                            viewModel.updateSelectedTextLayer { it.copy(opacity = newVal) }
                                                        },
                                                        colors = SliderDefaults.colors(thumbColor = RoyalGold, activeTrackColor = RoyalGold)
                                                    )
                                                }
                                            }
                                        }

                                        // Styling Buttons (Bold, Italic, Underline)
                                        item {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                IconToggleButton(
                                                    checked = textLayerObj.isBold,
                                                    onCheckedChange = { newVal ->
                                                        viewModel.updateSelectedTextLayer { it.copy(isBold = newVal) }
                                                    }
                                                ) {
                                                    Icon(imageVector = Icons.Default.FormatBold, contentDescription = "Bold", tint = if (textLayerObj.isBold) RoyalGold else Color.White)
                                                }
                                                IconToggleButton(
                                                    checked = textLayerObj.isItalic,
                                                    onCheckedChange = { newVal ->
                                                        viewModel.updateSelectedTextLayer { it.copy(isItalic = newVal) }
                                                    }
                                                ) {
                                                    Icon(imageVector = Icons.Default.FormatItalic, contentDescription = "Italic", tint = if (textLayerObj.isItalic) RoyalGold else Color.White)
                                                }
                                                IconToggleButton(
                                                    checked = textLayerObj.isUnderline,
                                                    onCheckedChange = { newVal ->
                                                        viewModel.updateSelectedTextLayer { it.copy(isUnderline = newVal) }
                                                    }
                                                ) {
                                                    Icon(imageVector = Icons.Default.FormatUnderlined, contentDescription = "Underline", tint = if (textLayerObj.isUnderline) RoyalGold else Color.White)
                                                }
                                                
                                                // Fonts Choice Selector
                                                val fonts = listOf("Serif", "SansSerif", "Monospace")
                                                fonts.forEach { fn ->
                                                    Button(
                                                        onClick = {
                                                            viewModel.updateSelectedTextLayer { it.copy(fontFamily = fn) }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = if (textLayerObj.fontFamily == fn) RoyalGold else CoolGray
                                                        ),
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                        modifier = Modifier.height(36.dp)
                                                    ) {
                                                        Text(fn, fontSize = 11.sp, color = if (textLayerObj.fontFamily == fn) MidnightSlate else Color.White)
                                                    }
                                                }
                                            }
                                        }

                                        // Color Swatches Selection row
                                        item {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(rememberScrollState()),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                val colors = listOf(
                                                    0xFFFFFFFF, 0xFFD4AF37, 0xFFF59E0B, 0xFFEF4444, 
                                                    0xFFF472B6, 0xFF3B82F6, 0xFF10B981, 0xFF000000
                                                )
                                                colors.forEach { c ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .background(Color(c), CircleShape)
                                                            .border(
                                                                width = if (textLayerObj.color == c) 2.dp else 1.dp,
                                                                color = Color.White,
                                                                shape = CircleShape
                                                            )
                                                            .clickable {
                                                                viewModel.updateSelectedTextLayer { it.copy(color = c) }
                                                            }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                if (selStickerId != null) {
                                    val stickerObj = viewModel.stickerLayers.find { it.id == selStickerId }
                                    if (stickerObj != null) {
                                        item {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                // Scale slider
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Sticker Scale: ${String.format("%.1f", stickerObj.scale)}x", color = CreamWhite, fontSize = 12.sp)
                                                    Slider(
                                                        value = stickerObj.scale,
                                                        onValueChange = { newVal ->
                                                            viewModel.updateSelectedStickerLayer { it.copy(scale = newVal) }
                                                        },
                                                        valueRange = 0.5f..2.5f,
                                                        colors = SliderDefaults.colors(thumbColor = RoyalGold, activeTrackColor = RoyalGold)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                // Rotation slider
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Sticker Rotation: ${stickerObj.rotation.toInt()}°", color = CreamWhite, fontSize = 12.sp)
                                                    Slider(
                                                        value = stickerObj.rotation,
                                                        onValueChange = { newVal ->
                                                            viewModel.updateSelectedStickerLayer { it.copy(rotation = newVal) }
                                                        },
                                                        valueRange = -180f..180f,
                                                        colors = SliderDefaults.colors(thumbColor = RoyalGold, activeTrackColor = RoyalGold)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Add" -> {
                        // Toolbar to add custom things (New custom text, Stickers list)
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Button(
                                    onClick = { showAddTextDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.TextFields, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(if (isBn) "নতুন টেক্সট যুক্ত করুন" else "Add Custom Text Layer", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            item {
                                Text(
                                    if (isBn) "আলংকারিক স্টিকার যুক্ত করুন" else "Add Decorative Sticker",
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalGold,
                                    fontSize = 14.sp
                                )
                            }

                            // Stickers selection horizontal row
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    val stickersAvailable = listOf("Heart", "Star", "Balloons", "Cake", "Rings", "Flowers", "Lantern")
                                    stickersAvailable.forEach { st ->
                                        Card(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clickable {
                                                    viewModel.addSticker(st)
                                                },
                                            colors = CardDefaults.cardColors(containerColor = DeepNavy.copy(alpha = 0.5f)),
                                            border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.3f))
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Box(modifier = Modifier.size(36.dp)) {
                                                    RenderStickerDesign(type = st, modifier = Modifier.fillMaxSize(), opacity = 1.0f)
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(st, fontSize = 10.sp, color = CreamWhite)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- POPUPS & DIALOGS ---

    // 1. Rename Draft Dialog
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text(if (isBn) "প্রজেক্টের নাম পরিবর্তন" else "Rename Project") },
            text = {
                OutlinedTextField(
                    value = tempTitle,
                    onValueChange = { tempTitle = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalGold)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateInvitationTitle(tempTitle)
                        showRenameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                ) {
                    Text("OK", color = MidnightSlate)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // 2. Add custom text layer
    if (showAddTextDialog) {
        AlertDialog(
            onDismissRequest = { showAddTextDialog = false },
            title = { Text(if (isBn) "টেক্সট লিখুন" else "Add Custom Text") },
            text = {
                OutlinedTextField(
                    value = tempCustomText,
                    onValueChange = { tempCustomText = it },
                    label = { Text("Write text here...") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalGold)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempCustomText.isNotBlank()) {
                            viewModel.addCustomText(tempCustomText)
                            tempCustomText = ""
                            showAddTextDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                ) {
                    Text("Add", color = MidnightSlate)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTextDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // 3. Configure QR code dialog
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = { Text(if (isBn) "QR কোড যুক্ত করুন" else "Add Event QR Code") },
            text = {
                Column {
                    Text(
                        if (isBn) "গুগল ম্যাপস লোকেশন অথবা RSVP লিঙ্ক লিখুন" else "Enter Google Maps location link or RSVP website url",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempQrLink,
                        onValueChange = { tempQrLink = it },
                        placeholder = { Text("https://...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalGold)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateQrCode(tempQrLink)
                        showQrDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                ) {
                    Text("Save", color = MidnightSlate)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showQrDialog = false
                }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // 4. AI Assistant Generator Dialog
    if (showAiAssistantDialog) {
        AlertDialog(
            onDismissRequest = { showAiAssistantDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = RoyalGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isBn) "স্মার্ট নিমন্ত্রণ লিপিকার" else "Smart Template Composer")
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        if (isBn) "অফলাইনে কোনো ইন্টারনেট ছাড়াই নিমেষেই চমৎকার আমন্ত্রণ বাণী তৈরি করুন!" else "Generate elegant invitation text instantly on-device without any internet connection.",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    OutlinedTextField(
                        value = aiHost,
                        onValueChange = { aiHost = it },
                        label = { Text("Host Name / আমন্ত্রণকারীর নাম") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalGold)
                    )

                    OutlinedTextField(
                        value = aiDetails,
                        onValueChange = { aiDetails = it },
                        label = { Text("Additional Details (e.g. Traditional, Warm, Fun)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalGold)
                    )

                    if (viewModel.aiLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = RoyalGold)
                        }
                    }

                    val result = viewModel.aiResult
                    if (result != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MidnightSlate),
                            border = BorderStroke(1.dp, RoyalGold)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Suggested Title: ${result["title"]}", fontWeight = FontWeight.Bold, color = RoyalGold)
                                Text("Suggested Body: ${result["body"]}", fontSize = 12.sp, color = CreamWhite)
                            }
                        }
                    }

                    viewModel.aiError?.let { err ->
                        Text("Error composing: $err", color = Color.Red, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (viewModel.aiResult != null) {
                        Button(
                            onClick = {
                                viewModel.applyGeminiResultToEditor()
                                showAiAssistantDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                        ) {
                            Text("Apply to Editor", color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.generateWithGemini(aiSelectedCategory, aiHost, aiDetails)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                            enabled = !viewModel.aiLoading
                        ) {
                            Text("Compose", color = Color.White)
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiAssistantDialog = false }) {
                    Text("Close", color = Color.Gray)
                }
            }
        )
    }

    // 5. Share Quality Sheet Dialog
    if (showShareQualitySheet) {
        AlertDialog(
            onDismissRequest = { showShareQualitySheet = false },
            title = { Text(if (isBn) "কার্ড এক্সপোর্ট করুন" else "Export Invitation Card") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (isBn) "এক্সপোর্ট কোয়ালিটি বেছে নিন:" else "Select Export Quality:", fontWeight = FontWeight.Bold)
                    
                    Button(
                        onClick = {
                            val bitmap = viewModel.renderBitmap(context, BitmapExporter.ExportQuality.LOW)
                            val uri = viewModel.getShareUri(context, bitmap)
                            triggerSystemShare(context, uri)
                            showShareQualitySheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CoolGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Low Quality (Fast Share) - PNG", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val bitmap = viewModel.renderBitmap(context, BitmapExporter.ExportQuality.MEDIUM)
                            val uri = viewModel.getShareUri(context, bitmap)
                            triggerSystemShare(context, uri)
                            showShareQualitySheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Medium Quality (Recommended) - PNG", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val bitmap = viewModel.renderBitmap(context, BitmapExporter.ExportQuality.HIGH)
                            val uri = viewModel.getShareUri(context, bitmap)
                            triggerSystemShare(context, uri)
                            showShareQualitySheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("High Definition (HD Print) - PNG", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val uri = viewModel.getPdfShareUri(context)
                            triggerSystemShare(context, uri, "application/pdf")
                            showShareQualitySheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalCrimson),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Export as Print PDF Document", color = Color.White)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showShareQualitySheet = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

private fun triggerSystemShare(context: Context, uri: Uri?, mime: String = "image/png") {
    if (uri == null) {
        Toast.makeText(context, "Failed to export card image.", Toast.LENGTH_SHORT).show()
        return
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mime
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Invitation via"))
}

// Helper vectors render
@Composable
fun RenderStickerDesign(type: String, modifier: Modifier = Modifier, opacity: Float = 1.0f) {
    Icon(
        imageVector = when (type) {
            "Heart" -> Icons.Default.Favorite
            "Star" -> Icons.Default.Star
            "Balloons" -> Icons.Default.Celebration
            "Cake" -> Icons.Default.Cake
            "Rings" -> Icons.Default.AllInclusive
            "Flowers" -> Icons.Default.LocalFlorist
            "Lantern" -> Icons.Default.Brightness3
            else -> Icons.Default.Star
        },
        contentDescription = type,
        tint = RoyalGold.copy(alpha = opacity),
        modifier = modifier
    )
}

@Composable
fun RenderQrMatrixVisual(text: String) {
    val matrix = QrCodeGenerator.generateQrMatrix(text)
    val size = matrix.size

    Column(modifier = Modifier.fillMaxSize()) {
        for (r in 0 until size) {
            Row(modifier = Modifier.weight(1f)) {
                for (c in 0 until size) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(if (matrix[r][c]) Color.Black else Color.White)
                    )
                }
            }
        }
    }
}

// --- 6. EVENT MANAGEMENT SCREEN ---

@Composable
fun EventManagerScreen(
    viewModel: InvitationViewModel,
    onBack: () -> Unit
) {
    val isBn = viewModel.language == "bn"
    
    var managerTab by remember { mutableStateOf("Guests") } // Guests, Budget, Checklist
    
    // Streams
    val guests by viewModel.guests.collectAsState()
    val budgetItems by viewModel.budgetItems.collectAsState()
    val checklist by viewModel.checklistItems.collectAsState()

    // Popups
    var showAddGuestDialog by remember { mutableStateOf(false) }
    var gName by remember { mutableStateOf("") }
    var gPhone by remember { mutableStateOf("") }
    var gRsvp by remember { mutableStateOf("Pending") }

    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var bName by remember { mutableStateOf("") }
    var bCostEst by remember { mutableStateOf("") }
    var bCostAct by remember { mutableStateOf("") }

    var showAddChecklistDialog by remember { mutableStateOf(false) }
    var tName by remember { mutableStateOf("") }
    var tPriority by remember { mutableStateOf("Medium") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (viewModel.isDarkMode) MidnightSlate else CreamWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isBn) "ইভেন্ট ও অতিথি ড্যাশবোর্ড" else "Event & RSVP Planner",
                        color = RoyalGold,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RoyalGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (viewModel.isDarkMode) MidnightSlate else CreamWhite
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DeepNavy,
                contentColor = RoyalGold
            ) {
                NavigationBarItem(
                    selected = managerTab == "Guests",
                    onClick = { managerTab = "Guests" },
                    icon = { Icon(imageVector = Icons.Default.People, contentDescription = "Guests") },
                    label = { Text(if (isBn) "অতিথি" else "Guests") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = MidnightSlate, selectedTextColor = RoyalGold, indicatorColor = RoyalGold)
                )
                NavigationBarItem(
                    selected = managerTab == "Budget",
                    onClick = { managerTab = "Budget" },
                    icon = { Icon(imageVector = Icons.Default.AttachMoney, contentDescription = "Budget") },
                    label = { Text(if (isBn) "বাজেট" else "Budget") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = MidnightSlate, selectedTextColor = RoyalGold, indicatorColor = RoyalGold)
                )
                NavigationBarItem(
                    selected = managerTab == "Checklist",
                    onClick = { managerTab = "Checklist" },
                    icon = { Icon(imageVector = Icons.Default.PlaylistAddCheck, contentDescription = "Tasks") },
                    label = { Text(if (isBn) "চেকলিস্ট" else "Checklist") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = MidnightSlate, selectedTextColor = RoyalGold, indicatorColor = RoyalGold)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats card showing summary metrics
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        if (isBn) "ইভেন্ট সারসংক্ষেপ" else "Planner Progress Summary",
                        fontWeight = FontWeight.Bold,
                        color = RoyalGold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // RSVP confirmed rate
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val confirmedCount = guests.count { it.rsvpStatus == "Confirmed" }
                            Text("$confirmedCount / ${guests.size}", color = CreamWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(if (isBn) "নিশ্চিত অতিথি" else "Confirmed RSVPs", color = CoolGray, fontSize = 11.sp)
                        }

                        // Total Budget spent
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val totalEst = budgetItems.sumOf { it.estimatedCost }
                            val totalAct = budgetItems.sumOf { it.actualCost }
                            Text("৳${totalAct.toInt()} / ৳${totalEst.toInt()}", color = CreamWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(if (isBn) "প্রকৃত বাজেট / আনুমানিক" else "Spent / Estimated", color = CoolGray, fontSize = 11.sp)
                        }

                        // Completed checklist rate
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val completedCount = checklist.count { it.isCompleted }
                            Text("$completedCount / ${checklist.size}", color = CreamWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(if (isBn) "কাজ সম্পন্ন" else "Tasks Done", color = CoolGray, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Tab-specific details lists
            when (managerTab) {
                "Guests" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isBn) "অতিথি তালিকা ও RSVP" else "Guest Registry",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (viewModel.isDarkMode) CreamWhite else MidnightSlate
                        )
                        IconButton(onClick = { showAddGuestDialog = true }) {
                            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Guest", tint = RoyalGold)
                        }
                    }

                    if (guests.isEmpty()) {
                        EmptyPlaceholderCard(msg = if (isBn) "অতিথি তালিকা খালি আছে" else "No guests added yet")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(guests) { g ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (viewModel.isDarkMode) DeepNavy else Color.White),
                                    border = BorderStroke(0.5.dp, if (viewModel.isDarkMode) CoolGray else Color.LightGray)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(g.name, fontWeight = FontWeight.Bold, color = if (viewModel.isDarkMode) CreamWhite else MidnightSlate)
                                            Text(g.phone, fontSize = 12.sp, color = CoolGray)
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // RSVP selector chips
                                            val rsvpOptions = listOf("Confirmed", "Declined", "Pending")
                                            rsvpOptions.forEach { status ->
                                                val isSel = g.rsvpStatus == status
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            color = if (isSel) {
                                                                when (status) {
                                                                    "Confirmed" -> Color(0xFF2B8A3E)
                                                                    "Declined" -> RoyalCrimson
                                                                    else -> CoolGray
                                                                }
                                                            } else Color.Transparent,
                                                            shape = RoundedCornerShape(4.dp)
                                                        )
                                                        .border(1.dp, CoolGray, RoundedCornerShape(4.dp))
                                                        .clickable {
                                                            viewModel.updateGuestRsvp(g, status)
                                                        }
                                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                                ) {
                                                    Text(
                                                        status.take(4),
                                                        fontSize = 10.sp,
                                                        color = if (isSel) Color.White else CoolGray,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            IconButton(onClick = { viewModel.deleteGuest(g) }) {
                                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoyalCrimson, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Budget" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isBn) "বাজেট হিসাবনিকাশ" else "Budget Ledger",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (viewModel.isDarkMode) CreamWhite else MidnightSlate
                        )
                        IconButton(onClick = { showAddBudgetDialog = true }) {
                            Icon(imageVector = Icons.Default.AddCard, contentDescription = "Add budget", tint = RoyalGold)
                        }
                    }

                    if (budgetItems.isEmpty()) {
                        EmptyPlaceholderCard(msg = if (isBn) "বাজেট হিসাব খাতা খালি আছে" else "No budget ledger items yet")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(budgetItems) { b ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (viewModel.isDarkMode) DeepNavy else Color.White),
                                    border = BorderStroke(0.5.dp, if (viewModel.isDarkMode) CoolGray else Color.LightGray)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(b.itemName, fontWeight = FontWeight.Bold, color = if (viewModel.isDarkMode) CreamWhite else MidnightSlate)
                                            Text("Est: ৳${b.estimatedCost.toInt()} • Act: ৳${b.actualCost.toInt()}", fontSize = 12.sp, color = CoolGray)
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(
                                                checked = b.isPaid,
                                                onCheckedChange = { viewModel.toggleBudgetItemPaid(b) },
                                                colors = CheckboxDefaults.colors(checkedColor = RoyalGold)
                                            )
                                            Text(
                                                if (b.isPaid) "Paid" else "Unpaid", 
                                                fontSize = 12.sp, 
                                                color = if (b.isPaid) Color.Green else Color.Red,
                                                fontWeight = FontWeight.Bold
                                            )
                                            IconButton(onClick = { viewModel.deleteBudgetItem(b) }) {
                                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoyalCrimson)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Checklist" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isBn) "ইভেন্ট কাজের চেকলিস্ট" else "Event Checklist Tasks",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (viewModel.isDarkMode) CreamWhite else MidnightSlate
                        )
                        IconButton(onClick = { showAddChecklistDialog = true }) {
                            Icon(imageVector = Icons.Default.PlaylistAdd, contentDescription = "Add Task", tint = RoyalGold)
                        }
                    }

                    if (checklist.isEmpty()) {
                        EmptyPlaceholderCard(msg = if (isBn) "কাজের চেকলিস্ট খালি আছে" else "No tasks scheduled yet")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(checklist) { t ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (viewModel.isDarkMode) DeepNavy else Color.White),
                                    border = BorderStroke(0.5.dp, if (viewModel.isDarkMode) CoolGray else Color.LightGray)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(
                                                checked = t.isCompleted,
                                                onCheckedChange = { viewModel.toggleChecklistItem(t) },
                                                colors = CheckboxDefaults.colors(checkedColor = RoyalGold)
                                            )
                                            Column {
                                                Text(
                                                    text = t.taskName,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (viewModel.isDarkMode) CreamWhite else MidnightSlate,
                                                    textDecoration = if (t.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                                )
                                                Text("Priority: ${t.priority}", fontSize = 11.sp, color = CoolGray)
                                            }
                                        }

                                        IconButton(onClick = { viewModel.deleteChecklistItem(t) }) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoyalCrimson)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS FOR ADDING PLANNERS ---

    // 1. Add Guest
    if (showAddGuestDialog) {
        AlertDialog(
            onDismissRequest = { showAddGuestDialog = false },
            title = { Text(if (isBn) "নতুন অতিথি যুক্ত করুন" else "Invite Guest") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = gName, onValueChange = { gName = it }, label = { Text("Guest Name") })
                    OutlinedTextField(value = gPhone, onValueChange = { gPhone = it }, label = { Text("Phone Number") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (gName.isNotBlank()) {
                            viewModel.addGuest(gName, gPhone, "", gRsvp)
                            gName = ""
                            gPhone = ""
                            showAddGuestDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                ) {
                    Text("Add", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGuestDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 2. Add Budget Ledger Item
    if (showAddBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showAddBudgetDialog = false },
            title = { Text(if (isBn) "বাজেট খরচ যুক্ত করুন" else "Add Ledger Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = bName, onValueChange = { bName = it }, label = { Text("Item Name (e.g. Catering)") })
                    OutlinedTextField(value = bCostEst, onValueChange = { bCostEst = it }, label = { Text("Estimated Cost (৳)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = bCostAct, onValueChange = { bCostAct = it }, label = { Text("Actual Cost (৳)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (bName.isNotBlank()) {
                            val est = bCostEst.toDoubleOrNull() ?: 0.0
                            val act = bCostAct.toDoubleOrNull() ?: 0.0
                            viewModel.addBudgetItem(bName, "Event", est, act)
                            bName = ""
                            bCostEst = ""
                            bCostAct = ""
                            showAddBudgetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                ) {
                    Text("Add", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBudgetDialog = false }) { Text("Cancel") }
            }
        )
    }

    // 3. Add Checklist task
    if (showAddChecklistDialog) {
        AlertDialog(
            onDismissRequest = { showAddChecklistDialog = false },
            title = { Text(if (isBn) "নতুন কাজ যুক্ত করুন" else "Add Checklist Task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = tName, onValueChange = { tName = it }, label = { Text("Task (e.g. Order flowers)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tName.isNotBlank()) {
                            viewModel.addChecklistItem(tName, tPriority, "")
                            tName = ""
                            showAddChecklistDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                ) {
                    Text("Add", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddChecklistDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun EmptyPlaceholderCard(msg: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DeepNavy.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Default.Inbox, contentDescription = null, tint = CoolGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(msg, color = CoolGray, fontSize = 13.sp)
        }
    }
}

// --- 7. ABOUT & SETTINGS SCREEN ---

@Composable
fun AboutScreen(
    viewModel: InvitationViewModel,
    onBack: () -> Unit
) {
    val isBn = viewModel.language == "bn"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (viewModel.isDarkMode) MidnightSlate else CreamWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isBn) "অ্যাপ সম্পর্কে জানুন" else "About App Settings",
                        color = RoyalGold,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RoyalGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (viewModel.isDarkMode) MidnightSlate else CreamWhite
                )
            )
        }
    ) { innerPadding ->
        val context = LocalContext.current
        val openUrl = { url: String ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                // Handle fallback gracefully
            }
        }
        val openWhatsApp = { phone: String ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phone"))
                context.startActivity(intent)
            } catch (e: Exception) {
                // Handle fallback gracefully
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Icon Graphic Simulation
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(
                        Brush.linearGradient(listOf(RoyalGold, RoyalCrimson)),
                        CircleShape
                    )
                    .border(3.dp, RoyalGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AllInclusive,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(58.dp)
                )
            }

            Text(
                "Infinity Invitation",
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = RoyalGold
                )
            )

            Text(
                "Version 1.0.0 (Smart Offline Engine)",
                fontSize = 13.sp,
                color = CoolGray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 1. ABOUT DEVELOPER CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(RoyalGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "AR",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Column {
                            Text(
                                if (isBn) "ডেভেলপার পরিচিতি" else "About Developer",
                                fontWeight = FontWeight.Bold,
                                color = RoyalGold,
                                fontSize = 15.sp
                            )
                            Text(
                                "Prince AR Abdur Rahman",
                                fontWeight = FontWeight.SemiBold,
                                color = CreamWhite,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Text(
                        text = "Independent App Developer passionate about building modern Android applications, productivity tools, AI-powered experiences, media players, educational apps, and next-generation digital products.",
                        fontSize = 12.sp,
                        color = CreamWhite.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )

                    Divider(color = CoolGray.copy(alpha = 0.2f), thickness = 1.dp)

                    Text(
                        if (isBn) "যোগাযোগ করুন" else "Connect with Developer",
                        fontWeight = FontWeight.Bold,
                        color = RoyalGold,
                        fontSize = 13.sp
                    )

                    // WhatsApp Links
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { openWhatsApp("8801707424006") },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WA: 01707424006", fontSize = 10.sp, color = RoyalGold, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { openWhatsApp("8801796951709") },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WA: 01796951709", fontSize = 10.sp, color = RoyalGold, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Facebook & Instagram buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { openUrl("https://www.facebook.com/share/1BNn32qoJo/") },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Facebook", fontSize = 10.sp, color = RoyalGold, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { openUrl("https://www.instagram.com/ur___abdur____rahman__2008") },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Instagram", fontSize = 10.sp, color = RoyalGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. ABOUT COMPANY CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        if (isBn) "প্রতিষ্ঠান পরিচিতি" else "About Company",
                        fontWeight = FontWeight.Bold,
                        color = RoyalGold,
                        fontSize = 15.sp
                    )
                    Text(
                        "NexVora Lab's Ofc",
                        fontWeight = FontWeight.SemiBold,
                        color = CreamWhite,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "NexVora Lab's Ofc focuses on creating innovative Android applications designed to improve productivity, entertainment, learning, and digital experiences.",
                        fontSize = 12.sp,
                        color = CreamWhite.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )

                    Divider(color = CoolGray.copy(alpha = 0.2f), thickness = 1.dp)

                    Text(
                        if (isBn) "আমাদের লক্ষ্য" else "Our Mission",
                        fontWeight = FontWeight.Bold,
                        color = RoyalGold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Build fast, beautiful, privacy-friendly, and user-focused applications accessible to everyone.",
                        fontSize = 12.sp,
                        color = CreamWhite.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }

            // 3. TECHNICAL INFO & CREDITS CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (isBn) "কারিগরি তথ্য ও ক্রেডিট" else "Technical Info & Credits",
                        fontWeight = FontWeight.Bold,
                        color = RoyalGold,
                        fontSize = 14.sp
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Application Version:", fontSize = 11.sp, color = CoolGray)
                        Text("1.0.0", fontSize = 11.sp, color = CreamWhite, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Developed By:", fontSize = 11.sp, color = CoolGray)
                        Text("Prince AR Abdur Rahman", fontSize = 11.sp, color = CreamWhite, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Published By:", fontSize = 11.sp, color = CoolGray)
                        Text("NexVora Lab's Ofc", fontSize = 11.sp, color = CreamWhite, fontWeight = FontWeight.Bold)
                    }

                    Divider(color = CoolGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        "© 2026 NexVora Lab's Ofc. All Rights Reserved.",
                        fontSize = 10.sp,
                        color = CoolGray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            // 4. LANGUAGE TOGGLE CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepNavy.copy(alpha = 0.5f)),
                border = BorderStroke(0.5.dp, CoolGray.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (isBn) "ভাষা পরিবর্তন করুন" else "Change Language",
                        fontWeight = FontWeight.Bold,
                        color = CreamWhite,
                        fontSize = 13.sp
                    )
                    Button(
                        onClick = { viewModel.toggleLanguage() },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                    ) {
                        Text(if (isBn) "English" else "বাংলা", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
