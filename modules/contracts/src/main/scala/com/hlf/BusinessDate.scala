package com.hlf

import com.owlike.genson.annotation.JsonProperty
import org.hyperledger.fabric.contract.annotation.{DataType, Property}

@DataType
case class BusinessDate(@Property @JsonProperty("date") date: String)
