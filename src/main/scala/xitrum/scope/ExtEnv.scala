package xitrum.scope

import org.jboss.netty.handler.codec.http.{DefaultHttpResponse, HttpResponseStatus, HttpVersion, HttpHeaders}
import HttpVersion.HTTP_1_1
import HttpResponseStatus.OK

import xitrum.Config
import xitrum.Action
import xitrum.scope.session.CSRF

trait ExtEnv extends Env with ParamAccess with UploadParamAccess with CSRF {
  this: Action =>

  /** The default response is empty 200 OK */
  val response = {
    // http://en.wikipedia.org/wiki/HTTP_persistent_connection
    // In HTTP 1.1 all connections are considered persistent unless declared otherwise
    val ret = new DefaultHttpResponse(HTTP_1_1, OK)
    HttpHeaders.setContentLength(ret, 0)
    ret
  }

  //----------------------------------------------------------------------------

  // The below are not always accessed by framwork/application, thus set to lazy

  lazy val at = new At

  // Avoid encoding, decoding when cookies/session is not touched by the application
  private var sessionTouched: Boolean = _
  private var cookiesTouched: Boolean = _
  {
    sessionTouched = false
    cookiesTouched = false
  }

  lazy val session = {
    sessionTouched = true
    Config.sessionStore.restore(this)
  }

  lazy val cookies = {
    cookiesTouched = true
    new Cookies(request)
  }

  def sessiono[T](key: String): Option[T] = {
    if (session.isDefinedAt(key)) {
      Some(session[T](key))
    } else {
      None
    }
  }

  def prepareWhenRespond {
    if (sessionTouched) Config.sessionStore.store(session, this)
    if (cookiesTouched) cookies.setCookiesWhenRespond(this)
  }
}
