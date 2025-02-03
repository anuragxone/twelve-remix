package org.lineageos.twelve.datasources.innertube

import android.util.Log
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.path
import org.lineageos.twelve.utils.ktorclient.KtorClient

class InnertubeClient(private val ktorclient: KtorClient) {

    suspend fun getInfo(videoId: String): String {
       val response =  ktorclient.client.post {

            val bodyJson = """
        {
    "videoId": "$videoId",
    "context": {
        "client": {
            "hl": "en",
            "gl": "IN",
            "remoteHost": "",
            "deviceMake": "",
            "deviceModel": "",
            "visitorData": "Cgt2akxrRE1wcTllNCiJg6u8BjIKCgJJThIEGgAgQg%3D%3D",
            "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36,gzip(gfe)",
            "clientName": "WEB_REMIX",
            "clientVersion": "1.20250113.00.00",
            "osName": "Windows",
            "osVersion": "10.0",
            "originalUrl": "https://music.youtube.com/",
            "platform": "DESKTOP",
            "clientFormFactor": "UNKNOWN_FORM_FACTOR",
            "configInfo": {
                "appInstallData": "CImDq7wGEMHCzhwQiqGxBRDAt84cEJnS_xIQg4WvBRDJ968FEMK3zhwQppOxBRDerbEFEN-0zhwQg8OxBRC9mbAFEMjYsQUQjNCxBRCFp7EFEIaszhwQmY2xBRDQjbAFEOvo_hIQr8LOHBCIsM4cEIfDsQUQiOOvBRDBq84cEJmYsQUQiIewBRDRlM4cEMHNsQUQlP6wBRDqws4cEOW5sQUQ3cjOHBC9irAFEO25sQUQ-KuxBRDnms4cEMO7zhwQksuxBRC36v4SEMbYsQUQt6TOHBCBw7EFEIvUsQUQ_LLOHBCEvc4cEMrUsQUQt--vBRCio84cEIHWsQUQjtCxBRC7ss4cEParsAUQ5s-xBRC-ws4cEKLUsQUQ0-GvBRCazrEFENmqzhwQ4riwBRCPw7EFEJS7zhwQ07nOHBCd0LAFEL22rgUQyeawBRDK2LEFEI7XsQUQrsHOHBDM364FEMTYsQUQq57OHBD6uM4cEJK4zhwQjdSxBRDqw68FEN68zhwQs8DOHBDYvs4cKiBDQU1TRWhVSm9MMndETkhrQnVIZGhRcWpfd1FkQnc9PQ%3D%3D",
                "coldConfigData": "CImDq7wGGjJBT2pGb3gxUFZOV0ttYno3MVI2Nk10Vkw2R1B1MzZEMjBtQXd2TE10QlBKdW4yLWN4ZyIyQU9qRm94MVBWTldLbWJ6NzFSNjZNdFZMNkdQdTM2RDIwbUF3dkxNdEJQSnVuMi1jeGc%3D",
                "coldHashData": "CImDq7wGEhM4MzcyMjg4Nzg1MDY2MDg0NzkyGImDq7wGMjJBT2pGb3gxUFZOV0ttYno3MVI2Nk10Vkw2R1B1MzZEMjBtQXd2TE10QlBKdW4yLWN4ZzoyQU9qRm94MVBWTldLbWJ6NzFSNjZNdFZMNkdQdTM2RDIwbUF3dkxNdEJQSnVuMi1jeGc%3D",
                "hotHashData": "CImDq7wGEhQxNzM2OTIzMjY3NTA1ODYxMDMzMBiJg6u8BjIyQU9qRm94MVBWTldLbWJ6NzFSNjZNdFZMNkdQdTM2RDIwbUF3dkxNdEJQSnVuMi1jeGc6MkFPakZveDFQVk5XS21iejcxUjY2TXRWTDZHUHUzNkQyMG1Bd3ZMTXRCUEp1bjItY3hn"
            },
            "browserName": "Chrome",
            "browserVersion": "132.0.0.0",
            "acceptHeader": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
            "deviceExperimentId": "ChxOelEyTURrNE9EVXlPREkyTWpNMU9UQXlNdz09EImDq7wGGImDq7wG",
            "rolloutToken": "CKOInNWyv8q9GhDcxvX8z_2KAxjcxvX8z_2KAw%3D%3D",
            "screenWidthPoints": 1045,
            "screenHeightPoints": 765,
            "screenPixelDensity": 1,
            "screenDensityFloat": 1,
            "utcOffsetMinutes": 330,
            "userInterfaceTheme": "USER_INTERFACE_THEME_DARK",
            "connectionType": "CONN_CELLULAR_4G",
            "timeZone": "Asia/Calcutta",
            "playerType": "UNIPLAYER",
            "tvAppInfo": {
                "livingRoomAppMode": "LIVING_ROOM_APP_MODE_UNSPECIFIED"
            },
            "clientScreen": "WATCH_FULL_SCREEN"
        },
        "user": {
            "lockedSafetyMode": false
        },
        "request": {
            "useSsl": true,
            "internalExperimentFlags": [],
            "consistencyTokenJars": []
        },
        "clientScreenNonce": "ch3N85NfI06dEmR4",
        "adSignalsInfo": {
            "params": [
                {
                    "key": "dt",
                    "value": "1737146761329"
                },
                {
                    "key": "flash",
                    "value": "0"
                },
                {
                    "key": "frm",
                    "value": "0"
                },
                {
                    "key": "u_tz",
                    "value": "330"
                },
                {
                    "key": "u_his",
                    "value": "4"
                },
                {
                    "key": "u_h",
                    "value": "900"
                },
                {
                    "key": "u_w",
                    "value": "1600"
                },
                {
                    "key": "u_ah",
                    "value": "852"
                },
                {
                    "key": "u_aw",
                    "value": "1600"
                },
                {
                    "key": "u_cd",
                    "value": "24"
                },
                {
                    "key": "bc",
                    "value": "31"
                },
                {
                    "key": "bih",
                    "value": "765"
                },
                {
                    "key": "biw",
                    "value": "1028"
                },
                {
                    "key": "brdim",
                    "value": "0,0,0,0,1600,0,1600,852,1045,765"
                },
                {
                    "key": "vis",
                    "value": "1"
                },
                {
                    "key": "wgl",
                    "value": "true"
                },
                {
                    "key": "ca_type",
                    "value": "image"
                }
            ]
        },
        "clickTracking": {
            "clickTrackingParams": "CIUDEMjeAiITCPPAkIPQ_YoDFUKGSwUdoyYXwA=="
        }
    },
    "playbackContext": {
        "contentPlaybackContext": {
            "html5Preference": "HTML5_PREF_WANTS",
            "lactMilliseconds": "13",
            "referer": "https://music.youtube.com/",
            "signatureTimestamp": 20102,
            "autoCaptionsDefaultOn": false,
            "mdxContext": {},
            "vis": 10
        }
    },
    "cpn": "CiSoNkKYfS4kVs9C",
    "captionParams": {},
    "serviceIntegrityDimensions": {
        "poToken": "MnQdsdbQg7qCfR2JS2iMDUTlm8YSDtJ9eWjx6bC205rbNJSa20b1RRj2CowgmBXzdQf3pd22CBnRxOc2WiyXkpkx1nShvP-BzBzHPXczA8SXEe-RalpKJC1u_63z1f4Kpp9NVwMGUHEPGOgp6AbYp0s5labdeA=="
    }
}
    """.trimIndent()

//        contentType(ContentType.Application.Json)
            url {
                protocol = URLProtocol.HTTPS
                host = "music.youtube.com"
                path("youtubei/v1/player")
                parameters.append("prettyPrint", "false")
            }
            headers {
                append(HttpHeaders.Accept, "*/*")
                append(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
                append(HttpHeaders.ContentType, "application/json")
                append(HttpHeaders.Cookie, "")
                append(HttpHeaders.Origin, "https://music.youtube.com")
                append("priority", "u=1, i")
                append(HttpHeaders.Referrer, "https://music.youtube.com")
                append(
                    "sec-ch-ua",
                    """"Not A(Brand";v="8", "Chromium";v="132", "Google Chrome";v="132""""
                )
                append("sec-ch-arch", "x86")
                append("sec-ch-bitness", "64")
                append("sec-ch-ua-form-factors", "Desktop")
                append("sec-ch-ua-full-version", "132.0.6834.84")
                append(
                    "sec-ch-ua-full-version-list",
                    """"Not A(Brand";v="8.0.0.0", "Chromium";v="132.0.6834.84", "Google Chrome";v="132.0.6834.84""""
                )
                append("sec-ch-ua-mobile", "?0")
                append("sec-ch-ua-model", "\"\"")
                append("sec-ch-ua-platform", "Windows")
                append("sec-ch-ua-platform-version", "\"19.0.0\"")
                append("sec-fetch-dest", "empty")
                append("sec-fetch-mode", "cors")
                append("sec-fetch-site", "same-origin")
                append(
                    HttpHeaders.UserAgent,
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36"
                )
                append("x-goog-visitor-id", "Cgt2akxrRE1wcTllNCiJg6u8BjIKCgJJThIEGgAgQg%3D%3D")
                append("x-youtube-bootstrap-logged-in", "false")
                append("x-youtube-client-version", "1.20250113.00.00")
            }
            setBody(bodyJson)
        }
        return response.bodyAsText()

    }

    suspend fun getBaseJs(): String {
        val response =
            ktorclient.client.get("https://music.youtube.com/s/player/f3d47b5a/player_ias.vflset/en_US/base.js")
        val baseJs = response.bodyAsText()
        return baseJs
    }

}