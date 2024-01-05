package com.crm.model
import zio._
import zio.http._
import zio.json.{DecoderOps, DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

sealed trait AppError extends Exception

object AppError {
  case class JokeRequestFailureException(message: String) extends AppError
}

case class Joke(id: String, joke: String, status: Int)

object Joke {
  implicit val decoder: JsonDecoder[Joke] = DeriveJsonDecoder.gen[Joke]

  implicit val encoder: JsonEncoder[Joke] = DeriveJsonEncoder.gen[Joke]
}
object SimpleClient extends ZIOAppDefault {
  //val url = URL.decode("https://icanhazdadjoke.com/").toOption.get


  val program = (for {
    url  <- ZIO.fromOption(URL.decode("https://icanhazdadjoke.com/").toOption)
    client <- ZIO.service[Client]
    res    <- client.addHeader("Accept","application/json").url(url).get("/")
    data   <- res.body.asString
    joke  <- ZIO.from(data.fromJson[Joke])
    _      <- Console.printLine(joke.joke)
  } yield ())
    .catchSome {
      case e: java.net.UnknownHostException =>
        Console.printLine("Unkown host")
    }

  override val run = program.provide(Client.default, Scope.default)

}
