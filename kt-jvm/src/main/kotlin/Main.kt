val tb = TextBundle()
const val version =  "0.9.9"

fun main(args: Array<String>)
{
    printHeader()

    if (System.getProperty("os.name").startsWith("win", true)){
        println()
        println(tb.key("msg_windows"))
    }

    Updater.run()

    when {
        args.size > 1 -> Processor.openProcess(args[0], args[1])
        args.isNotEmpty() -> {
            println()
            Processor.openProcess(args[0])
        }
        else -> Processor.quickStart()
    }
    println()
    Processor.main()
}