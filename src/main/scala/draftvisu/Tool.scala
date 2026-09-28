package draftvisu

/*
 * Copyright (C) 2025 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom
import typings.plotlyJs.anon.*
import typings.plotlyJs.mod.DownloadImgopts
import typings.plotlyJs.mod.{downloadImage}

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.*


object Tool:

  def percentile(data: Seq[Double], percentile: Double): Double =
    val sortedData = data.sorted
    val index = math.ceil((percentile / 100) * sortedData.length).toInt - 1
    sortedData(index)

  def downloadSVGButton(tag: String) =
    button(
      "SVG",
      onClick --> {
        val opt = DownloadImgopts(tag, typings.plotlyJs.plotlyJsStrings.svg)
        downloadImage(tag, opt)
        ()
      }
    )

  def fetchFiles(url: String*): Signal[Option[Seq[String]]] =
    def fut: Future[Seq[String]] =
      Future.sequence(url.map(f => dom.fetch(f).toFuture)).flatMap: s =>
        Future.sequence(s.map(_.text().toFuture))

    Signal.fromFuture(fut)


  case class Bean(low: Double, high: Double, size: Double):
    def middle = (low + high) / 2

  def distribution(s: Seq[Double], beans: Int) =
    val sorted = s.sorted
    val smin = sorted.head
    val smax = sorted.last
    val step = (smax - smin) / beans

    @tailrec def recurse(lowBound: BigDecimal, sorted: List[Double], beans: List[Bean]): List[Bean] =
      val highBound = lowBound + step
      if highBound >= smax
      then (Bean(lowBound.toDouble, highBound.toDouble, sorted.size) :: beans).reverse
      else
        def bean = Bean(lowBound.toDouble, highBound.toDouble, sorted.takeWhile(_ <= highBound).size)
        recurse(highBound, sorted.dropWhile(_ <= highBound), bean :: beans)

    val size = sorted.size
    recurse(BigDecimal(smin), sorted.toList, List()).map(b => b.copy(size = b.size))


  def colors =
    Seq("rgb(158, 228, 147)", "rgb(87, 83, 102)", "rgb(229, 88, 18)")
