package com.keithblaha

import flat._
import flat.utils.HttpClient
import monix.eval.Task
import scala.util.{Failure, Success}

object Main extends App with FlatApp {
  def forceHttpsBlankSub(request: HttpRequest, handler: (HttpRequest) => Task[HttpResponse]): Task[HttpResponse] = {
    val protocolOpt = request.headers.filter(_._1 == "X-Forwarded-Proto").headOption.map(_._2)
    val hostOpt = request.headers.filter(_._1 == "Host").headOption.map(_._2)

    (protocolOpt, hostOpt) match {
      case (Some("https"), Some("keithblaha.com")) =>
        handler(request)
      case (_, Some(host)) if host.startsWith("localhost") =>
        handler(request)
      case _ =>
        Found(s"https://keithblaha.com${request.uri}")
    }
  }

  app.post("/gizoogle/textilizer") { request =>
    forceHttpsBlankSub(request, request => {
      HttpClient.post("http://gizoogle.net/textilizer.php", List("translatetext" -> request.bodyOpt.get)).map {
        case Success(response) =>
          OK(Html(response.bodyOpt.get))
        case Failure(e) =>
          InternalServerError(e.getMessage)
      }
    })
  }

  app.get("/") { request =>
    forceHttpsBlankSub(request, request => Task.now {
      OK("hello")
    })
  }

  app.start
}

