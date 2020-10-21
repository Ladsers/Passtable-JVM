import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.util.*
import kotlin.system.exitProcess

// TODO: change /Ladsers/temp-files/ to /Ladsers/Passtable/

class Updater {
    companion object{
        private val urlUpdate = URL("""https://raw.githubusercontent.com/Ladsers/temp-files/master/update.ini""")
        private lateinit var lastVer: String

        fun check(platform: String){
            val isNeedUpdate: Boolean

            println("\nChecking for updates...")
            try {
                val s = Scanner(urlUpdate.openStream())
                s.useDelimiter("[^\\S]")
                val lines = mutableListOf<String>()
                while (s.hasNextLine()) lines.add(s.next())
                isNeedUpdate = when(platform.toLowerCase()){
                    "jvm" -> {
                        lastVer = lines[3]
                        version != lines[3]
                    }
                    "android" -> {
                        lastVer = lines[5]
                        version != lines[5]
                    }
                    else -> {
                        lastVer = version
                        false
                    }
                }
            }
            catch (e:Exception){
                println("Failed: could not connect to the server!")
                return
            }

            if (isNeedUpdate){
                println("New version available! Do you want to download? (y/n)")
                when (readLine()!!){
                    tb.key("c_yes2") -> download()
                    else -> return
                }
            }
            else println("Ok")
        }

        private fun download(){
            println("Downloading...")
            val fos = FileOutputStream("Passtable-$lastVer.jar")
            try{
                val rbc = Channels.newChannel(getUrlDownload().openStream())
                fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
                rbc.close()
                println("Success! Open \"Passtable-$lastVer.jar\" in the same directory to continue working.")
                exitProcess(0)
            }
            catch (e:Exception){
                try {
                    val rbc = Channels.newChannel(getUrlDownload(true).openStream())
                    fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
                    rbc.close()
                    println("Success! Open \"Passtable-$lastVer.jar\" in the same directory to continue working.")
                    exitProcess(0)
                }
                catch (e:Exception){
                    println("Failed to download!")
                }
            }
            fos.close()
        }

        private fun getUrlDownload(withIndex: Boolean = false): URL{
            return if(withIndex){
                URL("https://github.com/Ladsers/temp-files/releases/download/$lastVer/Passtable-$lastVer.jar")
            }
            else{
                URL("https://github.com/Ladsers/temp-files/releases/download/${lastVer.dropLast(4)}/Passtable-$lastVer.jar")
            }
        }
    }
}

