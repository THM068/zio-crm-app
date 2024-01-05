import com.crm.server.AppServer
import com.crm.server.routes.{AssetRoutes, ExampleRoutes, HomeRoute, JokeTickerBroadCaster, JokeWsRoute}
import com.crm.services.JokeService
import zio._
import zio.Console.printLine

object Main extends ZIOAppDefault {
  override val run: Task[Unit] = for {
    _ <- JokeTickerBroadCaster.scheduleNotification.fork
    _ <- ZIO.serviceWithZIO[AppServer](_.runServer())
      .provide(
        AppServer.layer,
        HomeRoute.layer,
        AssetRoutes.layer,
        ExampleRoutes.layer,
        JokeWsRoute.layer
      )
  } yield ()
}