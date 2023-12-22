package com.crm.server.routes

import com.crm.server.renderer.ViewRenderer
import zio.ZLayer
import zio.http._

class HomeRoute() {

  val index = Method.GET / "" -> handler {
    val content = html.IndexPage.render(List("Apple", "Oranges", "Mangoes"), "Home")
    ViewRenderer.render(content.body)
  }

  val about = Method.GET / "about" -> handler {
    val content = html.about.render("Home")
    ViewRenderer.render(content.body)
  }


  val apps: HttpApp[Any] = Routes(index, about)
    .handleError(handle)
    .sandbox
    .toHttpApp

    def handle(throwable: Throwable) = {
      Response.text("The error is " + throwable).status(Status.InternalServerError)
    }

}

object HomeRoute {
  val layer: ZLayer[Any, Nothing, HomeRoute] = ZLayer.succeed(apply)

  def apply = new HomeRoute()

}

