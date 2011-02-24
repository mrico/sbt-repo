package mrico.repo

import org.apache.maven.wagon.events.{TransferListener,TransferEvent}

private [repo] trait ConsoleTransferListener extends TransferListener {

  def transferStarted(e: TransferEvent) = println("[info] Started - " + e.getResource.getName)
  def transferInitiated(e: TransferEvent) = println("[info] Initiated - " + e.getResource.getName)
  def transferCompleted(e: TransferEvent) = println("[info] Completed - " + e.getResource.getName)

  def transferProgress(e: TransferEvent, data: Array[Byte], i: Int) = {}
  def transferError(e: TransferEvent) = println("[error] " + e.getException.getMessage)

  def debug(s: String) = {} // println("[debug] " + s)
}
