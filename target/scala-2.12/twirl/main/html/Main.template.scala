
package html

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml

object main extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template2[String,Html,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(title: String)(body: Html):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*2.1*/("""
"""),format.raw/*3.1*/("""<html>
  <head>
    <title>"""),_display_(/*5.13*/title),format.raw/*5.18*/("""</title>
    <link href="/assets/bootstrap.min.css" rel="stylesheet" >
    <link href="/assets/custom.css" rel="stylesheet" >
  </head>
  <body>
    <div class="container">
      """),_display_(/*11.8*/body),format.raw/*11.12*/("""
    """),format.raw/*12.5*/("""</div>

  </body>
</html>"""))
      }
    }
  }

  def render(title:String,body:Html): play.twirl.api.HtmlFormat.Appendable = apply(title)(body)

  def f:((String) => (Html) => play.twirl.api.HtmlFormat.Appendable) = (title) => (body) => apply(title)(body)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  SOURCE: src/main/twirl/main.scala.html
                  HASH: 9af0479ecff3107560127b168930c46c6c9995de
                  MATRIX: 564->1|685->29|712->30|766->58|791->63|997->243|1022->247|1054->252
                  LINES: 14->1|19->2|20->3|22->5|22->5|28->11|28->11|29->12
                  -- GENERATED --
              */
          