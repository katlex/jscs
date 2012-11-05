package com.katlex.jscs

import grizzled.slf4j.Logging
import io.Source
import java.io.{FileInputStream, InputStream, File}
import net.liftweb.common.{Failure, Full, Box}
import Box._
import java.util.regex.Matcher
import com.katlex.jscs.Config.OnDiskSourceFile
import util.matching.Regex

object Config extends Logging with ConfigTransformers {
  abstract class SourceFile(val fileName:String) {
    protected def inputStream:InputStream
    def withInputStream[T](body: InputStream => T) = {
      val is = inputStream
      try {
        body(is)
      } finally {
        is.close()
      }
    }
  }
  class OnDiskSourceFile(file:File) extends SourceFile(file.getAbsolutePath) {
    protected def inputStream = new FileInputStream(file)
  }

  lazy val configDir =
    Box(home).flatMap { home =>
      (home / ".closure-compiler-server") match {
        case Directory(dir) => Full(dir)
        case f:File => Failure("Config dir should exist and be a directory: " + f.getAbsolutePath)
      }
    } ?~ "Home dir not found"

  def parse(configName:String):Box[Config] =
    configDir flatMap { configDir =>
      (configDir / configName) match {
        case ReadableFile(f) =>
          val configBox:Box[Config] = Full(Config(configName, Set(), Map()))
          val lines = Source.fromFile(f).getLines.map(_.trim).zipWithIndex.
                        filterNot(_._1 == "").
                        filterNot(_._1.startsWith("#"))
          (configBox /: lines) {
            case (Full(conf), (ConfigTransformer(transform), _)) =>
              transform(conf)
            case (Full(_), (line, lineNum)) =>
              Failure(
                "Config file '%s' contains an error at line %d: %s"
                  format (f.getAbsolutePath, lineNum, line)
              )
            case (failure, _) => failure
          }
        case f:File => Failure("Config file '%s' can not be read" format f.getAbsolutePath)
      }
    }
}

case class Config(name:String, files:Set[Config.SourceFile], variables:Map[String, String]) {
  def expandVars(str:String) =
    (str /: variables) {
      case (str, (varName, value)) =>
        str.replaceAll("\\$%s" format varName, Matcher.quoteReplacement(value))
    }
}

trait ConfigTransformers extends FileUtil {
  object ConfigTransformer {
    def unapply(cmd:String):Option[ConfigTransformer] =
      VarDeclaration.unapply(cmd) or
      SrcRootDirective.unapply(cmd) or
      ExcludeDirective.unapply(cmd)
  }
  trait ConfigTransformer {
    def apply(conf:Config):Box[Config]
  }

  /**
   * Variable declaration e.g. BASE=/home/user/sources
   */
  object VarDeclaration {
    val R = """([^=]+)=([^=]+)""".r
    def unapply(cmd:String) = cmd match {
      case R(varName, value) =>
        Some(new VarDeclaration(varName.trim, value.trim))
      case _ => None
    }
  }
  class VarDeclaration(name:String, value:String) extends ConfigTransformer {
    def apply(conf: Config) = Full(conf.copy(variables = conf.variables + (name -> value)))
  }

  /**
   * Source root declaration e.g. srcRoot $BASE
   */
  object SrcRootDirective {
    val R = """srcRoot (.*)""".r
    def unapply(cmd:String) = cmd match {
      case R(root) => Some(new SrcRootDirective(root))
      case _ => None
    }
  }
  class SrcRootDirective(root:String) extends ConfigTransformer {
    def apply(conf: Config) =
      new File(conf.expandVars(root)) match {
        case Directory(dir) =>
          val files = dir ** "\\.js$".r
          Full(conf.copy(files = conf.files ++ files.map(f => new OnDiskSourceFile(f))))
        case f => Failure("Bad source root '%s'" format f.getAbsolutePath)
      }
  }

  /**
   * Exclude directive to exclude some paths from the collected sources
   */
  object ExcludeDirective {
    val R = "exclude (.*)".r
    def unapply(cmd:String) = cmd match {
      case R(filterString) => Some(new ExcludeDirective(filterString.r))
      case _ => None
    }
  }
  class ExcludeDirective(filter:Regex) extends ConfigTransformer {
    def apply(conf: Config) = Full(conf.copy(files = conf.files.filterNot {
      sourceFile => filter.findFirstIn(sourceFile.fileName).isDefined
    }))
  }
}