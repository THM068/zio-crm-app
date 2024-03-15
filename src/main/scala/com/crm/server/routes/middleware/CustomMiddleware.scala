package com.crm.server.routes.middleware

import com.crm.server.renderer.ViewRenderer
import zio.Exit
import zio.http.{Handler, HandlerAspect, Headers, Middleware, Request, Response, Status}

import scala.Console;

object CustomMiddleware {

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

//def customAuth(
//                verify: Request => Boolean,
//                responseHeaders: Headers = Headers.empty,
//                responseStatus: Status = Status.Unauthorized,
//              ): HandlerAspect[Any, Unit] =
//  HandlerAspect.interceptIncomingHandler[Any, Unit] {
//    Handler.fromFunctionExit[Request] { request =>
//      if (verify(request)) Exit.succeed(request -> ())
//      else Exit.fail(Response.status(responseStatus).addHeaders(responseHeaders))
//    }
//  }
