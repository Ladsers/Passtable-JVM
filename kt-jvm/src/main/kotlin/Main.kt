val tb = TextBundle()
const val version =  "1.0.0-beta1"

fun main(args: Array<String>)
{
    printHeader()
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