package net.sarasarasa.lifeup.vo

class VersionVO {
    var newVersion = 0
    var downloadUrl = ""
    var versionDesc = ""
    var versionName = ""

    override fun toString(): String {
        return "VersionVO(newVersion=$newVersion, downloadUrl='$downloadUrl', versionDesc='$versionDesc', versionName='$versionName')"
    }

}