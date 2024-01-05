package com.crm.services

import com.crm.model.{AppError, Joke}
import zio._
import zio.http._
import zio.json.DecoderOps

import java.io

trait JokeService {

  def getJoke():  ZIO[Any, AppError, Joke]
}
class JokeServiceImpl extends JokeService {
  val jokeUrl = "https://icanhazdadjoke.com/"
  override def getJoke(): ZIO[Any, AppError, Joke] = (for {
    url <- ZIO.fromOption(URL.decode(jokeUrl).toOption)
    client <- ZIO.service[Client]
    res <- client.addHeader("Accept", "application/json").url(url).get("/")
    data <- res.body.asString
    joke <- ZIO.from(data.fromJson[Joke])
  } yield  joke).provide(Client.default, Scope.default)
    .mapError( e => AppError
    .JokeRequestFailureException("Error has occurred calling Joke API"))
}

object JokeService {
  val layer: ZLayer[Any, AppError, JokeService] = ZLayer.succeed(new JokeServiceImpl())
}
