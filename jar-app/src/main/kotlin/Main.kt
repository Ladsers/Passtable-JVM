val tb = TextBundle()
val version =  "0.9.1.JVM"

fun main(args: Array<String>)
{
    printHeader()
    //checkUpdates("jvm")

    when {
        args.size > 1 -> Processor.openProcess(args[0], args[1])
        args.isNotEmpty() -> {
            println()
            Processor.openProcess(args[0])
        }
        else -> Processor.quickStart()
    }
    Processor.main()
}