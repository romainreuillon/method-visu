package draftvisu.ici

/*
 * Copyright (C) 2025 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 */

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows

import draftvisu.Tool

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import typings.echarts.typesDistEchartsMod.*
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("echarts", JSImport.Namespace)
object EChartsJS extends js.Object:
  def init(dom: org.scalajs.dom.Element): js.Dynamic = js.native

enum AdditionalSerie:
  case Horizontal(value: Double)
  case Set(values: Seq[Double])

trait BrushSelectedEvent extends js.Object:
  val batch: js.Array[BrushBatch]

trait BrushBatch extends js.Object:
  val selected: js.Array[BrushSelection]

trait BrushSelection extends js.Object:
  val seriesIndex: Int
  val dataIndex: js.Array[Int]

import typings.echarts.echartsStrings.scatter

extension (chart: EChartsType)
  def dyn: js.Dynamic = chart.asInstanceOf[js.Dynamic]

  def on(event: String)(cb: js.Dynamic => Unit): Unit =
    dyn.on(event, cb)

  def setOption(opt: js.Any): Unit =
    dyn.setOption(opt)

  def getOption(): js.Any = dyn.getOption()

  def getHeight(): Double =
    dyn.getHeight().asInstanceOf[Double]

  def onBrushSelected(cb: BrushSelectedEvent => Unit): Unit =
    dyn.on(
      "brushSelected",
      (e: js.Dynamic) =>
        cb(e.asInstanceOf[BrushSelectedEvent])
    )

object PPSE:

  def plot(): HtmlElement =
    div(
      h1("Graph Using ECharts"),

      child <-- Tool.fetchFiles( "data/ici/PPSE-5th-HardLockdown3-20260919_tset_niche.json" ).map:
        case None => "Loading..."
        case Some(json) =>
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

          div(
            idAttr := "chart",

            onMountCallback:
              ctx =>
                val points = data(json(0))
                val series = ScatterSeriesOption()

                series.setType(scatter)
                series.setSymbol("rect")
                series.setSymbolSize(12.0)
                series.setData(
                  points
                    .map:
                      p =>
                        js.Array(
                          p.x,
                          p.y,
                          p.color
                        )
                    .toJSArray
                )

                val colorValues = points.map(_.color)
                val colorMin = colorValues.min
                val colorMax = colorValues.max

                val option =
                  js.Dynamic.literal(
                    xAxis =
                      js.Dynamic.literal(
                        `type` = "value",
                        name = "hospit_days_upper_threshold",
                        nameLocation = "middle",
                        nameGap = 35
                      ),
                    yAxis =
                      js.Dynamic.literal(
                        `type` = "value",
                        name = "peak_hospit",
                        nameLocation = "middle",
                        nameGap = 45
                      ),
                    grid =
                      js.Dynamic.literal(
                        left = 80,
                        right = 120,
                        top = 40,
                        bottom = 80
                      ),
                    legend =
                      js.Dynamic.literal(
                        show = false
                      ),
                    textStyle =
                      js.Dynamic.literal(
                        fontSize = 14
                      ),
                    visualMap =
                      js.Dynamic.literal(
                        min = colorMin,
                        max = colorMax,
                        dimension = 2,
                        show = true,
                        calculable = true,
                        orient = "vertical",
                        right = 10,
                        top = "center",
                        inRange =
                          js.Dynamic.literal(
                            color =
                              js.Array(
                                "rgb(255,248,220)",
                                "rgb(255,224,138)",
                                "rgb(255,183,77)",
                                "rgb(239,108,0)",
                                "rgb(183,28,28)"
                              )
                          )
                      ),
                    series =
                      js.Array(series)
                  )

                ctx.thisNode.ref
                  .asInstanceOf[dom.html.Element]
                  .style
                  .width = "1000px"

                ctx.thisNode.ref
                  .asInstanceOf[dom.html.Element]
                  .style
                  .height = "1000px"

                val chart = typings.echarts.mod.init(ctx.thisNode.ref)
                chart.setOption(option)
          )
    )
