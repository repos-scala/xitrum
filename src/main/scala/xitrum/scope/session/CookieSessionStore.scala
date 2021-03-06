package xitrum.scope.session

import org.jboss.netty.handler.codec.http.DefaultCookie

import xitrum.{Action, Config}
import xitrum.scope.ExtEnv

class CookieSessionStore extends SessionStore {
  def restore(extEnv: ExtEnv): Session = {
    extEnv.cookies(Config.cookieName) match {
      case Some(cookie) =>
        val base64String = cookie.getValue
        val ret          = new CookieSession
        ret.decrypt(base64String)
        ret

      case None =>
        new CookieSession
    }
  }

  def store(session: Session, extEnv: ExtEnv) {
    val cookieSession = session.asInstanceOf[CookieSession]
    val s = cookieSession.encrypt

    extEnv.cookies(Config.cookieName) match {
      case Some(cookie) =>
        cookie.setHttpOnly(true)
        cookie.setPath("/")
        cookie.setValue(s)

      case None =>
        val cookie = new DefaultCookie(Config.cookieName, s)
        cookie.setHttpOnly(true)
        cookie.setPath("/")
        extEnv.cookies.add(cookie)
    }
  }
}
