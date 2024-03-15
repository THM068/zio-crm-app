package com.crm.server

import com.crm.server.routes.{AssetRoutes, ExampleRoutes, HomeRoute, JokeWsRoute, NotFoundRoute}
import zio._
import zio.http.{Middleware, Path, Routes, Server}

case class AppServer(homeRoute: HomeRoute, exampleRoutes: ExampleRoutes,
                     assetRoutes: AssetRoutes, jokeWsRoute: JokeWsRoute, notFound: NotFoundRoute) {

  val serveResourcesApp = Routes.empty.toHttpApp @@  Middleware.serveResources(Path.empty /
    "resources")
  //not found should be the last one in this apps concatenation
  val apps = homeRoute.apps ++ assetRoutes.apps ++ exampleRoutes.apps ++ jokeWsRoute.apps ++ serveResourcesApp ++ notFound.apps
  val port = 9999

  def runServer(): ZIO[Any, Throwable, Unit] = for {
    _ <- ZIO.debug(s"Starting server on http://localhost:${port}")
    _ <- Server.serve(apps)
      .provide(Server.defaultWithPort(port))
  } yield ()
}

object AppServer {
  val layer: ZLayer[HomeRoute with AssetRoutes
    with ExampleRoutes with JokeWsRoute with NotFoundRoute, Nothing, AppServer] =
    ZLayer.fromFunction(AppServer.apply _)
}
