package com.katlex.ccs

import grizzled.slf4j.Logging
import java.util.UUID

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
      CompilationLevel.WHITESPACE_ONLY,
      WarningLevel.QUIET
    )
  }

  def compiler = new Compiler

  def compile(code:String) = {
    import SourceFile._

    implicit def toSource(nameContent: (Option[String], String)) = nameContent match {
      case (name, content) =>
        val realName = name.getOrElse(UUID.randomUUID().toString)
        (new Builder).buildFromGenerator(realName, new Generator {
          def getCode = content
        })
    }

    implicit def contentToSource(content:String) = toSource(None -> content)

    val c = compiler
    c.compile("", code, options)
    c.toSource
  }
}