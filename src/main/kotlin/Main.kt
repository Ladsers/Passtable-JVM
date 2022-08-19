import com.ladsers.passtable.lib.Updater

val tb = TextBundle()
val osWindows = System.getProperty("os.name").startsWith("win", true)
val jre8 = System.getProperty("java.version").startsWith("1.8.")
const val version =  "22.8.0"

fun main(args: Array<String>)
{
    val argList = args.toMutableList()
    if (argList.isNotEmpty() && !osWindows && jre8) {
        when (argList[0].lowercase()) {
            "-ru-ru" -> {
                tb.changeLocale("ru")
                argList.removeAt(0)
            }
        }
    }

    printHeader()
    Updater.run()

    when {
        argList.size > 1 -> Processor.openProcess(argList[0], argList[1])
        argList.isNotEmpty() -> {
            println()
            Processor.openProcess(argList[0])
        }
        else -> Processor.quickStart()
    }
    println()
    Processor.main()
}