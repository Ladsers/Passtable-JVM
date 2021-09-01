val tb = TextBundle()
val osWindows = System.getProperty("os.name").startsWith("win", true)
const val version =  "1.0.0-beta2"

fun main(args: Array<String>)
{
    val argList = args.toMutableList()
    if (argList.isNotEmpty() && !osWindows) {
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