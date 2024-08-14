package com.crm.middleware

import zio.test.ZIOSpecDefault
import zio._
import zio.http._
import zio.test.{test, _}
object HtmxMiddlewareSpec extends ZIOSpecDefault{

  def spec =
    test("hello world route and fallback") {
      for {
        client           <- ZIO.service[Client]
        _                <- TestClient.addRoutes {
          Routes(
            Method.GET / trailing          -> handler { Response.text("fallback") },
            Method.GET / "hello" / "world" -> handler { Response.text("Hey there!") },
          )
        }
        helloResponse    <- client.batched(Request.get(URL.root / "hello" / "world"))
        helloBody        <- helloResponse.body.asString
        fallbackResponse <- client.batched(Request.get(URL.root / "any"))
        fallbackBody     <- fallbackResponse.body.asString
      } yield assertTrue(helloBody == "Hey there!", fallbackBody == "fallback")
    }.provide(TestClient.layer)
}
