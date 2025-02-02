package org.lineageos.twelve.repositories.innertube

import android.util.Log
import kotlinx.coroutines.coroutineScope
import org.lineageos.twelve.datasources.innertube.InnertubeClient

class Player(private val innertubeClient: InnertubeClient) {

    fun extractSigSourceCode(baseJs: String): String {
        val regex =
            Regex("""function\(([A-Za-z_0-9]+)\)\{([A-Za-z_0-9]+=[A-Za-z_0-9]+\.split\(""\)(.+?)\.join\(""\))\}""")
        val match = regex.find(baseJs)

        if (match == null) {
            Log.d("error1", "Failed to extract signature decipher algorithm.")
            return ""
        }

        val varName = match.groups[1]?.value
        val objName =
            match.groups[3]?.value?.split(Regex("""\.|\["""))?.get(0)?.replace(";", "")?.trim()
        val functions = getStringBetweenStrings(baseJs, "var $objName={", "};")

        if (functions.isNullOrEmpty() || varName.isNullOrEmpty()) {
            Log.d("error2", "Failed to extract signature decipher algorithm.")
            return ""
        }
        return "function descramble_sig($varName) { let $objName={$functions}; ${match.groups[2]?.value} } descramble_sig(sig);"
    }

    fun getNsigSource(baseJs: String): String {
        val regexOpts = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
        val regex =
            "[a-zA-z0-9]+=function\\(\\S\\)\\{var \\S=\\S\\.split.*-_w8_\\\"\\+[a-zA-Z]\\}return \\S\\.join\\(\\\"\\\"\\)\\};"
        val nsig = Regex(regex, regexOpts)
        val result = nsig.find(baseJs)
        if (result != null) {
            return result.groupValues[0]
        }
        return ""
    }

    private fun getStringBetweenStrings(
        data: String,
        startString: String,
        endString: String
    ): String? {
        val regex = Regex(
            "${escapeStringRegexp(startString)}(.*?)${escapeStringRegexp(endString)}",
            RegexOption.DOT_MATCHES_ALL
        )
        val matchResult = regex.find(data)
        return matchResult?.groups?.get(1)?.value
    }

    fun escapeStringRegexp(input: String): String {
        return input.replace(Regex("[|\\\\{}()\\[\\]^$+*?.]")) { "\\${it.value}" }
            .replace("-", "\\x2d")
    }


}