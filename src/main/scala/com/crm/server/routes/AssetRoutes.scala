package com.crm.server.routes

import zio.ZLayer
import zio.http._

import java.io.File

class AssetRoutes {
  private val cssList = List("/custom.css", "bootstrap.min.css")
  private val jsList = List("/htmx.min.js", "alpine.min.js", "tailwind.js")

  val apps: HttpApp[Any] = Routes.fromIterable(cssRoutes() ++ jsRoutes())
    .handleError(handle)
    .sandbox
    .toHttpApp

  def handle(throwable: Throwable) = {
    Response.text("The error is " + throwable).status(Status.InternalServerError)
  }

  private def cssRoutes() = cssList.map { filename =>
    Method.GET / "assets" / filename ->  Handler.fromFile(new File
    (s"src/main/resources/web/assets/css/${filename}"))
  }

  private def jsRoutes() = jsList.map { filename =>
    Method.GET / "assets" / filename ->  Handler.fromFile(new File
    (s"src/main/resources/web/assets/js/${filename}"))
  }


}

object AssetRoutes {
  val layer: ZLayer[Any, Nothing, AssetRoutes] = ZLayer.succeed(apply)

  def apply = new AssetRoutes()

}

