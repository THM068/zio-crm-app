package com.crm.server.routes

import com.crm.server.renderer.ViewRenderer
import zio.ZLayer
import zio.http.{ Method, Response, RoutePattern, Routes, Status, handler}

class NotFoundRoute {
  val notFoundRoute = RoutePattern.any -> handler {
    val content = examples.html.notFound()
    ViewRenderer.render(content.body,Response.notFound.status)
  }

  val apps = Routes(notFoundRoute)
    .handleError { t: Throwable => Response.text("The error is " + t).status(Status
      .InternalServerError) }

  def handle(throwable: Throwable) = {
    Response.text("The error is " + throwable).status(Status.InternalServerError)
  }
}

object NotFoundRoute {
  val layer: ZLayer[Any, Nothing, NotFoundRoute] = ZLayer.succeed(apply)

  def apply = new NotFoundRoute()

}
