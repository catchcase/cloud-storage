//package core
//
//import com.nhaarman.mockito_kotlin.*
//import org.junit.*
//import rest.*
//
//class ClientTest {
//
//	private val api = mock<DictionaryAPI> {
//		on { insert("key", "value") } doReturn RequestResult(true)
//		on { insert("", "") } doReturn RequestResult(false, "Invalid parameters")
//
//		on { delete ("key") } doReturn RequestResult(true)
//		on { delete ("") } doReturn RequestResult(false, "Invalid parameters")
//
//		on { search("key") } doReturn RequestResult(true)
//		on { search("") } doReturn RequestResult(false, "Invalid parameters")
//
//		on { range("key", "value") } doReturn RequestResult(true)
//		on { range("", "") } doReturn RequestResult(false, "Invalid parameters")
//	}
//
////	val api = DictionaryAPI(RequestHandler(), ResponseHandler())
//	val client = Client(api)
//
//	@Before
//	fun setUp() {
//	}
//
//	@Test
//	fun readFromValidFileAndStore() {
//		client.readFromFileAndStore("testEntries.txt")
//
//		verify(api, times(20)).insert(any(), any())
//	}
//
//}