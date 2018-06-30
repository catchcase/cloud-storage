package app

import core.*
import rest.*

fun main(args: Array<String>) {
	val client = if (args.size == 1) {
		Client(DictionaryAPI(RequestHandler(args[0]), ResponseHandler()))
	} else {
		Client(DictionaryAPI(RequestHandler(), ResponseHandler()))
	}

	client.runTasks()
	System.exit(0)
}

//https://cca4cloudstorage.azurewebsites.net