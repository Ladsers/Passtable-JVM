import java.util.*

class TextBundle() {
    private var rb = ResourceBundle.getBundle("text", Locale.ROOT)
    fun changeLocale(language: String) {
        val locale: Locale = when (language) {
            "ru" -> Locale("ru", "RU")
            else -> Locale.ROOT
        }
        rb = ResourceBundle.getBundle("text", locale)
    }
    fun key(key: String): String {
        return String(rb.getString(key).toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
    }
}