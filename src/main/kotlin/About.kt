fun aboutText(){
    val webRepo = """https://github.com/Ladsers/Passtable-JVM""" //TODO: put away
    val webPage = """https://www.ladsers.com/Passtable""" //TODO: put away
    println("Passtable (%s)".format(tb.key("about_jvmApp")))
    println(tb.key("about_version").format(version))
    tb.println("about_createdBy")
    println()

    tb.println("about_webResources")
    println(tb.key("header_projectpage").format(webPage))
    println(tb.key("header_projectrepo").format(webRepo))
    println()

    tb.println("about_license")
    println()

    tb.println("about_thirdPartyResources")
    println("""
Cryptographic algorithms:
Bouncy Castle
Legion of the Bouncy Castle Inc.
MIT License
https://bouncycastle.org
    """.trimIndent())
    println()
    println("""ASCII Logo:
Small by Glenn Chappell 4/93 -- based on Standard
Includes ISO Latin-1
Modified by Paul Burton <solution@earthlink.net> 12/96 to include new parameter
supported by FIGlet and FIGWin.
    """.trimMargin())
}