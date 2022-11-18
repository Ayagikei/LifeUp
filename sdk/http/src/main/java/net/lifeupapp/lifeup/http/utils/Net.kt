package net.lifeupapp.lifeup.http.utils

import java.net.NetworkInterface


fun getIpAddressInLocalNetwork(): String? {
    val localAddresses = getIpAddressListInLocalNetwork()
    return localAddresses.firstOrNull()
}

fun getIpAddressListInLocalNetwork(): List<String> {
    val networkInterfaces = NetworkInterface.getNetworkInterfaces().iterator().asSequence()
    return networkInterfaces.flatMap {
        it.inetAddresses.asSequence()
            .filter { inetAddress ->
                inetAddress.isSiteLocalAddress && !inetAddress.hostAddress.contains(":") &&
                        inetAddress.hostAddress != "127.0.0.1"
            }
            .map { inetAddress -> inetAddress.hostAddress }
    }.toList()
}
