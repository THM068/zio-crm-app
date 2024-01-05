package com.crm.server.routes

import com.crm.services.JokeService
import zio.{Random, ZIO, ZLayer, durationInt}
import zio.http.ChannelEvent.{Read, Unregistered, UserEvent, UserEventTriggered}
import zio.http.codec.PathCodec.string
import zio.http.{Handler, HttpApp, Method, Request, Response, Routes, WebSocketApp, WebSocketChannel, WebSocketFrame, handler}
import zio.json.EncoderOps

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
            ZIO.log(s"${clientId} has disconnected")
        case _ =>
          ZIO.unit
      }
    }

  val apps: HttpApp[Any] =
    Routes(
      Method.GET / "joke-subscription" / string("id") -> handler { (id: String, _: Request) =>
        socketApp(id).toResponse
      }
    ).toHttpApp

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

  private def notifiyClients(): ZIO[JokeService, Throwable, Any] =
      ZIO.foreachDiscard(clientsMap.map { case (key, value) => (key, value) }) { entry =>
        val channel = entry._2
        for {
          jokeService <- ZIO.service[JokeService]
          joke <- jokeService.getJoke()
          content = examples.snippets.html.dadjoke(joke)
          _ <- channel.send(Read(WebSocketFrame.Text(content.body)))
        } yield ()
      }

  def scheduleNotification = notifiyClients().repeat(
    zio.Schedule.spaced(5.seconds) &&
      zio.Schedule.recurs(10000)
  ).provide(JokeService.layer)
}
