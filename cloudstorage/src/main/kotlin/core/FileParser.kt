package core

import java.io.*

class FileParser {

	fun parseFile(filePath: String): MutableMap<String, String> {
		val classLoader = javaClass.classLoader

		try {
			val file = File(classLoader.getResource(filePath)?.file)
			val entries = mutableMapOf<String, String>()
			file.readLines().forEach {
				val splitLine = it.split(":")
				entries.put(splitLine[0], splitLine[1])
			}

			return entries
		} catch (e: Exception) {
			throw FileNotFoundException("The given directory does not exist.")
		}

	}
}