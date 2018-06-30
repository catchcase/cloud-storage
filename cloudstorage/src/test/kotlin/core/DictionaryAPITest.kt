//package core
//import com.github.kittinunf.fuel.core.*
//import com.nhaarman.mockito_kotlin.*
//import core.*
//import org.junit.*
//import rest.*
//
//class DictionaryAPITest {
//
//	private val api = DictionaryAPI(RequestHandler(), ResponseHandler())
//	private val url: String = "http://catchcase.org"
//	private val entriesPath: String = "entries"
//	private val rangePath: String = "range"
//
//
//
//	val responseObj = mock<Response> {
//		on { statusCode } doReturn 200
//	}
//
//	val requestObj = mock<Request> {
//		on { response() }
//	}
//
//	val requestHandler = mock<RequestHandler> {
//		on { insert("key", "value") } doReturn requestObj
//	}
//
//	@Before
//	fun setUp() {
//	}
//
//	@Test
//	fun insert() {
//		val requestResult = api.insert("key", "value")
//	}
//
//	@Test
//	fun delete() {
//	}
//
//	@Test
//	fun search() {
//	}
//
//	@Test
//	fun range() {
//	}
//}