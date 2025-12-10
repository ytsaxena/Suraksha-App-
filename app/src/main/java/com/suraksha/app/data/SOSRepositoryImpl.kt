package com.suraksha.app.data

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.SmsManager
import com.google.firebase.firestore.FirebaseFirestore
import com.suraksha.app.domain.SOSRepository
import com.suraksha.app.domain.model.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class SOSRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
) : SOSRepository {
    override suspend fun getContact(): List<Contact> = withContext(Dispatchers.IO) {
        val contactList = mutableListOf<Contact>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ), null, null, null
        )
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                contactList.add(Contact(name, number))
            }
        }
        contactList
    }

    override suspend fun saveContacts(contacts: List<Contact>) {
        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val dtoList = contacts.map {
            EmergencyContactDto(
                id = UUID.randomUUID().toString(),
                name = it.name,
                phoneNumber = it.phoneNumber
            )
        }
        firestore.collection("emergency_contacts")
            .document(deviceId)
            .set(mapOf("contacts" to dtoList))
            .await()
    }

    override suspend fun selectedContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val snapshot = firestore.collection("emergency_contacts")
            .document(deviceId)
            .get()
            .await()

        val dtoList = snapshot.get("contacts") as? List<Map<String, Any>> ?: emptyList()

        return@withContext dtoList.map { map ->
            Contact(
                name = map["name"] as? String ?: "",
                phoneNumber = map["phoneNumber"] as? String ?: ""
            )
        }
    }

    override suspend fun sendSms(phoneNumber: String, message: String): Result<Unit> {
        return try {
            val smsManager = context.getSystemService(SmsManager::class.java)

            val parts = smsManager.divideMessage(message)

            smsManager.sendMultipartTextMessage(
                phoneNumber, null, parts, null, null
            )

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

}

data class EmergencyContactDto(
    val id: String,
    val name: String,
    val phoneNumber: String,
)