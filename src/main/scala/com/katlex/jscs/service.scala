package com.katlex
package jscs

import unfiltered.request._
import unfiltered.response._

import net.liftweb.common.{Full, Failure}
import launch.{EntryPoint, LauncherAdapter}

object Server extends EntryPoint {

  class Launcher extends LauncherAdapter(this)

  private object GET_@ {
    def unapply[T](req:HttpRequest[T]) = req match {
      case GET(Path(Seg(pathList))) => Some(pathList)
      case _ => None
    }
  }

  private object CompilationConfig {
    lazy val JS_EXT = """\.js$""".r
    def unapply[T](req:HttpRequest[T]) = req match {
      case GET_@(fileName :: Nil) => Some(JS_EXT.replaceAllIn(fileName, ""))
      case _ => None
    }
  }

  class CompileConfigApp extends unfiltered.filter.Plan {
    def intent = {
      case CompilationConfig(configName) =>
        logger.debug("Config '%s' was requested" format configName)

        def error(msg:String) = {
          logger.error(msg)
          InternalServerError ~> JsContent ~> ResponseString("""{error:"%s"}""" format msg)
        }

        Config.parse(configName) match {
          case Full(config) =>
            logger.debug("Compiling files: \n" + config.files.map(_.fileName).mkString("\n"))
            Ok ~> JsContent ~> ResponseString(JsCompiler.compile(config))
          case Failure(msg, _, _) => error(msg)
          case _ => error("Unknown error")
        }
    }
  }

  def run(args: Array[String]) = {
    val http = unfiltered.jetty.Http.local(7770)
    http.filter(new CompileConfigApp).run({ svr =>
        //unfiltered.util.Browser.open(http.url + "test.js")
      }, { svr =>
        logger.info("shutting down server")
      })
    0
  }
}
