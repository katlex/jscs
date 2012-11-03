package com.katlex.ccs

import grizzled.slf4j.Logging
import java.util.UUID
import java.io.File
import com.google.javascript.jscomp.SourceFile.{Generator, Builder}

object JsCompiler extends Logging {
  import com.google.javascript.jscomp._

  lazy val options = {
    type OptionModifier = CompilerOptions => Unit
    implicit def compilationLevelAsModifier(cl:CompilationLevel):OptionModifier = cl.setOptionsForCompilationLevel(_)
    implicit def warningLevelAsModifier(wl:WarningLevel):OptionModifier = wl.setOptionsForWarningLevel(_)

    def make(list:OptionModifier*) =
      (new CompilerOptions /: list) {
        case (ops, modifier) =>
          modifier(ops)
          ops
      }

    make(
      CompilationLevel.SIMPLE_OPTIMIZATIONS,
      WarningLevel.QUIET
    )
  }

  def compile(code:String) = {
    implicit def toSource(nameContent: (Option[String], String)) = nameContent match {
      case (name, content) =>
        val realName = name.getOrElse(uuid)
        builder.buildFromCode(realName, content)
    }
    implicit def contentToSource(content:String) = toSource(None -> content)

    val c = compiler
    c.compile("", code, options)
    c.toSource
  }

  def compile(config:Config) = {
    import scala.collection.JavaConversions._
    val sourceFiles = config.files.map { file =>
      file.withInputStream { is =>
        builder.buildFromInputStream(file.fileName, is)
      }
    } .toList
    val c = compiler
    c.compile(List.empty[SourceFile], sourceFiles, options)
    c.toSource
  }

  private def uuid = UUID.randomUUID().toString
  private def builder = new Builder
  private def compiler = new Compiler

}