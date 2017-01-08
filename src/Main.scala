package com.keithblaha

import flat._
import flat.utils.HttpClient
import scala.util.{Failure, Success}

object Main extends App with FlatApp {
  app.addPrehandler { request =>
    val protocolOpt = request.headers.filter(_._1 == "X-Forwarded-Proto").headOption.map(_._2)
    val hostOpt = request.headers.filter(_._1 == "Host").headOption.map(_._2)

    (protocolOpt, hostOpt) match {
      case (Some("https"), Some("keithblaha.com")) =>
        None
      case (_, Some(host)) if host.startsWith("localhost") =>
        None
      case _ =>
        Some(Found(s"https://keithblaha.com${request.uri}"))
    }
  }

  app.post("/gizoogle/textilizer") { request =>
    HttpClient.post("http://gizoogle.net/textilizer.php", List("translatetext" -> request.bodyOpt.get)).map {
      case Success(response) =>
        Ok(Html(response.bodyOpt.get))
      case Failure(e) =>
        InternalServerError(e.getMessage)
    }
  }

  app.get("/") { request =>
    Ok("hello")
  }

  app.start
}

