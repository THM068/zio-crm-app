package com.crm.server.routes

import com.crm.services.JokeService
import zio._
import zio.http.ChannelEvent.{Read, Unregistered, UserEvent, UserEventTriggered}
import zio.http.codec.PathCodec.string
import zio.http.{Handler, Method, Request, Routes, WebSocketApp, WebSocketChannel, WebSocketFrame, handler}

class JokeWsRoute {

  private val socketApp: (String) => WebSocketApp[Any] = (clientId: String) =>
    Handler.webSocket { channel =>
      channel.receiveAll {
        case Read(WebSocketFrame.Text("end")) =>
          JokeTickerBroadCaster.unRegister(clientId)
          channel.shutdown *>
            ZIO.log(s"${clientId} has disconnected")
        case Read(WebSocketFrame.Text(text)) =>
          channel.send(Read(WebSocketFrame.Text(text.toUpperCase())))
        case UserEventTriggered(UserEvent.HandshakeComplete) =>
          JokeTickerBroadCaster.register(clientId, channel)
          channel.send(Read(WebSocketFrame.text("Greetings from Dad Jokes!")))
        case Unregistered =>
          ZIO.succeed(JokeTickerBroadCaster.unRegister(clientId)) *>
            ZIO.log(s"${clientId} has disconnected >>>>>")
        case _ =>
          ZIO.unit
      }
    }

  val apps =
    Routes(
      Method.GET / "joke-subscription" / string("id") -> handler { (id: String, _: Request) =>
        socketApp(id).toResponse
      }
    )

}

object JokeWsRoute {
  val layer: ZLayer[Any, Nothing, JokeWsRoute] =
    ZLayer.succeed(new JokeWsRoute())
}

object JokeTickerBroadCaster {

  import scala.collection.mutable.Map

  val clientsMap: Map[String, WebSocketChannel] = Map[String, WebSocketChannel]()


  def register(clientId: String, channel: WebSocketChannel) =
    clientsMap += (clientId -> channel)

  def unRegister(clientId: String) = clientsMap.remove(clientId)

  def getJoke = (for {
    jokeService <- ZIO.service[JokeService]
    joke <- jokeService.getJoke()
  } yield joke)

  private def notifiyClients(): ZIO[JokeService, Throwable, Any] =
    getJoke.flatMap { joke =>
      val content = examples.snippets.html.dadjoke(joke).body
      ZIO.foreachDiscard(clientsMap.map { case (key, value) => (key, value) }) { entry =>
        val channel = entry._2
        for {
          _ <- channel.send(Read(WebSocketFrame.Text(content)))
        } yield ()
      }
    }

  def scheduleNotification = notifiyClients().repeat(
    zio.Schedule.spaced(5.seconds) &&
      zio.Schedule.recurs(10000)
  ).provide(JokeService.layer)
}
