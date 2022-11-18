package net.lifeupapp.lifeup.http.service

import android.util.Log
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.html
import kotlinx.html.i
import kotlinx.html.p
import kotlinx.html.stream.appendHTML
import net.lifeupapp.lifeup.api.LifeUpApi
import net.lifeupapp.lifeup.http.base.AppScope
import net.lifeupapp.lifeup.http.base.appCtx
import net.lifeupapp.lifeup.http.utils.getIpAddressInLocalNetwork
import net.lifeupapp.lifeup.http.vo.RawQueryVo
import java.util.logging.Logger

object KtorService : LifeUpService {

    // FIXME: random the port if the port is occupied
    private val port = 13276

    private val logger = Logger.getLogger("LifeUp-Http")

    private val _isRunning = MutableStateFlow(LifeUpService.RunningState.NOT_RUNNING)
    private val _errorMessage = MutableStateFlow<Throwable?>(null)

    override val isRunning: StateFlow<LifeUpService.RunningState>
        get() = _isRunning

    override val errorMessage: StateFlow<Throwable?>
        get() = _errorMessage

    private val mutex = Mutex()

    init {
        AppScope.launch {
            _isRunning.collect {
                if (LifeUpService.RunningState.RUNNING == it) {
                    ServerNotificationService.start(appCtx)
                } else {
                    ServerNotificationService.cancel(appCtx)
                }
            }
        }
    }

    private var server: NettyApplicationEngine? = newService

    val newService
        get() = embeddedServer(Netty, port, watchPaths = emptyList()) {
            install(WebSockets)
            install(CallLogging)
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/") {
                    val localAddressIp = getIpAddressInLocalNetwork() ?: "UNKNOWN"
                    call.respondText(ContentType.Text.Html) {
                        buildString {
                            appendHTML().html {
                                body {
                                    h1 {
                                        +"Hello from "
                                        i {
                                            +"LifeUp Cloud!"
                                        }
                                    }
                                    p { +"Now you can call LifeUp api on your computers (until the app is killed by the Android)." }
                                    p { +"take the 'lifeup://api/reward?type=coin&content=Call LifeUp API from HTTP&number=1' as a example" }
                                    h2 {
                                        +"GET request"
                                    }
                                    p {
                                        +"http://${localAddressIp}:$port/api?url=YOUR_ENCODED_API_URL"
                                    }
                                    p {
                                        +"you can send the get request to call in directly, but you need to encode the API like this: "
                                    }
                                    p {
                                        a(
                                            href = "http://${localAddressIp}:$port/api?url=lifeup%3A%2F%2Fapi%2Freward%3Ftype%3Dcoin%26content%3DCall%20LifeUp%20API%20from%20HTTP%26number%3D1",
                                            target = "_blank"
                                        ) {
                                            +"http://${localAddressIp}:$port/api?url=lifeup%3A%2F%2Fapi%2Freward%3Ftype%3Dcoin%26content%3DCall%20LifeUp%20API%20from%20HTTP%26number%3D1"
                                        }
                                    }
                                    div()
                                    h2 {
                                        +"POST request"
                                    }
                                    p {
                                        +"http://${localAddressIp}$port/api"
                                    }
                                    p {
                                        +"or you can POST it the below URL with 'application/json' content type and the body is a json string like this: "
                                    }
                                    p {
                                        +"{\n"
                                        +"  \"url\": \"lifeup://api/reward?type=coin&content=Call LifeUp API from HTTP&number=1\"\n"
                                        +"}"
                                    }
                                }
                            }
                        }
                    }
                }

                get("/api") {
                    call.request.queryParameters["url"]?.let { url ->
                        logger.info("Got url: ${url}")
                        LifeUpApi.call(appCtx, url)
                    }
                    call.respond("success\ncheck your phone!")
                }

                post<RawQueryVo>("/api") {
                    logger.info("Got url: ${it.url}")
                    LifeUpApi.call(appCtx, it.url)
                    call.respond("success")
                }
            }
        }

    override fun start() {
        AppScope.launch(Dispatchers.IO) {
            logger.info("Starting server...")
            mutex.withLock {
                if (_isRunning.value != LifeUpService.RunningState.NOT_RUNNING) {
                    logger.info("Server is already running")
                    return@launch
                }
                _errorMessage.value = null
                _isRunning.value = LifeUpService.RunningState.STARTING
                _isRunning.value = LifeUpService.RunningState.RUNNING
                if (server == null) {
                    server = newService
                }
            }
            kotlin.runCatching {
                server!!.start(wait = true)
            }.onFailure {
                _isRunning.value = LifeUpService.RunningState.NOT_RUNNING
                _errorMessage.value = it
            }

        }
    }

    override fun stop() {
        AppScope.launch(Dispatchers.IO) {
            mutex.withLock {
                if (_isRunning.value != LifeUpService.RunningState.RUNNING) {
                    logger.info("Server is not running")
                    return@launch
                }

                logger.info("Stopping server")
                server?.stop(1_000, 2_000)
                server = null
                _isRunning.value = LifeUpService.RunningState.NOT_RUNNING
            }
        }
    }
}