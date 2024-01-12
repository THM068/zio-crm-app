package com.crm.server.renderer

import zio.http._

object ViewRenderer {

  def render(content: String, status: Status = Response.ok.status ): Response = Response(
    status,
    Headers(Header.ContentType(MediaType.text.html).untyped),
    Body.fromString(content),
  )
}
