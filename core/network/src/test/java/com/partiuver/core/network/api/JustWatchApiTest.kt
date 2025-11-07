package com.partiuver.core.network.api

import com.partiuver.core.network.dto.JustWatchResponseDto
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JustWatchApiTest {

    private lateinit var server: MockWebServer
    private lateinit var api: JustWatchApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JustWatchApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `search usa L=pt_BR por default e faz parsing do JSON`() = runTest {
        val body = """
            {
              "ok": true,
              "error_code": 200,
              "description": [
                {
                  "id": "tt0133093",
                  "title": "The Matrix",
                  "year": 1999,
                  "runtime": 136,
                  "tomatoMeter": 88,
                  "backdrops": ["https://img/backdrop1.jpg", "https://img/backdrop2.jpg"],
                  "offers": [
                    {"type":"RENT HD","name":"Amazon Video","url":"https://prime/xyz"}
                  ],
                  "photo_url": ["https://img/poster1.jpg", "https://img/poster2.jpg"]
                }
              ]
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val resp: JustWatchResponseDto = api.search(query = "matrix") // L default
        val request = server.takeRequest()

        // Caminho e query params
        Assert.assertEquals("/justwatch", request.requestUrl?.encodedPath)
        Assert.assertEquals("matrix", request.requestUrl?.queryParameter("q"))
        Assert.assertEquals("pt_BR", request.requestUrl?.queryParameter("L"))

        // Parsing básico
        Assert.assertTrue(resp.ok)
        Assert.assertEquals(200, resp.errorCode)
        Assert.assertEquals(1, resp.items.size)

        val m = resp.items.first()

        Assert.assertEquals("tt0133093", m.id)
        Assert.assertEquals("The Matrix", m.title)
        Assert.assertEquals(1999, m.year)
        Assert.assertEquals(136, m.runtime)
        Assert.assertEquals(88, m.tomatoMeter)
        Assert.assertEquals(
            listOf("https://img/poster1.jpg", "https://img/poster2.jpg"),
            m.photoUrl
        )
        Assert.assertEquals(
            listOf("https://img/backdrop1.jpg", "https://img/backdrop2.jpg"),
            m.backdrops
        )
        Assert.assertEquals(1, m.offers.size)
        Assert.assertEquals("Amazon Video", m.offers.first().name)
    }

    @Test
    fun `search aceita locale customizado`() = runTest {
        server.enqueue(MockResponse().setBody("""{"ok":true,"error_code":200,"description":[]}"""))

        api.search(query = "matrix", locale = "en_US")

        val request = server.takeRequest()
        Assert.assertEquals("en_US", request.requestUrl?.queryParameter("L"))
    }
}