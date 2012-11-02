package com.katlex.ccs

import grizzled.slf4j.Logging
import io.Source
import java.io.File

object Config extends Logging {
  case class FileOps(f:File) {
    def /(dir:String) = new File(f.getAbsolutePath + File.separator + dir)
  }
  implicit def toFileOps(f:File):FileOps = FileOps(f)

  lazy val home = sys.props.get("user.home").map(new File(_))
  lazy val configDir =
    home.flatMap { home =>
      (home / ".closure-compiler-server") match {
        case f:File if f.exists && f.isDirectory => Some(f)
        case _ => None
      }
    }

  def parse(name:String):Option[Config] =
    configDir flatMap { conf =>
      (conf / name) match {
        case f:File if f.isFile && f.canRead =>
          val lines = Source.fromFile(f).getLines()
          logger.debug("Parsing lines: \n" + lines.mkString("\n"))
          Some(Config(name, List.empty[File]))
        case _ => None
      }
    }

}

case class Config(name:String, files:List[File])