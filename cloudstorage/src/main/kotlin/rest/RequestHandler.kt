package rest

import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.core.*
import mu.*

/**
 * class RequestHandler
 *
 * This class constructs the necessary Fuel-based Request objects mapped to the appropriate REST interface URL paths. It does not actually execute the request; it returns a ready-to-fire Request object. The default url is set to localhost:8080 but this may be overridden through a constructor argument.
 */
class RequestHandler(
		private val url: String = "http://localhost:8080",
		private val entriesPath: String = "entries",
		private val rangePath: String = "range"
) {
	companion object: KLogging()

	init {
		logger.debug { "init()" }

		FuelManager.instance.basePath = url
		logger.debug { "FuelManager basePath set to $url" }
	}

	fun insert(key: String, value: String): Request = "/$entriesPath/$key".httpPost().body(value)

	fun delete(key: String): Request = "/$entriesPath/$key".httpDelete()

	fun search(key: String): Request = "/$entriesPath/$key".httpGet()

	fun range(startKey: String, endKey: String): Request =
			"/$entriesPath/$rangePath/$startKey/$endKey".httpGet()

	fun retrieveAll(): Request = "/$entriesPath".httpGet()
}