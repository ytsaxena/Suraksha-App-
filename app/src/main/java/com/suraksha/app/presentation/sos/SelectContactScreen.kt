package com.suraksha.app.presentation.sos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
    modifier: Modifier = Modifier,
    contacts: List<Contact>,
    onSelectContactClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    var selectedContacts by remember { mutableStateOf(setOf<Int>()) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.select_emergency_contact),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorGrayBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(16.dp),
                            tint = colorGrayLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .background(Color.White)
                .padding(bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding() + 20.dp)
        ) {
            Divider(Modifier.fillMaxWidth().padding(bottom = 8.dp), color = grayColor, thickness = 0.4.dp)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
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
            Divider(Modifier.fillMaxWidth().padding(top = 8.dp), color = grayColor, thickness = 0.4.dp)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                items(contacts.size) { index ->
                    val (name, phone) = contacts[index]
                    val isSelected = index in selectedContacts

                    ContactCard(
                        name = name,
                        phoneNumber = phone,
                        isSelected = isSelected,
                        onSelectChange = { selected ->
                            selectedContacts =
                                if (selected) selectedContacts + index
                                else selectedContacts - index
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
                onClick = { onSelectContactClicked() },
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
                        text = "Save Selected Contacts (${selectedContacts.size})",
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

@Preview(showBackground = true)
@Composable
private fun SelectContactScreenPreview() {
    val contacts = List(8) { index ->
        Contact(
            name = "Aditi Sharma",
            phoneNumber = "+91 000000000$index"
        )
    }

    Box(modifier = Modifier.background(Color.White)) {
        SelectContactScreen(
            contacts = contacts,
            onSelectContactClicked = {}
        )
    }
}