package com.crm.server.routes

import com.crm.server.renderer.ViewRenderer
import zio.ZLayer
import zio.http._

class LoginRoute {

  val login = Method.GET / "login" -> handler {
    val content = html.login()
    ViewRenderer.render(content.body)
  }

  val apps: HttpApp[Any] = Routes(login)
    .handleError { t: Throwable => Response.text("The error is " + t).status(Status
      .InternalServerError) }
    .toHttpApp

  def handle(throwable: Throwable) = {
    Response.text("The error is " + throwable).status(Status.InternalServerError)
  }
}

object LoginRoute {
  val layer: ZLayer[Any, Nothing, LoginRoute] = ZLayer.succeed(apply)

  def apply = new LoginRoute()
}
