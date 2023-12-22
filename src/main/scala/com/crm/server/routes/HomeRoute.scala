package com.crm.server.routes

import com.crm.server.renderer.ViewRenderer
import zio.ZLayer
import zio.http.Method.GET
import zio.http._
import zio.http.codec.PathCodec
import zio.http.codec.PathCodec.int
import zio.http.endpoint.Endpoint

import java.io.File

class HomeRoute() {

  val index = Method.GET / "" -> handler {
    val content = html.IndexPage.render(List("Apple", "Oranges", "Mangoes"), "Home")
    ViewRenderer.render(content.body)
  }


  val apps: HttpApp[Any] = Routes(index)
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

