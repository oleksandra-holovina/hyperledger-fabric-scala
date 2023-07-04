package com.hlf.service

import com.google.gson.{GsonBuilder, JsonParser}
import org.hyperledger.fabric.client.Contract

import java.nio.charset.StandardCharsets

class BusinessDateService(contract: Contract) {

  private val gson = new GsonBuilder().setPrettyPrinting().create

  def fetchBusinessDate(): String = {
    val date = contract.evaluateTransaction("fetchBusinessDate")
    prettyJson(date)
  }

  def saveBusinessDate(date: String): Unit = {
    contract.submitTransaction("saveBusinessDate", date)
  }

  private def prettyJson(json: Array[Byte]): String = prettyJson(new String(json, StandardCharsets.UTF_8))

  private def prettyJson(json: String): String = {
    val parsedJson = JsonParser.parseString(json)
    gson.toJson(parsedJson)
  }
}
