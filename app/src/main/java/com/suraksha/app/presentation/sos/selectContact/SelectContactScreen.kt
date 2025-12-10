package com.suraksha.app.presentation.sos.selectContact

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.suraksha.app.R
import com.suraksha.app.domain.model.Contact
import com.suraksha.app.presentation.sos.components.ContactCard
import com.suraksha.app.presentation.theme.White
import com.suraksha.app.presentation.theme.buttonColorEnd
import com.suraksha.app.presentation.theme.buttonColorStart
import com.suraksha.app.presentation.theme.colorGrayBold
import com.suraksha.app.presentation.theme.colorGrayLight
import com.suraksha.app.presentation.theme.grayColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectContactScreen(
    viewModel: SelectContactVM = hiltViewModel(),
    modifier: Modifier = Modifier,
    navigateToSOSScreen: (contactList: List<Contact>) -> Unit = {},
    onBackClicked: () -> Unit = {},
    selectedContacts : Set<Contact> = emptySet(),
) {
    LaunchedEffect(Unit) {
        if(selectedContacts.isNotEmpty()) {
            viewModel.loadSelectedContact(selectedContacts)
        }
        viewModel.events.collect { event ->
            when (event) {
                is SelectContactNavEvent.NavigateToSOSScreen -> navigateToSOSScreen(event.contactList)
            }
        }
    }
    val contacts by viewModel.contactsFlow.collectAsState()
    val allContacts = contacts
    var contactsBasedOnQuery = contacts
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.fetchContacts()
        } else {
            Toast.makeText(context, "Permission required to show contacts", Toast.LENGTH_SHORT).show()
        }
    }

    // Check permission and request if needed
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) -> {
                viewModel.fetchContacts()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }
    var searchQuery by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf<Boolean>(false) }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.save_emergency_contacts),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorGrayBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Save ${viewModel.selectedContacts.joinToString(", ") { it.name }} as your emergency contacts?",
                        fontSize = 15.sp,
                        color = colorGrayLight
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { showDialog = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp, colorGrayBold
                            )
                        ) {
                            Text("Cancel", color = colorGrayBold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(buttonColorStart, buttonColorEnd)
                                    )
                                )
                                .clickable(onClick = {
                                    viewModel.onSaveSelectContactClicked()
                                    showDialog = false
                                }),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Save",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(40.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    IconButton(onClick = { showDialog = false }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "close",
                            tint = colorGrayBold
                        )
                    }
                }
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            CustomCenterTopBar(
                title = stringResource(R.string.select_emergency_contact),
                onBackClicked = onBackClicked,
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = grayColor,
                thickness = 0.4.dp
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    if(it.length <= 20) {
                        searchQuery = it
                        if (searchQuery.isEmpty()) {
                            contactsBasedOnQuery = allContacts
                        } else {
                            contactsBasedOnQuery = filterContacts(searchQuery, allContacts)
                        }
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(18.dp)
                    )
                },
                placeholder = {
                    Text(
                        "Search contact",
                        fontSize = 13.sp,
                        color = grayColor
                    )
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 13.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .heightIn(min = 30.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = grayColor,
                    unfocusedLeadingIconColor = grayColor
                )
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = grayColor,
                thickness = 0.4.dp
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                items(contactsBasedOnQuery.size) { index ->
                    val contact = contactsBasedOnQuery[index]
                    val isSelected = contact in viewModel.selectedContacts

                    ContactCard(
                        name = contact.name,
                        phoneNumber = contact.phoneNumber,
                        isSelected = isSelected,
                        onSelectChange = { selected ->
                            viewModel.toggleSelection(contact, selected)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            Button(
                modifier = Modifier
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 8.dp
                    )
                    .fillMaxWidth(),
                onClick = { showDialog = true },
                elevation = null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    buttonColorStart,
                                    buttonColorEnd
                                )
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save Selected Contacts (${viewModel.selectedContacts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun filterContacts(
    searchQuery: String,
    allContacts: List<Contact>,
): List<Contact> {
    val filteredContacts =
        allContacts.filter { it.name.contains(searchQuery , ignoreCase = true) || it.phoneNumber.contains(searchQuery, ignoreCase = true) }
    return filteredContacts
}

@Composable
fun CustomCenterTopBar(
    title: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
    ) {
        // Back Button
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colorGrayLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorGrayBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectContactScreenPreview() {

    Box(modifier = Modifier.background(Color.White)) {
        SelectContactScreen(
        )
    }
}