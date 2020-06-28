package com.sabbath.fias

import com.fasterxml.jackson.databind.ObjectMapper
import com.fias.addrobj.AddressObjects
import org.apache.commons.io.input.BOMInputStream
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.util.*
import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller
import javax.xml.stream.XMLInputFactory


val objectMapper = ObjectMapper()

@SpringBootApplication
class FiasApplication(
        val es: ElasticsearchOperations
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val jc: JAXBContext = JAXBContext.newInstance(AddressObjects::class.java)
        val xif = XMLInputFactory.newFactory()
        val bis = (BufferedInputStream(FileInputStream("C:\\workspace-presentation\\fias\\src\\main\\resources\\xml\\AS_ADDROBJ_20200601_29cb8c71-a1a3-41a1-9b8f-da8a67f3fa6f.XML")))
        val xml = BOMInputStream(bis)
        xml.use {
            val xsr = xif.createXMLStreamReader(xml)
            val unmarshaller: Unmarshaller = jc.createUnmarshaller()
            unmarshaller.listener = HousesListener(es)
            unmarshaller.unmarshal(xsr)
        }
        println("Done!!")
    }
}

class HousesListener(val es:ElasticsearchOperations) : Unmarshaller.Listener() {
    override fun afterUnmarshal(target: Any?, parent: Any?) {
        when (target) {
            is AddressObjects.Object -> {
                val query = IndexQueryBuilder()
                        .withSource(target.toJson())
                        .withId(UUID.randomUUID().toString())
                        .build()
                es.index(query, IndexCoordinates.of("address"))
                println(target.toJson())
            }
        }

    }
}


fun main(args: Array<String>) {
    runApplication<FiasApplication>(*args)
}

fun Any?.toJson() = objectMapper.writeValueAsString(this)
