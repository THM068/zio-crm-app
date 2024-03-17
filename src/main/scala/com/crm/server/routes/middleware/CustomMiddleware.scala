package com.crm.server.routes.middleware

import com.crm.server.renderer.ViewRenderer
import zio.Exit
import zio.http.Header.HeaderType
import zio.http.{Handler, HandlerAspect, Header, Headers, Middleware, Request, Response, Status}

import scala.Console;

object CustomMiddleware {

  def hxRequest(): HandlerAspect[Any, Unit] =
    Middleware.interceptIncomingHandler {
      Handler.fromFunctionExit[Request] { request =>
       val hxRequestHeader = Header.Custom("HX-Request", "true")
        request.header(hxRequestHeader.headerType) match {
          case Some(headerValue) =>
            Console.println(s"Headervalue: ${headerValue.value}")
            Exit.succeed(request -> ())
          case None =>
            Exit.fail(Response.text("Content cannot be viewed via this method"))
        }
      }
    }
  def cookieBearer(): HandlerAspect[Any, Unit] =
    Middleware.interceptIncomingHandler {
      Handler.fromFunctionExit[Request] { request =>
        request.cookie("token") match {
          case Some(token) =>
            Console.println(s"Token: ${token}")
            Exit.succeed(request -> ())
          case None =>
            val content = html.login()
            Exit.fail(ViewRenderer.render(content.body))
        }

      }
    }
}
