package com.katlex.ccs

import unfiltered.request._
import unfiltered.response._

import grizzled.slf4j.Logging

object Server extends Logging {

  private object GET_@ {
    def unapply[T](req:HttpRequest[T]) = req match {
      case GET(Path(Seg(pathList))) => Some(pathList)
      case _ => None
    }
  }

  private object CompiledConfig {
    lazy val JS_EXT = """\.js$""".r
    def unapply[T](req:HttpRequest[T]) = req match {
      case GET_@(fileName :: Nil) => Some(JS_EXT.replaceFirstIn(fileName, ""))
      case _ => None
    }
  }

  private class CompileByConfigApp extends unfiltered.filter.Plan {
    def intent = {
      case CompiledConfig(configName) =>
        logger.debug("Config '%s' was requested" format configName)
        Ok ~> JsContent ~> ResponseString(
          JsCompiler.compile("""
                               |if (console && console.log) {
                               | console.log("hello world");
                               |}
                             """.stripMargin)
        )
    }
  }

  def main(args: Array[String]) {
    val http = unfiltered.jetty.Http.local(7770)
    http.filter(new CompileByConfigApp).run({ svr =>
        //unfiltered.util.Browser.open(http.url + "test.js")
      }, { svr =>
        logger.info("shutting down server")
      })
  }
}
