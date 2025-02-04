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
        return "function descramble_sig($varName) { let $objName={$functions}; ${match.groups[2]?.value} };"
    }

    fun getNsigSource(baseJs: String): String {
        val regexOpts = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
        var variable = ""
        val regex =
            "([a-zA-z0-9]+)=function\\(\\S\\)\\{var \\S=\\S\\.split.*-_w8_\\\"\\+[a-zA-Z]\\}return \\S\\.join\\(\\\"\\\"\\)\\};"
        val nsig = Regex(regex, regexOpts)
        val result = nsig.find(baseJs)
        if (result != null) {
            val variableRegex = "typeof (.*)===\\\"undefined\\\""
            val varMatch = Regex(variableRegex, regexOpts)
            val variable = varMatch.find(result.groupValues[0])
            var variableGrpValue =""
            if (variable != null ) {
                variableGrpValue = variable.groupValues[1]
                if(variableGrpValue.contains('$')){
                    variableGrpValue = variableGrpValue.replace("$", "\\\$")
                    Log.d("variable group match", variableGrpValue)
                }

            }
            val variableValueRegexPattern = "var $variableGrpValue=[[-][0-9]]+;"
            val variableValueRegex = Regex(pattern = variableValueRegexPattern)
            val variableMatch = variableValueRegex.find(baseJs)
            if (variableMatch != null) {

                Log.d("variableMatch", variableMatch.groupValues[0])

                return variableMatch.groupValues[0] + "var "+ result.groupValues[0] + "${result.groupValues[1]}(nsig);"
            }
        }
        return "hello"
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