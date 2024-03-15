package com.crm.server.routes
import com.crm.server.renderer.ViewRenderer
import com.crm.server.routes.middleware.CustomMiddleware.{cookieBearer}
import zio._
import zio.http._
import html._

class HomeRoute() {

  val index = Method.GET / "" -> handler {
    val content = html.IndexPage(List("Apple", "Oranges", "Mangoes"), "Home")
    ViewRenderer.render(content.body)
  } @@ cookieBearer

  val about = Method.GET / "about" -> handler {
    val content = html.about()
    ViewRenderer.render(content.body)
  }

  val contact = Method.GET / "contact" -> handler {
    val content = html.contact()
    ViewRenderer.render(content.body)
  }



  val apps: HttpApp[Any] = Routes(index, about, contact)
    .handleError { t: Throwable => Response.text("The error is " + t).status(Status
      .InternalServerError) }
    .toHttpApp

    def handle(throwable: Throwable) = {
      Response.text("The error is " + throwable).status(Status.InternalServerError)
    }

}

object HomeRoute {
  val layer: ZLayer[Any, Nothing, HomeRoute] = ZLayer.succeed(apply)

  def apply = new HomeRoute()

}

