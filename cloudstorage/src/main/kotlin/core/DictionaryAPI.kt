package core

import mu.*
import rest.*

/**
 * class DictionaryAPI
 *
 * This class invokes the necessary RequestHandler and ResponseHandler functions in order to build, dispatch and interpret the Response relating to each Client request. It passes the result of each request directly back to the Client and does not do any interpretation or forking logic itself.
 */
class DictionaryAPI(private val requestHandler: RequestHandler, private val responseHandler: ResponseHandler) {
	companion object: KLogging()

	val parameterDelimiter = " "
	val insertCommand = "insert"
	val deleteCommand = "delete"
	val searchCommand = "search"
	val rangeCommand = "range"
	val helpCommand = "help"
	private val paramSizeErrorMsg = "Incorrect number of input parameters. "

	init {
		logger.info { "Starting core.DictionaryAPI" }
	}

	fun printHelp() {
		println("""
Insert an entry:
insert key value

Delete an entry:
delete key

Search for an entry:
search key

Range query execution:
range startKey endKey

		""")
	}

	fun insert(key: String, value: String): RequestResult {
		logger.debug { "insert() - creating entry with key $key and value $value" }
		val request = requestHandler.insert(key, value)
		return responseHandler.handleResponse(request)
	}

	fun delete(key: String): RequestResult {
		logger.debug { "delete() - deleting entry for key $key" }
		val request = requestHandler.delete(key)
		return responseHandler.handleResponse(request)
	}

	fun search(key: String): RequestResult {
		logger.debug { "search() - reading entry for key $key" }
		val request = requestHandler.search(key)
		return responseHandler.handleResponse(request)
	}

	fun range(startKey: String, endKey: String): RequestResult {
		logger.debug { "range() - reading entry for key $startKey and $endKey" }
		val request = requestHandler.range(startKey, endKey)
		return responseHandler.handleResponse(request)
	}

	fun retrieveAll(): RequestResult {
		logger.debug { "retrieveAll() - reading all entries" }
		val request = requestHandler.retrieveAll()
		return responseHandler.handleResponse(request)
	}

	fun printErrorDialog(errorString: String = paramSizeErrorMsg) {
		println("\n${errorString}Please try again. Use the 'help' command to view the help dialog.\n")
	}
}