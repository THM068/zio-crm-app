package com.crm.server

import com.crm.server.routes.{AssetRoutes, ExampleRoutes, HomeRoute}
import zio._
import zio.http.Server

case class AppServer(homeRoute: HomeRoute, exampleRoutes: ExampleRoutes, assetRoutes: AssetRoutes) {

  val apps = homeRoute.apps ++ assetRoutes.apps ++ exampleRoutes.apps
  val port = 9999

  def runServer(): ZIO[Any, Throwable, Unit] = for {
    _ <- ZIO.debug(s"Starting server on http://localhost:${port}")
    _ <- Server.serve(apps)
      .provide(Server.defaultWithPort(port))
  } yield ()
}

object AppServer {
  val layer: ZLayer[HomeRoute with AssetRoutes with ExampleRoutes, Nothing, AppServer] =
    ZLayer.fromFunction(AppServer.apply _)
}
