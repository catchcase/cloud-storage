package core

import mu.*
import sun.plugin.dom.exception.*
import java.util.*

/**
 * class Client
 *
 * This class provides operated task execution, as defined in the project description. It logs all necessary data and responses to the CloudStorageGroup2.log file (and prints the same output data to the runtime console). It is responsible for invoking the corresponding DictionaryAPI functions for each task and logging the results.
 */
class Client(private val api: DictionaryAPI, private val filePath: String = "testEntries.txt") {
	private val fileParser = FileParser()
	private lateinit var fileContents: MutableMap<String, String>

	companion object : KLogging()

	init {
		logger.debug { "init()" }
		logger.info { "Starting core.Client" }
		logger.info { "" }
	}

	fun runTasks() {
		initialize()

//		for (i in 1..20)
//			api.delete("$i")
		readFromFileAndStore(filePath)
		randomlySelectAndRead()
		deleteTwoEntries()
		executeRangeQuery()
////		deleteAll()
		initialize()
	}

	private fun initialize() {
		logger.debug { "initialize()" }
		logger.info { "Attempting to initialize the cloud storage service..." }

		val result = api.retrieveAll()

		if (result.success) {
			logger.info { "Successfully initialized service" }
			logger.info { result.msg.replace("=", "") }
		} else {
			logger.info { " - Failed - ${result.msg.replace("=", "")}" }
			throw InvalidStateException("Service could not initialize.")
		}
		logger.info { "" }
	}

	private fun readFromFileAndStore(file: String) {
		logger.debug { "readFromFileAndStore()" }
		logger.info { "Attempting to read from file and store values..." }

		logger.info { "Parsing file: $file" }
		fileContents = fileParser.parseFile(file)
		logger.info { "File parsing complete." }

		logger.info { "Starting insert requests..." }
		for (entry in fileContents) {
			logger.info { "Executing insert request for key: ${entry.key} and value: ${entry.value}" }
			val result = api.insert(entry.key, entry.value)

			if (result.success) {
				logger.info { " - Successfully inserted - ${result.msg.replace("=", "")}" }
			} else {
				logger.info { " - Failed - ${result.msg}" }
			}
		}

		logger.info { "Finished executing insert requests." }
		logger.info { "Retrieving current state of all files..." }
		val result = api.retrieveAll()
		if (result.success)
			logger.info { result.msg.replace("=", "") }
		else
			logger.info { "Failed to retrieve current state" }
		logger.info { "" }
	}

	private fun randomlySelectAndRead() {
		logger.debug { "randomlySelectAndRead()" }
		logger.info { "Randomly selecting and reading three values..." }
		logger.info { "Selecting three entries at random" }
		val keysToRead = randomElemsAsList(3)
		logger.info { "First random key: ${keysToRead.first()}" }
		logger.info { "Second random key: ${keysToRead[1]}" }
		logger.info { "Third random key: ${keysToRead.last()}" }

		logger.info { "Making search requests..." }
		keysToRead.forEach {
			logger.info { "Searching for key: $it" }
			val result = api.search(it)

			if (result.success) {
				logger.info { " - Found key - ${result.msg.replace("=", "")}" }
			} else {
				logger.info { " - Failed - ${result.msg}" }
			}
		}

		logger.info { "Finished executing search requests." }
		logger.info { "" }
	}

	private fun deleteTwoEntries() {
		logger.debug { "deleteTwoEntries()" }
		logger.info { "Deleting two entries at random..." }
		logger.info { "Selecting two entries at random" }
		val keysToDelete = randomElemsAsList(2)
		logger.info { "First random key: ${keysToDelete.first()}" }
		logger.info { "Second random key: ${keysToDelete.last()}" }

		logger.info { "Making delete requests..." }
		keysToDelete.distinct().forEach {
			logger.info { "Deleting entry for key: $it" }
			val result = api.delete(it)

			if (result.success) {
				logger.info { " - Successfully deleted - ${result.msg.replace("=", "")}" }
				fileContents.remove(it)
			} else {
				logger.info { " - Failed - ${result.msg}" }
			}
		}

		logger.info { "Finished executing delete requests." }
		logger.info { "" }
	}

	private fun executeRangeQuery() {
		logger.debug { "executeRangeQuery()" }
		logger.info { "Querying range for two entries at random..." }
		logger.info { "Selecting two entries at random" }
		val rangeKeys = randomElemsAsList(2)
		logger.info { "First random key: ${rangeKeys.first()}" }
		logger.info { "Second random key: ${rangeKeys.last()}" }

		val key1 = rangeKeys.distinct().first()
		val key2 = rangeKeys.distinct().last()
		logger.info { "Making range request for key 1: $key1 and key 2: $key2" }
		val result = api.range(key1, key2)

		if (result.success) {
			logger.info { " - Successfully retrieved range for key 1: $key1 and key 2: $key2\" - ${result.msg.replace("=", "")}" }
		} else {
			logger.info { " - Failed - ${result.msg}" }
		}

		logger.info { "Finished executing range request." }
		logger.info { "" }
	}

	private fun randomElemsAsList(size: Int): MutableList<String> {
		logger.debug { "twoRandomElemsAsList()" }
		val keysAsList = fileContents.keys.toList()
		val resultKeys = mutableListOf<String>()

		while (resultKeys.distinct().size < size) {
			resultKeys.add(keysAsList.getRandomElement())
		}
		return resultKeys
	}

	private fun retrieveAll() {
		logger.debug { "retreiveAll()" }
		logger.info { "Attempting to retrieve all values..." }
		val result = api.retrieveAll()

		if (result.success) {
			logger.info { " - Successfully retreived all entries" }
			logger.info { result.msg }
		} else {
			logger.info { " - Failed - ${result.msg}" }
		}
	}

	private fun deleteAll() {
		logger.debug { "deleteAll()" }
		logger.info { "Attempting to delete all values..." }

		logger.info { "Starting delete requests..." }
		for (entry in fileContents) {
			logger.info { "Executing delete request for key: ${entry.key} and value: ${entry.value}" }
			val result = api.delete(entry.key)

			if (result.success) {
				logger.info { "  - Successfully deleted all entries" }
				logger.info { result.msg.replace("=", "") }
				fileContents.remove(entry.key)
			} else {
				logger.info { " - Failed - ${result.msg}" }
			}
		}
		logger.info { "Finished executing delete requests." }
		logger.info { "" }
	}

//	private fun deleteAll() {
//		logger.debug { "deleteAll()" }
//		logger.info { "Attempting to delete all values..." }
//
//		logger.info { "Starting delete requests..." }
//		for (entry in fileContents) {
//			logger.info { "Executing delete request for key: ${entry.key} and value: ${entry.value}" }
//			val result = api.deleteAll()
//
//			if (result.success) {
//				logger.info { "  - Successfully deleted all entries" }
//				logger.info { result.msg.replace("=", "") }
//				fileContents.clear()
//			} else {
//				logger.info { " - Failed - ${result.msg}" }
//			}
//		}
//		logger.info { "Finished executing delete requests." }
//		logger.info { "" }
//	}
}

fun <E> List<E>.getRandomElement() = this[Random().nextInt(this.size)]