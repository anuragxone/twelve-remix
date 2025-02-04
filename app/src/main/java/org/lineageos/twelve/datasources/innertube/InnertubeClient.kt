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
    val pot = "MnTqwowGzQcg7Or6qcSESAUMOV7rA3T2RfuYaeBa58OZMTJQG9BHUujQJXip1woHFciwSPfHx5E7tl5NnOUF0wPBKfd23ZpMwnhpwW8pLLDu5iUKyUkIoWXrgOxkQ_ZW6osHB348CaqH3Bc8obOtofcwiLLsZw=="

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
                      "visitorData": "CgtmWWFldUV5NDZ4USjMv4i9BjIKCgJJThIEGgAgVg%3D%3D",
                      "userAgent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36,gzip(gfe)",
                      "clientName": "WEB_REMIX",
                      "clientVersion": "1.20250129.01.00",
                      "osName": "X11",
                      "osVersion": "",
                      "originalUrl": "https://music.youtube.com/",
                      "platform": "DESKTOP",
                      "clientFormFactor": "UNKNOWN_FORM_FACTOR",
                      "configInfo": {
                        "appInstallData": "CMy_iL0GEObPsQUQr8LOHBDqw68FEJmYsQUQ48nOHBCBzc4cEIWnsQUQytixBRCI468FEMK3zhwQvLLOHBDE2LEFEI7QsQUQppOxBRD2q7AFEN-0zhwQvYqwBRCDw7EFEOeazhwQjNCxBRDBzbEFEIqhsQUQi9SxBRDerbEFEL22rgUQkrjOHBDJ5rAFEOfQzhwQgdaxBRCZ0v8SEI3UsQUQytSxBRCZjbEFEL2ZsAUQyNixBRDEu84cENCNsAUQxtixBRCEvc4cEJT-sAUQ_eX_EhDT4a8FEKKjzhwQms6xBRCBw7EFEIiwzhwQyfevBRCHrM4cEOW5sQUQjtTOHBCi1LEFELfq_hIQ0ZTOHBDr6P4SEIiHsAUQjtexBRDiuLAFEI_DsQUQwLfOHBDM364FEJLLsQUQ_LLOHBC14_8SEMHCzhwQ3rzOHBDtubEFEIfDsQUQ-rjOHBC3pM4cEJS7zhwQ3MjOHBCuwc4cEPirsQUQntCwBRDYvs4cEO66zhwqIENBTVNFaFVKb0wyd0ROSGtCdUhkaFFxal93UWRCdz09",
                        "coldConfigData": "CMy_iL0GGjJBT2pGb3gwVU5ISFBtQ0U3bWVja2NoUEVkczFHTFZQeVh2M2VneXQzdFpsazJ6NWlhdyIyQU9qRm94MFVOSEhQbUNFN21lY2tjaFBFZHMxR0xWUHlYdjNlZ3l0M3RabGsyejVpYXc%3D",
                        "coldHashData": "CMy_iL0GEhM4MzcyMjg4Nzg1MDY2MDg0NzkyGMy_iL0GMjJBT2pGb3gwVU5ISFBtQ0U3bWVja2NoUEVkczFHTFZQeVh2M2VneXQzdFpsazJ6NWlhdzoyQU9qRm94MFVOSEhQbUNFN21lY2tjaFBFZHMxR0xWUHlYdjNlZ3l0M3RabGsyejVpYXc%3D",
                        "hotHashData": "CMy_iL0GEhQxNzM2OTIzMjY3NTA1ODYxMDMzMBjMv4i9BjIyQU9qRm94MFVOSEhQbUNFN21lY2tjaFBFZHMxR0xWUHlYdjNlZ3l0M3RabGsyejVpYXc6MkFPakZveDBVTkhIUG1DRTdtZWNrY2hQRWRzMUdMVlB5WHYzZWd5dDN0WmxrMno1aWF3"
                      },
                      "browserName": "Chrome",
                      "browserVersion": "132.0.0.0",
                      "acceptHeader": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                      "deviceExperimentId": "ChxOelEyTnpVMk5qQTVOREkyT0RreE1qVXdPUT09EMy_iL0GGMy_iL0G",
                      "rolloutToken": "CNas9fyc3vyTiAEQ_K3DjZmqiwMY_K3DjZmqiwM%3D",
                      "screenWidthPoints": 638,
                      "screenHeightPoints": 957,
                      "screenPixelDensity": 1,
                      "screenDensityFloat": 1,
                      "utcOffsetMinutes": 330,
                      "userInterfaceTheme": "USER_INTERFACE_THEME_DARK",
                      "connectionType": "CONN_CELLULAR_4G",
                      "timeZone": "Asia/Calcutta",
                      "playerType": "UNIPLAYER",
                      "tvAppInfo": { "livingRoomAppMode": "LIVING_ROOM_APP_MODE_UNSPECIFIED" },
                      "clientScreen": "WATCH_FULL_SCREEN"
                    },
                    "user": { "lockedSafetyMode": false },
                    "request": {
                      "useSsl": true,
                      "internalExperimentFlags": [],
                      "consistencyTokenJars": []
                    },
                    "clientScreenNonce": "ef6W0cCndoMi40Ei",
                    "adSignalsInfo": {
                      "params": [
                        { "key": "dt", "value": "1738678221625" },
                        { "key": "flash", "value": "0" },
                        { "key": "frm", "value": "0" },
                        { "key": "u_tz", "value": "330" },
                        { "key": "u_his", "value": "4" },
                        { "key": "u_h", "value": "1080" },
                        { "key": "u_w", "value": "1920" },
                        { "key": "u_ah", "value": "1045" },
                        { "key": "u_aw", "value": "1920" },
                        { "key": "u_cd", "value": "24" },
                        { "key": "bc", "value": "31" },
                        { "key": "bih", "value": "957" },
                        { "key": "biw", "value": "623" },
                        { "key": "brdim", "value": "0,35,0,35,1920,35,1920,1045,638,957" },
                        { "key": "vis", "value": "1" },
                        { "key": "wgl", "value": "true" },
                        { "key": "ca_type", "value": "image" }
                      ]
                    },
                    "clickTracking": {
                      "clickTrackingParams": "CIIDEMjeAiITCNTu4bGZqosDFRDlNAcd7-wRYw=="
                    }
                  },
                  "playbackContext": {
                    "contentPlaybackContext": {
                      "html5Preference": "HTML5_PREF_WANTS",
                      "lactMilliseconds": "17",
                      "referer": "https://music.youtube.com/search?q=timeless+playboi+carti",
                      "signatureTimestamp": 20118,
                      "autoCaptionsDefaultOn": false,
                      "mdxContext": {},
                      "vis": 10
                    }
                  },
                  "cpn": "e_trSOTIOFAmRZQC",
                  "captionParams": {},
                  "serviceIntegrityDimensions": {
                    "poToken": "MnTqwowGzQcg7Or6qcSESAUMOV7rA3T2RfuYaeBa58OZMTJQG9BHUujQJXip1woHFciwSPfHx5E7tl5NnOUF0wPBKfd23ZpMwnhpwW8pLLDu5iUKyUkIoWXrgOxkQ_ZW6osHB348CaqH3Bc8obOtofcwiLLsZw=="
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
                append("sec-ch-ua-full-version", "132.0.6834.159")
                append(
                    "sec-ch-ua-full-version-list",
                    """"Not A(Brand";v="8.0.0.0", "Chromium";v="132.0.6834.159", "Google Chrome";v="132.0.6834.159""""
                )
                append("sec-ch-ua-mobile", "?0")
                append("sec-ch-ua-model", "\"\"")
                append("sec-ch-ua-platform", "\"Linux\"")
                append("sec-ch-ua-platform-version", "\"6.8.0\"")
                append("sec-fetch-dest", "empty")
                append("sec-fetch-mode", "cors")
                append("sec-fetch-site", "same-origin")
                append(
                    HttpHeaders.UserAgent,
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36"
                )
                append("x-goog-visitor-id", "CgtmWWFldUV5NDZ4USjMv4i9BjIKCgJJThIEGgAgVg%3D%3D")
                append("x-youtube-bootstrap-logged-in", "false")
                append("x-youtube-client-name", "67")
                append("x-youtube-client-version", "1.20250129.01.00")
            }
            setBody(bodyJson)
        }
        return response.bodyAsText()

    }

    suspend fun getBaseJs(): String {
        val response =
            ktorclient.client.get("https://music.youtube.com/s/player/0f7c1eff/player_ias.vflset/en_US/base.js")
        val baseJs = response.bodyAsText()
        return baseJs
    }

}