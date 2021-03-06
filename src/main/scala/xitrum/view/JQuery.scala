package xitrum.view

import org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE

import xitrum.{Action, Config}

trait JQuery {
  this: Action =>

  lazy val xitrumHead =
    if (Config.isProductionMode)
      <xml:group>
        <link href={urlForResource("xitrum/xitrum.css")} type="text/css" rel="stylesheet" media="all"></link>
        <script type="text/javascript" src={urlForResource("xitrum/jquery-1.6.2.min.js")}></script>
        <script type="text/javascript" src={urlForResource("xitrum/jquery.validate-1.8.1/jquery.validate.min.js")}></script>
        <script type="text/javascript" src={urlForResource("xitrum/jquery.validate-1.8.1/additional-methods.min.js")}></script>
        <script type="text/javascript" src={urlForResource("xitrum/xitrum.js")}></script>
      </xml:group>
    else
      <xml:group>
        <link href={urlForResource("xitrum/xitrum.css")} type="text/css" rel="stylesheet" media="all"></link>
        <script type="text/javascript" src={urlForResource("xitrum/jquery-1.6.2.js")}></script>
        <script type="text/javascript" src={urlForResource("xitrum/jquery.validate-1.8.1/jquery.validate.js")}></script>
        <script type="text/javascript" src={urlForResource("xitrum/jquery.validate-1.8.1/additional-methods.js")}></script>
        <script type="text/javascript" src={urlForResource("xitrum/xitrum.js")}></script>
      </xml:group>

  /** See escape_javascript of Rails */
  def jsEscape(value: Any) = value.toString
    .replace("\\\\", "\\0\\0")
    .replace("</",   "<\\/")
    .replace("\r\n", "\\n")
    .replace("\n",   "\\n")
    .replace("\r",   "\\n")
    .replace("\"",   "\\\"")
    .replace("'",    "\\'")

  def jsCall(function: String, args: String*) =
    function + "(" + args.mkString(", ") + ")"

  def js$(s: String) = jsCall("$", s)

  def jsById(id: String) = js$("\"#" + id + "\"")

  def jsByName(name: String) = js$("\"[name='" + name + "']\"")

  def jsChain(jsCalls: String*) = jsCalls.mkString(".")

  def jsHtml(selector: String, value: Any) =
    jsChain(
      selector,
      jsCall("html", "\"" + jsEscape(value.toString) + "\"")
    )

  def jsVal(selector: String, value: Any) =
    jsChain(
      selector,
      jsCall("val", "\"" + jsEscape(value.toString) + "\"")
    )

  def jsBefore(selector: String, value: Any) = {
    jsChain(
      selector,
      jsCall("before", "\"" + jsEscape(value.toString) + "\"")
    )
  }

  //----------------------------------------------------------------------------

  def jsRender(values: String*) {
    val js = values.mkString(";\n") + ";\n"
    renderText(js, "text/javascript")
  }

  def jsRenderCall(function: String, args: String*) {
    val js = jsCall(function, args:_*)
    jsRender(js)
  }

  def jsRenderHtml(selector: String, value: Any) {
    jsRender(jsHtml(selector, value))
  }

  //----------------------------------------------------------------------------

  /** See http://stackoverflow.com/questions/503093/how-can-i-make-a-redirect-page-in-jquery */
  def jsRedirectTo(location: String) {
    jsRender("window.location.href = \"" + jsEscape(location) + "\"")
  }

  def jsRedirectTo[T: Manifest] { jsRedirectTo(urlFor[T]) }
}
