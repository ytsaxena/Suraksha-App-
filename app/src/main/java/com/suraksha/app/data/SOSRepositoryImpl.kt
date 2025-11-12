package com.suraksha.app.data

import android.content.ContentResolver
import android.provider.ContactsContract
import com.suraksha.app.domain.SOSRepository
import com.suraksha.app.domain.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SOSRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
): SOSRepository {
    override suspend fun getContact(): List<Contact> = withContext(Dispatchers.IO){
        val contactList = mutableListOf<Contact>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null, null
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
}