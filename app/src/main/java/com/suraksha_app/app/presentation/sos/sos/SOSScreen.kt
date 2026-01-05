package com.suraksha_app.app.presentation.sos.sos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.gandiva.neumorphic.LightSource
import com.gandiva.neumorphic.NeuAttrs
import com.gandiva.neumorphic.neu
import com.gandiva.neumorphic.shape.Pressed
import com.gandiva.neumorphic.shape.RoundedCorner
import com.suraksha_app.app.R
import com.suraksha_app.app.domain.model.Contact
import com.suraksha_app.app.presentation.theme.White
import com.suraksha_app.app.presentation.theme.backgroundColor
import com.suraksha_app.app.presentation.theme.colorGrayBold
import com.suraksha_app.app.presentation.theme.colorGrayLight
import com.suraksha_app.app.presentation.theme.gray37Color
import com.suraksha_app.app.presentation.theme.grayColor
import com.suraksha_app.app.presentation.theme.lightGreen40Color
import com.suraksha_app.app.presentation.theme.poppinsBold
import com.suraksha_app.app.presentation.theme.poppinsRegular
import com.suraksha_app.app.presentation.theme.switchOffBorderColor
import com.suraksha_app.app.presentation.theme.switchOnBorderColor
import com.suraksha_app.app.presentation.theme.switchUnCheckThumbColor
import com.suraksha_app.app.presentation.theme.switchUnCheckTrackColor
import com.suraksha_app.app.presentation.theme.violetBlueColor
import com.suraksha_app.app.presentation.theme.whatsappColor
import com.suraksha_app.app.presentation.theme.whatsappColor37
import com.suraksha_app.app.presentation.theme.whatsappColor5
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(
    modifier: Modifier = Modifier,
    viewModel: SosViewModel = hiltViewModel(),
    navigateToContactSelectScreen: (List<Contact>) -> Unit,
    contactList: List<Contact> = emptyList(),
) {


    val context = LocalContext.current
    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

    }

    val soundPlayer = remember {
        SosSoundPlayer(context)
    }

    var sosTriggered by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            soundPlayer.stop()
        }
    }



    LaunchedEffect(contactList) {
        viewModel.loadContacts(contactList)
    }

    // Check permission and request if needed
    LaunchedEffect(Unit) {
        val permissionsList = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(Manifest.permission.SEND_SMS)
        }

        if (permissionsList.isNotEmpty()) {
            multiplePermissionLauncher.launch(permissionsList.toTypedArray())
        }
    }


    val scrollState = rememberScrollState()
    val smsState by viewModel.smsState.collectAsState()
    if (smsState is SmsState.Success) {
        Toast.makeText(context, "Location Shared via SMS Success", Toast.LENGTH_SHORT).show()
    } else if (smsState is SmsState.Error) {
        Toast.makeText(
            context,
            "Location Shared via SMS Failure ${(smsState as SmsState.Error).message}",
            Toast.LENGTH_SHORT
        ).show()
    }

    Surface(modifier = Modifier.background(backgroundColor)) {
        var alarmSoundCheck by remember { mutableStateOf(true) }

        Column(
            modifier = modifier
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Emergency SOS",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = colorGrayBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Press and hold the button to activate",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = colorGrayLight,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 6.dp),
            )
            SoSButton1(afterSosPressedDelay = {
//                val currentTime = System.currentTimeMillis()
//                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
//                val formattedTime = dateFormat.format(Date(currentTime))
//                viewModel.shareLiveLocation(
//                    formattedTime
//                )

                    if (!sosTriggered) {
                        sosTriggered = true

                        val time = SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss",
                            Locale.getDefault()
                        ).format(Date())

                        // 1️⃣ send SMS once
                        viewModel.shareLiveLocation(time)

                        // 2️⃣ play sound once
                        if (alarmSoundCheck) {
                            soundPlayer.playOnce {
                                sosTriggered = false
                            }
                        } else {
                            sosTriggered = false
                        }
                    }

            }, onSosPressed = {
//                if (alarmSoundCheck) {
//
//                //    soundPlayer.start()
////
////                    val activity = context as MainActivity
////                    println("Launching Foreground Service")
////                    activity.startMyService()
//                }
            })
            if (smsState == SmsState.Loading) {
                Card(
                    modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors().copy(
                        containerColor = whatsappColor5
                    ), shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp, horizontal = 30.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(whatsappColor)
                        )
                        Text(
                            text = "Live Location Sharing Active....",
                            fontSize = 14.sp,
                            color = whatsappColor,
                            fontFamily = poppinsRegular,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 21.dp)
                        )
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), colors = CardColors(
                    containerColor = Color.White,
                    contentColor = Color.Unspecified,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Gray
                ), shape = RoundedCornerShape(20.dp)
            ) {

                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .background(gray37Color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_speaker),
                            contentDescription = "Alarm Sound Icon",
                            tint = grayColor,
                        )
                    }
                    Text(
                        text = "Alarm Sound",
                        fontSize = 14.sp,
                        color = colorGrayLight,
                        fontFamily = poppinsRegular,
                        modifier = Modifier.padding(horizontal = 21.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    val interactionSource = remember { MutableInteractionSource() }

                    CompositionLocalProvider(
                        LocalRippleConfiguration provides RippleConfiguration(
                            color = Color.Transparent, rippleAlpha = RippleAlpha(0f, 0f, 0f, 0f)
                        )
                    ) {
                        Switch(
                            modifier = Modifier.scale(0.75f),
                            interactionSource = interactionSource,
                            enabled = true,
                            checked = alarmSoundCheck,
                            onCheckedChange = { alarmSoundCheck = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = lightGreen40Color,
                                checkedBorderColor = switchOnBorderColor,
                                uncheckedBorderColor = switchOffBorderColor,
                                uncheckedTrackColor = switchUnCheckTrackColor,
                                uncheckedThumbColor = switchUnCheckThumbColor
                            ),
                            thumbContent = {
                                Box(
                                    Modifier
                                        .scale(0.8f)
                                        .size(20.dp)
                                        .background(
                                            color = if (alarmSoundCheck) Color.White else switchUnCheckThumbColor,
                                            shape = CircleShape
                                        )
                                )
                            })
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardColors(
                    containerColor = violetBlueColor,
                    contentColor = Color.Unspecified,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(6.dp),
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 18.dp, horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Send alert after countdown",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = White,
                            fontFamily = poppinsBold,
                        )
                        Text(
                            text = "This app sends emergency SMS messages to user-selected contacts when the user explicitly presses the SOS button. SMS is sent only after a visible countdown and user interaction. No background or silent SMS is sent.",
                            fontSize = 12.sp,
                            color = White,
                            fontFamily = poppinsRegular,
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Emergency Contact",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontFamily = poppinsBold,
                )
                Spacer(modifier = Modifier.weight(1f))
//                IconButton(modifier = Modifier, onClick = {
//                    navigateToContactSelectScreen(contactList)
//                }) {
//                    Icon(
//                        painter = painterResource(R.drawable.ic_pencil),
//                        contentDescription = "Alarm Sound Icon",
//                        tint = grayColor,
//                    )
//                }
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                contactList.forEach { contact ->
                    ContactItem(contact)
                }
            }
        }
    }
}

@Composable
fun ContactItem(contact: Contact) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(violetBlueColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val initials = contact.name.split(" ").filter { it.isNotEmpty() }.take(2)
                    .joinToString("") { it.first().uppercase() }
                Text(
                    text = initials,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = poppinsBold
                )
            }
            Column(
                verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colorGrayBold,
                    fontFamily = poppinsBold,
                )
                Text(
                    text = contact.phoneNumber,
                    fontSize = 14.sp,
                    color = colorGrayLight,
                    fontFamily = poppinsRegular,
                )
            }
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(whatsappColor37, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_call),
                    contentDescription = "Call Icon",
                    tint = whatsappColor
                )
            }
        }
    }
}

@Composable
fun SoSButton1(afterSosPressedDelay: () -> Unit, onSosPressed: () -> Unit) {
    var isEnabled by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .size(200.dp)
            .clip(CircleShape)
            .neu(
                neuAttrs = NeuAttrs(
                    lightShadowColor = Color(0x81FFFFFF),
                    darkShadowColor = Color(0x99AAAACC),
                    shadowElevation = 5.dp,
                    lightSource = LightSource.LEFT_TOP,
                    shape = Pressed(RoundedCorner(150.dp)),
                )
            ), contentAlignment = Alignment.Center
    ) {

        var countDownTimer by remember { mutableStateOf(-1) }
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .size(155.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF736B), Color(0xFFFF3B30)
                        ), radius = 150f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = isEnabled) {
                        isEnabled = false
                        coroutineScope.launch {
                            onSosPressed()
                            for (sec in 3 downTo 1) {
                                countDownTimer = sec
                                println(countDownTimer)
                                delay(1000)
                            }
                            afterSosPressedDelay()
                            countDownTimer = -1
                            isEnabled = true
                        }
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(45.dp),
                    painter = painterResource(R.drawable.ic_filled_sos),
                    tint = Color.White,
                    contentDescription = "SOS Icon"
                )
                Text(
                    text = if (countDownTimer == -1) "SOS" else "$countDownTimer",
                    fontSize = 32.sp,
                    color = White,
                    fontFamily = poppinsBold,
                )
            }
        }
    }
}


class SosSoundPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun playOnce(onComplete: () -> Unit) {
        stop()

        mediaPlayer = MediaPlayer.create(context, R.raw.sos_tone).apply {
            isLooping = false
            setOnCompletionListener {
                stop()
                onComplete()
            }
        }

        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

