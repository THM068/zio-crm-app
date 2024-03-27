package com.crm.server

import com.crm.server.routes.{AssetRoutes, ExampleRoutes, HomeRoute, JokeWsRoute, LoginRoute, NotFoundRoute}
import zio._
import zio.http.{Middleware, Path, Routes, Server}

case class AppServer(homeRoute: HomeRoute, exampleRoutes: ExampleRoutes,
                     assetRoutes: AssetRoutes, jokeWsRoute: JokeWsRoute, notFound: NotFoundRoute,
                     loginRoute: LoginRoute) {

  val serveResourcesApp = Routes.empty.toHttpApp @@  Middleware.serveResources(Path.empty /
    "resources")
  //not found should be the last one in this apps concatenation
  val apps = homeRoute.apps ++ assetRoutes.apps ++ exampleRoutes.apps ++ jokeWsRoute.apps ++
    serveResourcesApp ++  loginRoute.apps ++ notFound.apps
  val port = 9977

  def runServer(): ZIO[Any, Throwable, Unit] = for {
    _ <- ZIO.debug(s"Starting server on http://localhost:${port}")
    _ <- Server.serve(apps)
      .provide(Server.defaultWithPort(port))
  } yield ()
}

object AppServer {
  val layer: ZLayer[HomeRoute with AssetRoutes
    with ExampleRoutes with JokeWsRoute with NotFoundRoute with LoginRoute, Nothing, AppServer] =
    ZLayer.fromFunction(AppServer.apply _)
}
