package uk.dudley.servicerequest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFF003366), // Dudley-ish blue
                    secondary = androidx.compose.ui.graphics.Color(0xFF4A90A4)
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ServiceRequestScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceRequestScreen() {
    val context = LocalContext.current

    var category by remember { mutableStateOf("Pothole / Road defect") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") } // used for BCC
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Pothole / Road defect",
        "Fly-tipping / Dumped rubbish",
        "Missed bin collection",
        "Street lighting / Traffic signals",
        "Graffiti",
        "Overgrown vegetation / Hedges",
        "Blocked drain / Flooding",
        "Abandoned vehicle",
        "Anti-social behaviour",
        "Other / General enquiry"
    )

    val councilEmail = "dudleycouncilplus@dudley.gov.uk"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dudley Service Request", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Email a service request to Dudley Council. Leave personal details blank for an anonymous-style report. Your email (if provided) will be BCC'd so you receive a copy.",
                style = MaterialTheme.typography.bodyMedium
            )

            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                category = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location / Address / Postcode") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description of the issue / request") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                maxLines = 8
            )

            Divider()

            Text("Optional contact details (leave blank for anonymous)", fontWeight = FontWeight.SemiBold)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Your email (for BCC copy)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val subject = "Service Request: $category - $location"
                    val body = buildString {
                        appendLine("SERVICE REQUEST TO DUDLEY COUNCIL")
                        appendLine("================================")
                        appendLine()
                        appendLine("Category: $category")
                        appendLine("Location: $location")
                        appendLine()
                        appendLine("Description:")
                        appendLine(description)
                        appendLine()
                        if (name.isNotBlank() || phone.isNotBlank() || email.isNotBlank()) {
                            appendLine("Contact details:")
                            if (name.isNotBlank()) appendLine("Name: $name")
                            if (phone.isNotBlank()) appendLine("Phone: $phone")
                            if (email.isNotBlank()) appendLine("Email: $email")
                        } else {
                            appendLine("Contact details: (none provided – anonymous report)")
                        }
                        appendLine()
                        appendLine("Sent via Dudley Service Request Emailer app")
                    }

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(councilEmail))
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, body)
                        if (email.isNotBlank()) {
                            putExtra(Intent.EXTRA_BCC, arrayOf(email))
                        }
                    }

                    // Fallback if no email client
                    try {
                        context.startActivity(Intent.createChooser(intent, "Send service request via…"))
                    } catch (e: Exception) {
                        // Could show a Toast here in a real app
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = location.isNotBlank() && description.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Send Email to Council", fontSize = 16.sp)
            }

            Text(
                "To: $councilEmail\n" +
                "BCC: ${if (email.isNotBlank()) email else "(none)"}\n\n" +
                "This app only opens your email client. Nothing is sent until you press Send in your email app.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
