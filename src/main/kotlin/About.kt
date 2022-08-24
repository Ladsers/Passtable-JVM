fun aboutText(){
    val webRepo = """https://github.com/Ladsers/Passtable-JVM""" //TODO: put away
    val webPage = """https://www.ladsers.com/Passtable""" //TODO: put away
    println("Passtable (JVM Application)")
    println("Version: $version")
    println("Created by Max Korolev\n")

    println("Web resources")
    println(tb.key("header_projectpage").format(webPage))
    println(tb.key("header_projectrepo").format(webRepo))
    println()

    println("Licensed under the Apache License, Version 2.0")
    println(tb.key("header_license"))
    println()

    println("THIRD PARTY RESOURCES\n")
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