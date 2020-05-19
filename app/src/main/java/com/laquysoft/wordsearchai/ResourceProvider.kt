package com.laquysoft.wordsearchai

import android.content.Context
import android.content.res.Resources
import java.io.InputStream

class ResourceProvider(private val context: Context) {

    fun getDictionaryStream(): InputStream {
        val resources: Resources = context.resources
        val dictionaryFileId = resources.getIdentifier(
            "dictionary",
            "raw", context.packageName
        )

        return resources.openRawResource(dictionaryFileId)
    }


}
