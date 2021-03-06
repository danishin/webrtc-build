package action.webrtc

import java.io.{File, FileReader, Reader}
import java.nio.file.{Files, Paths}

import util.Program.AppError
import util.{Helper, Program}

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

private case class RTCType(enum: String, values: List[String])
private object parseRTCTypes extends JavaTokenParsers {
  import scala.language.postfixOps

  // http://stackoverflow.com/questions/5952720/ignoring-c-style-comments-in-a-scala-combinator-parser
  // treat comments as whitespace - forward slashes don't need to escaped in java
  override protected val whiteSpace: Regex = """(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r

  private def rtcTypes: Parser[RTCType] =
    "typedef" ~ "enum" ~ "{" ~> repsep(ident | "}", ",") ~ ident <~ ";" ^^ {
      case values ~ enum => RTCType(enum, values.dropRight(1))
    }

  private def expr: Parser[List[RTCType]] = rep(rtcTypes)

  def apply(file: File): Program[List[RTCType]] = parse(expr, new FileReader(file)) match {
    case Success(l, rest) => Program.just(l)
    case failure: NoSuccess => Program.error(AppError.just(s"Failed to parse RTCType.h: ${failure.msg}"))
  }
}

object Assemble extends Helper {
  def run(archs: List[Platform#Architecture]): Program[Unit] = archs.head.platform match {
    case Platform.IOS => for {
      _ <- Program.guard(archs.forall(a => file.exists(a.archive_file_path)))("Archive files don't exist. Please run build before running assemble")

      _ <- echo("Create 'WebRTCiOS.framework'")
      _ <- shell("rm", "-rf", root.output.`WebRTCiOS.framework`)
      _ <- shell("mkdir", "-p", root.output.`WebRTCiOS.framework`.Versions.A.Headers)

      _ <- shell("lipo", "-output", root.output.`WebRTCiOS.framework`.Versions.A("WebRTCiOS"), "-create", archs.map(_.archive_file_path).mkString(" "))

      /**
        * - Exclude `RTCNS*.h` since these are OSX-specific
        * - Exclude `.*\+Private.h` since these are private APIs.
        */
      webrtcHeaderFiles = new File(root.lib.src("talk/app/webrtc/objc/public")).listFiles.filter(_.getName.matches("""RTC(?!NS).*(?<!\+Private)\.h""")).toList

      _ <- shell("cp", webrtcHeaderFiles.map(_.getAbsolutePath).mkString(" "), root.output.`WebRTCiOS.framework`.Versions.A.Headers)

      /*
      * Convert from original RTCTypes.h with `typedef enum` to `typedef NS_ENUM`, which is compatible with swift.
      * */
      rtcTypes <- parseRTCTypes(webrtcHeaderFiles.find(_.getName == "RTCTypes.h").get)
      _ <- file.write(root.output.`WebRTCiOS.framework`.Versions.A.Headers("RTCTypes.h"), List(
        "// DO NOT EDIT THIS FILE. This is generated file based on original RTCTypes.h. All changes will be lost.",
        "",
        "#import <Foundation/Foundation.h>",
        "",
        rtcTypes.map(t => s"typedef NS_ENUM(NSInteger, ${t.enum}) {\n${t.values.mkString("  ", ",\n  ", ",")}\n};").mkString("\n\n")
      ).mkString("\n"))
      _ <- file.write(root.output.`WebRTCiOS.framework`.Versions.A.Headers("WebRTCiOS.h"), List(
        "#import <UIKit/UIKit.h>",
        "",
        webrtcHeaderFiles.map(f => s"""#import "${f.getName}"""").mkString("\n"),
        "",
        "FOUNDATION_EXPORT double libjingle_peerconnectionVersionNumber;",
        "FOUNDATION_EXPORT const unsigned char libjingle_peerconnectionVersionString[];"
      ).mkString("\n"))

      _ <- shell("ln", "-sfh", root.output.`WebRTCiOS.framework`.Versions.A, root.output.`WebRTCiOS.framework`.Versions("Current"))
      _ <- shell("ln", "-sfh", root.output.`WebRTCiOS.framework`.Versions("Current/Headers"), root.output.`WebRTCiOS.framework`("Headers"))
      _ <- shell("ln", "-sfh", root.output.`WebRTCiOS.framework`.Versions("Current/WebRTCiOS"), root.output.`WebRTCiOS.framework`("WebRTCiOS"))
    } yield ()

    case Platform.Android =>
      ???
  }
}
