package core
import org.junit.*
import org.junit.Assert.*
import java.io.*

class FileParserTest {
	lateinit var parser: FileParser

	@Before
	fun setUp() {
		parser = FileParser()
	}

	@Test(expected = FileNotFoundException::class)
	fun parseNonFile() {
		val filePath = "/Unknown/directory"
		parser.parseFile(filePath)
	}

	@Test
	fun parseValidFile() {
		val filePath = "testEntries.txt"
		val entries = parser.parseFile(filePath)
		assertTrue(entries.size == 20)
		assertFalse(entries.isEmpty())
		println(entries.entries)
	}
}