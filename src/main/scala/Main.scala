import com.crm.server.AppServer
import com.crm.server.routes.{AssetRoutes, ExampleRoutes, HomeRoute}
import zio._
import zio.Console.printLine

object Main extends ZIOAppDefault {
  override val run: Task[Unit] = for {
    _ <- ZIO.serviceWithZIO[AppServer](_.runServer())
      .provide(
        AppServer.layer,
        HomeRoute.layer,
        AssetRoutes.layer,
        ExampleRoutes.layer
      )
  } yield ()
}