package app

import core.*
import rest.*

fun main(args: Array<String>) {
	println("\nWelcome to Distributed Storage - Group 2 - Cloud Computing 2017\n")

	val client = DictionaryAPI(RequestHandler(), ResponseHandler())
	var cmd: List<String>

	while (true) {
		print("--> ")
		cmd = readLine()?.toLowerCase()?.split(client.parameterDelimiter) ?: listOf(client.helpCommand)

		when (cmd[0]) {
			client.insertCommand -> {
				if (cmd.size != 3)
					client.printErrorDialog()
				else
					client.insert(cmd[1], cmd[2])
			}
			client.deleteCommand -> {
				if (cmd.size != 2)
					client.printErrorDialog()
				else
					client.delete(cmd[1])
			}
			client.searchCommand -> {
				if (cmd.size != 2)
					client.printErrorDialog()
				else
					client.search(cmd[1])
			}
			client.rangeCommand -> {
				if (cmd.size != 3)
					client.printErrorDialog()
				else
					client.range(cmd[1], cmd[2])
			}
			client.helpCommand -> {
				client.printHelp()
			}
			else -> {
				client.printErrorDialog("Invalid command. ")
			}
		}
	}
}