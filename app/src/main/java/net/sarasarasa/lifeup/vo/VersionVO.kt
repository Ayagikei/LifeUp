package net.sarasarasa.lifeup.vo

class VersionVO {
    var newVersion = 0
    var downloadUrl = ""

    override fun toString(): String {
        return "VersionVO(newVersion=$newVersion, downloadUrl='$downloadUrl')"
    }

}