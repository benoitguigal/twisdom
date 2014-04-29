package models

import com.cybozu.labs.langdetect.DetectorFactory
import com.cybozu.labs.langdetect.Detector
import java.util.jar.JarFile
import java.net.JarURLConnection
import scala.util.Try


object LanguageDetector {
  final val UNKNOWN_LANG = "unknown"
}

import LanguageDetector._

class LanguageDetector {

  DetectorFactory.loadProfile("conf/profiles.sm")

  def apply(text: String): String = {
    val detector = DetectorFactory.create()
    detector.append(text)
    Try(detector.detect()).getOrElse(UNKNOWN_LANG)
  }

}
