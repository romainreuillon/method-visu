package draftvisu.ici

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

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import draftvisu.Tool
import org.scalablytyped.runtime.StObject
import org.scalajs.dom
import typings.plotlyJs.anon.*
import typings.plotlyJs.mod.*
import typings.plotlyJs.plotlyJsStrings.*

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

object PPSE:

  def plot(): HtmlElement =
    div(
      h1("Graph Using Plotly"),
      child <-- Tool.fetchFiles("/data/ici/PPSE-5th-HardLockdown3-20260919_tset_niche.json").map:
        case None => "Loading..."
        case Some(str) =>
          def data(str: String) =
            val vars = ujson.read(str)("data")(0)("variables")
            val y = vars("evolution$pattern0").arr.toSeq.map(_.num)
            val x = vars("evolution$pattern1").arr.toSeq.map(_.num)

            val y1 = vars("objective$peak_hospit").arr.toSeq.map(_.num).map(x => ((x - 30) / 4).toInt * 4 + 32).map(_.toDouble)
            val x1 = vars("objective$hospit_days_upper_threshold").arr.toSeq.map(_.num).map(_.toInt).map(_.toDouble + 0.5)

            val color = vars("evolution$likelihood").arr.map(_.num).toSeq
            (x1 lazyZip y1 lazyZip color).map: (x, y, color) =>
              (x = x, y = y, color = color)
            .distinctBy(v => (v.x, v.y))

          div(idAttr := "chart",
            onMountCallback: ctx =>
              def toPlotData(d: Seq[(x: Double, y: Double, color: Double)]) =
                val color = "rgb(158, 228, 147)"

                val colorscale: ColorScale = js.Array(
                  (0.0, "rgb(255,248,220)"),
                  (0.25, "rgb(255,224,138)"),
                  (0.5, "rgb(255,183,77)"),
                  (0.75, "rgb(239,108,0)"),
                  (1.0, "rgb(183,28,28)")
                )

                def toPlot =
                  //val marker = PartialBoxPlotMarkerColor().setSize(5).setColor(colors(str.head).toJSArray)
                  val data = PartialBoxPlotData()
                  data.setX(d.map(_.x).toJSArray)
                  data.setY(d.map(_.y).toJSArray)
                  data.setWidth(1.0)
                  data.setMode(markers)

                  val marker =
                    PartialBoxPlotMarkerColor()
                      .setSymbol("square")
                      .setSize(12)
                      .setColor(d.map(_.color).toJSArray)

                  StObject.set(marker, "colorscale", colorscale)
                  StObject.set(marker, "showscale", true)

                  data.setMarker(marker)

                  //data.setMarker(PartialBoxPlotMarkerColor().setSymbol("square").setSize(12).setColor(d.map(_.color).toJSArray))
                  //data.setLine(PartialScatterLine().setColor(color))
                  data.setShowlegend(false)
                  //data.setName("Infected")
                  data

                toPlot


              val layout = PartialLayout()
              layout.setWidth(1000)
              layout.setHeight(1000)

              layout.setXaxis(PartialLayoutAxisAnchor().setTitle(PartialDataTitleFont().setText("hospit_days_upper_threshold")))
              layout.setYaxis(PartialLayoutAxisAnchor().setTitle(PartialDataTitleFont().setText("peak_hospit")))
//
//              layout.setXaxis2(PartialLayoutAxisAnchor().setTitle(PartialDataTitleFont().setText("infectedWidth")))
//              layout.setYaxis2(PartialLayoutAxisAnchor().setTitle(PartialDataTitleFont().setText("infectedPeak")))
//
//
//              layout.setXaxis3(PartialLayoutAxisAnchor().setTitle(PartialDataTitleFont().setText("infectedWidth")))
//              layout.setYaxis3(PartialLayoutAxisAnchor().setTitle(PartialDataTitleFont().setText("infectedPeak")))

              layout.setFont(PartialFont().setSize(14))
              layout.setMargin(PartialMargin().setPad(5))

              layout.setGrid(Partialrowsnumberroworder().setPattern(independent).setRows(1).setColumns(1))
              //layout.setAnnotations(js.Array(PartialAnnotations().setText("test").setX("x1").setY("y1")))


              val datas =
                Seq[typings.plotlyJs.mod.Data](
                  toPlotData(data(str(0))).setXaxis("x1").setYaxis("y1"),
//                  toPlotData(data(str(1))).setXaxis("x2").setYaxis("y2"),
//                  toPlotData(data(str(2))).setXaxis("x3").setYaxis("y3"),

                ).toJSArray
              newPlot(ctx.thisNode.ref.id, datas, layout)
          ),
      Tool.downloadSVGButton("chart")

    )
