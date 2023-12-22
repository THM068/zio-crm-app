package com.crm.server.routes

import zio.ZLayer
import zio.http._

import java.io.File

class AssetRoutes {
  private val cssList = List("/custom.css", "bootstrap.min.css")

  val apps: HttpApp[Any] = Routes.fromIterable(cssRoutes())
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


}

object AssetRoutes {
  val layer: ZLayer[Any, Nothing, AssetRoutes] = ZLayer.succeed(apply)

  def apply = new AssetRoutes()

}

