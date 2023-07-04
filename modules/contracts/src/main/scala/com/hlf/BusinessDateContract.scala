package com.hlf

import org.hyperledger.fabric.contract.annotation.{Contract, Default, Info, Transaction}
import org.hyperledger.fabric.contract.{Context, ContractInterface}

@Contract(
  name = "hlf",
  info = new Info(
    title = "Business Date Creation",
    description = "Setting up current business date",
    version = "0.0.1"
  )
)
@Default
class BusinessDateContract extends ContractInterface {

  @Transaction(intent = Transaction.TYPE.SUBMIT)
  def saveBusinessDate(ctx: Context, date: String): Unit = {
    val stub = ctx.getStub
    stub.putStringState("businessDate", date)
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  def fetchBusinessDate(ctx: Context): String = {
    val stub = ctx.getStub
    stub.getStringState("businessDate")
  }
}
