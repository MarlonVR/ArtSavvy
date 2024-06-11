import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.*

object TTSManager : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    fun initialize(context: Context) {
        if (tts == null) {
            tts = TextToSpeech(context.applicationContext, this).apply {
                setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        // Narração iniciada
                    }

                    override fun onDone(utteranceId: String?) {
                        // Narração concluída
                        onSpeakingChanged(false)
                    }

                    override fun onError(utteranceId: String?) {
                        // Erro ao narrar
                        onSpeakingChanged(false)
                    }
                })
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("pt", "BR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                isInitialized = false
            } else {
                tts?.setPitch(1.0f)  // Ajuste de tom de voz
                tts?.setSpeechRate(1.0f)  // Ajuste da velocidade da fala
                isInitialized = true
            }
        } else {
            isInitialized = false
        }
    }


    fun speak(text: String) {
        if (isInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UniqueID")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
    }

    var onSpeakingChanged: (Boolean) -> Unit = {}
}
