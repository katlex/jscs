package com.katlex.ccs

import unfiltered.request._
import unfiltered.response._

import grizzled.slf4j.Logging

object Server extends Logging {

  class CompileByConfigApp extends unfiltered.filter.Plan {
    def intent = {
      case GET(Path(Seg("js" :: fileName :: Nil))) =>
        logger.debug("JsScript %s requested" format """\.js$""".r.replaceFirstIn(fileName, ""))
        Ok ~> JsContent ~> ResponseString(
          JsCompiler.compile("""
                               |if (console && console.log) {
                               | console.log("hello world");
                               |}
                             """.stripMargin, fileName)
        )
    }
  }

  def main(args: Array[String]) {
    val http = unfiltered.jetty.Http.local(7770)
    http.filter(new CompileByConfigApp).run({ svr =>
        //unfiltered.util.Browser.open(http.url + "js/test.js")
      }, { svr =>
        logger.info("shutting down server")
      })
  }
}
