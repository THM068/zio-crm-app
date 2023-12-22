
package html

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
/*1.2*/import html.main

object IndexPage extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template2[List[String],String,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*2.2*/(items: List[String], name: String = "Home"):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {

def /*3.2*/sub_template/*3.14*/ = {{}};
Seq[Any](format.raw/*3.20*/("""
"""),_display_(/*4.2*/main(name)/*4.12*/ {_display_(Seq[Any](format.raw/*4.14*/("""
  """),format.raw/*5.3*/("""<h1>Welcome to My Web Page</h1>
  <p>This is a paragraph of text in my web page. It can contain various details about me or the subject of the page.</p>
  <ul>
  """),_display_(/*8.4*/for(item <- items) yield /*8.22*/ {_display_(Seq[Any](format.raw/*8.24*/("""
    """),format.raw/*9.5*/("""<li>"""),_display_(/*9.10*/item),format.raw/*9.14*/("""</li>
  """)))}),format.raw/*10.4*/("""

  """),format.raw/*12.3*/("""</ul>
  <a href="https://example.com">Click here to go to example.com</a>
""")))}),format.raw/*14.2*/("""




"""))
      }
    }
  }

  def render(items:List[String],name:String): play.twirl.api.HtmlFormat.Appendable = apply(items,name)

  def f:((List[String],String) => play.twirl.api.HtmlFormat.Appendable) = (items,name) => apply(items,name)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  SOURCE: src/main/twirl/IndexPage.scala.html
                  HASH: 0cd5a3be0e6ec6945252d6c266c48035be65a0d1
                  MATRIX: 263->1|601->19|723->65|743->77|779->83|806->85|824->95|863->97|892->100|1080->263|1113->281|1152->283|1183->288|1214->293|1238->297|1277->306|1308->310|1413->385
                  LINES: 10->1|15->2|19->3|19->3|20->3|21->4|21->4|21->4|22->5|25->8|25->8|25->8|26->9|26->9|26->9|27->10|29->12|31->14
                  -- GENERATED --
              */
          