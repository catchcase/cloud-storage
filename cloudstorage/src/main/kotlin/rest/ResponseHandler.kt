package rest

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.result.*

/**
 * class ResponseHandler
 *
 * This class makes an HTTP request using the Request object passed in as a function parameter. Once an HTTP response has been received, it interprets it and returns a RequestResult object, reflective of whether the response was a successful or failing result. Any messages contained in the HTTP Response body are passed along as well.
 */
class ResponseHandler {

	fun handleResponse(requestObject: Request): RequestResult {
		var success = false
		var msg = ""

		val (_, _, result) = requestObject.responseString()

		when (result) {
			is Result.Failure -> {
				msg = result.getAs<String>() ?: ""
			}
			is Result.Success -> {
				success = true
				msg = result.getAs<String>() ?: ""
			}
		}

		return RequestResult(success, msg)
	}
}

/**
 * class RequestResult
 *
 * This data class consists of a boolean value indicating whether the respective request failed or succeeded, as well as a string message (with a default value of an empty string).
 */
data class RequestResult(val success: Boolean, val msg: String = "")