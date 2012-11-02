package com.katlex.ccs

import grizzled.slf4j.Logging

object JsCompiler extends Logging {
  import com.google.javascript.jscomp._

  lazy val options = {
    def make(list:Any*) =
      (new CompilerOptions /: list) {
        case (ops, conf) =>
          conf match {
            case cl:CompilationLevel => cl.setOptionsForCompilationLevel(ops)
            case wl:WarningLevel => wl.setOptionsForWarningLevel(ops)
            case other => logger.warn("Incorrect compiler options setup provided: " + other)
          }
          ops
      }

    make(
      CompilationLevel.SIMPLE_OPTIMIZATIONS,
      WarningLevel.QUIET
    )
  }

  def compiler = new Compiler

  def compile(code:String, fileName:String) = {
    import SourceFile._

    implicit def toSource(nameContent: (String, String)) = nameContent match {
      case (name, content) =>
        (new Builder).buildFromGenerator(name, new Generator {
          def getCode = content
        })
    }
    val c = compiler
    c.compile("empty.js" -> "", fileName -> code, options)
    c.toSource
  }
}