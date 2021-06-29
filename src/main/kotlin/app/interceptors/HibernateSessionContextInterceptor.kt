package app.interceptors

import core.Request
import core.Response
import db.HibernateSessionFactory
import intercepting.Interceptor

class HibernateSessionContextInterceptor(
    private val hibernateSessionFactory: HibernateSessionFactory
) : Interceptor {
    override fun interceptRequest(request: Request): Request {
        hibernateSessionFactory.createSession()
        return request
    }

    override fun interceptResponse(response: Response): Response {
        TODO("Not yet implemented")
    }
}