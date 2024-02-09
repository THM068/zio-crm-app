package com.crm.middleware

import com.crm.server.renderer.ViewRenderer
import zio.Exit
import zio.http.{Handler, HandlerAspect, Headers, Request, Response, Status}

object CustomMiddleware {

  def customAuth(
                  verify: Request => Boolean,
                  responseHeaders: Headers = Headers.empty,
                  responseStatus: Status = Status.Unauthorized,
                ): HandlerAspect[Any, Unit] =
    HandlerAspect.interceptIncomingHandler[Any, Unit] {
      Handler.fromFunctionExit[Request] { request =>
        if (verify(request)) Exit.succeed(request -> ())
        else Exit.fail(
          ViewRenderer.render(html.login().body, responseStatus).addHeaders(responseHeaders)
        )
      }
    }
}
