package com.crm.server

import com.crm.server.routes.{AssetRoutes, HomeRoute}
import zio._
import zio.http.Server

case class AppServer(homeRoute: HomeRoute, assetRoutes: AssetRoutes) {

  val apps = homeRoute.apps ++ assetRoutes.apps
  val port = 9999

  def runServer(): ZIO[Any, Throwable, Unit] = for {
    _ <- ZIO.debug(s"Starting server on http://localhost:${port}")
    _ <- Server.serve(apps)
      .provide(Server.defaultWithPort(port))
  } yield ()
}

object AppServer {
  val layer: ZLayer[HomeRoute with AssetRoutes, Nothing, AppServer] =
    ZLayer.fromFunction(AppServer.apply _)
}
