package util

import java.io.File

import play.api.libs.json.{JsLookupResult, JsValue, Json, Reads}
import util.Program.{AppError, Env}

import scalaz._

trait Helper extends ProgramFunctions with ProgramOps {
  import sys.process.Process
  import java.nio.charset.StandardCharsets
  import java.nio.file.{Paths, Files}

  abstract class PathRep(val path: String) {
    def apply(p: String): String = s"$path/$p"
  }
  object PathRep {
    import scala.language.implicitConversions
    implicit def pathRep2String(pathRep: PathRep): String = pathRep.path
  }

  object Color extends Enumeration {
    val Cyan = Value(Console.CYAN)
    val Blue = Value(Console.BLUE)
  }
  private def log(message: String, color: Color.Value): Unit = println(s"${color.toString}$message${Console.RESET}")

  def echo(m: String): Program[Unit] = Program(log(m, Color.Blue))
  def debug(m: String, vs: Any*): Unit = log(
    s"""
      |$m
      |${vs.mkString("------------------------\n- ", "\n- ", "\n------------------------")}
    """.stripMargin, Color.Cyan)

  def shell(commandArgs: String*): Program[Unit] = {
    // NB: `/bin/sh -c` is needed to support shell features like globbing. And it accepts one string so we `mkString`. And we wrap every argument with quote to avoid parameter expansion wrt whitespace.
    val command = Seq("/bin/sh", "-c") :+ commandArgs/*.map(a => s"'$a'")*/.mkString(" ")

    for {
      _ <- Program((env: Env) => log(s"""
          |Executing...
          |-------------------------
          |- Command: ${command.mkString(" ")}
          |- CWD: ${env.cwd}
          |- Env Vars: ${env.envVars.mkString("\n", "\n", "")}
          |-------------------------
        """.stripMargin, Color.Cyan))

      _ <- Program((env: Env) =>
        Process(command, new File(env.cwd), env.envVars.toList: _*).! match {
          case 0 => \/-(())
          case exit_code => throw AppError.just(s"Command '${command.mkString(" ")}' exited with exit code $exit_code")
        }
      )
    } yield ()
  }

  def write(path: String, content: String): Program[Unit] = Program(Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8))).map(_ => ())

  def parseConfigJson: Program[JsValue] = Program(Json.parse(Files.readAllBytes(Paths.get("config.json"))))

}

object Helper extends Helper