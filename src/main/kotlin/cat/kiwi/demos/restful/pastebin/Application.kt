package cat.kiwi.demos.restful.pastebin

import com.google.common.hash.Hashing
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlin.text.Charsets.UTF_8

object Application {
    private val contentMap = HashMap<String, String>()
//    private val configFile = File("./config.json")

    @JvmStatic
    fun main(args: Array<String>) {
        val vertx = Vertx.vertx()
        val router = Router.router(vertx)

        router.post("/paste").handler(::pasteHandler)
        router.get("/g/:key").handler(::getContentHandler)

        vertx.createHttpServer().requestHandler(router).listen(8180)
    }

    /**
     * @api {get} /g/:key Query content by key
     * @apiName GetContent
     * @apiGroup GetContent
     *
     * @apiParam {String} key content key
     *
     * @apiSuccessExample {json} Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "content": "foobar"
     *     }
     *
     * @apiErrorExample {json} Error-Response:
     *     HTTP/1.1 404 Not Found
     *     {
     *       "Exception": "error"
     *     }
     */
    private fun getContentHandler(routingContext: RoutingContext) = with(routingContext) {
        when (val result = contentMap[request().getParam("key")]) {
            null -> {
                val responseJson = JsonObject()
                responseJson.put("error", "TODO")
                response().putHeader("Content-Type", "application/json").setStatusCode(404).end(responseJson.toString())
            }

            else -> {
                val responseJson = JsonObject()
                responseJson.put("content", result)
                response().putHeader("Content-Type", "application/json").end(responseJson.toString())
            }
        }
    }


    /**
     * @api {post} /paste Send paste
     * @apiName SendPaste
     * @apiGroup SendPaste
     *
     *
     * @apiParamExample {json} Request-Example:
     *     {
     *       "content": "foobar"
     *     }
     * @apiSuccessExample {json} Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "key": "deadbeef"
     *     }
     *
     * @apiErrorExample {json} Error-Response:
     *     HTTP/1.1 500 Internal Server Error
     *     {
     *       "Exception": "error"
     *     }
     */
    private fun pasteHandler(routingContext: RoutingContext) = with(routingContext) {
        request().bodyHandler {
            try {
                val requestJson = JsonObject(it)
                if (requestJson.getString("content") == null) {
                    response().setStatusCode(500).end()
                }
                val hash =
                    Hashing.goodFastHash(32).hashBytes(requestJson.getString("content").toByteArray(UTF_8)).toString()
                contentMap[hash] = requestJson.getString("content").toString()
                val responseJson = JsonObject()
                responseJson.put("key", hash)
                response().putHeader("Content-Type", "application/json").end(responseJson.toString())

            } catch (e: Exception) {
                val responseJson = JsonObject()
                responseJson.put("error", "TODO")
                response().putHeader("Content-Type", "application/json").setStatusCode(500).end(responseJson.toString())
            }
        }
    }
}