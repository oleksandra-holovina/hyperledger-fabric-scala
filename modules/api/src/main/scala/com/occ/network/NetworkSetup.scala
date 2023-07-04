package com.hlf.network

import io.grpc.netty.shaded.io.grpc.netty.{GrpcSslContexts, NettyChannelBuilder}
import io.grpc.{CallOptions, ManagedChannel}
import org.hyperledger.fabric.client.identity.{Identities, Signers, X509Identity}
import org.hyperledger.fabric.client.{Contract, Gateway}
import org.slf4j.LoggerFactory

import java.nio.file.{Files, Path, Paths}
import java.util.concurrent.TimeUnit
import java.util.function.UnaryOperator

object NetworkSetup {
  val logger = LoggerFactory.getLogger(NetworkSetup.getClass)

  val mspID = "Org1MSP"
  val channelName = "mychannel"
  val chaincodeName = "hlf"

//  val cryptoPath = Paths.get("test-network", "organizations", "peerOrganizations", "org1.example.com")
  val certPath = Paths.get("/Users/lexie__h/Desktop/programming/Blockchain/hyperledger-fabric-scala/modules/test-network/organizations/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/signcerts/cert.pem") //cryptoPath.resolve(Paths.get("users", "User1@org1.example.com", "msp", "signcerts", "cert.pem"))
  val keyDirPath = Paths.get("/Users/lexie__h/Desktop/programming/Blockchain/hyperledger-fabric-scala/modules/test-network/organizations/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/keystore")  //cryptoPath.resolve(Paths.get("users", "User1@org1.example.com", "msp", "keystore"))
  val tlsCertPath = Paths.get("/Users/lexie__h/Desktop/programming/Blockchain/hyperledger-fabric-scala/modules/test-network/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt")  //cryptoPath.resolve(Paths.get("peers", "peer0.org1.example.com", "tls", "ca.crt"))

  val peerEndpoint = "localhost:7051"
  val overrideAuth = "peer0.org1.example.com"

  def createContract(): Contract = {
    val channel = newGrpcConnection()

    val evaluateOption: UnaryOperator[CallOptions] = _.withDeadlineAfter(5, TimeUnit.SECONDS)
    val endorseOption: UnaryOperator[CallOptions] = _.withDeadlineAfter(15, TimeUnit.SECONDS)
    val submitOption: UnaryOperator[CallOptions] = _.withDeadlineAfter(5, TimeUnit.SECONDS)
    val commitOption: UnaryOperator[CallOptions] = _.withDeadlineAfter(3, TimeUnit.SECONDS)

    val builder = Gateway.newInstance
      .identity(newIdentity())
      .signer(newSigner()).connection(channel)
      .evaluateOptions(evaluateOption)
      .endorseOptions(endorseOption)
      .submitOptions(submitOption)
      .commitStatusOptions(commitOption)

    val gateway = builder.connect

    val network = gateway.getNetwork(channelName)
    network.getContract(chaincodeName)
  }

  private def newGrpcConnection(): ManagedChannel = {
    val tlsCertReader = Files.newBufferedReader(tlsCertPath)
    val tlsCert = Identities.readX509Certificate(tlsCertReader)
    NettyChannelBuilder
      .forTarget(peerEndpoint)
      .sslContext(GrpcSslContexts.forClient.trustManager(tlsCert).build)
      .overrideAuthority(overrideAuth)
      .build
  }

  private def newIdentity() = {
    val certReader = Files.newBufferedReader(certPath)
    val certificate = Identities.readX509Certificate(certReader)
    new X509Identity(mspID, certificate)
  }

  private def newSigner() = {
    val keyReader = Files.newBufferedReader(getPrivateKeyPath)
    val privateKey = Identities.readPrivateKey(keyReader)
    Signers.newPrivateKeySigner(privateKey)
  }


  private def getPrivateKeyPath: Path = {
    val keyFiles = Files.list(keyDirPath)
    keyFiles.findFirst.orElseThrow()
  }
}
