package draftvisu

import com.raquo.ew.JsString

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom

import typings.plotlyJs.anon.*
import typings.plotlyJs.mod.DownloadImgopts
import typings.plotlyJs.mod.{newPlot, downloadImage}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.*

object Main:

  def main(args: Array[String]): Unit =
    // Laminar initialization
    renderOnDomContentLoaded(dom.document.querySelector("#app"), draftvisu.ici.PPSE.plot())


