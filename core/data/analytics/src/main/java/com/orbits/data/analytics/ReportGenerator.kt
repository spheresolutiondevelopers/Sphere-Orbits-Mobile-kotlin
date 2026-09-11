package com.orbits.data.analytics

import com.orbits.core.common.Logger
import com.opencsv.CSVWriter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.properties.UnitValue
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ReportGenerator @Inject constructor() {

    companion object {
        private const val TAG = "ReportGenerator"
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * Generate a report in the specified format.
     */
    suspend fun generateReport(
        startDate: String,
        endDate: String,
        data: Map<String, Any>,
        format: String = "json"
    ): String {
        return when (format.lowercase()) {
            "csv" -> generateCsvReport(startDate, endDate, data)
            "pdf" -> generatePdfReport(startDate, endDate, data)
            "json" -> generateJsonReport(startDate, endDate, data)
            else -> {
                Logger.w(TAG, "Unknown format '$format', defaulting to JSON")
                generateJsonReport(startDate, endDate, data)
            }
        }
    }

    /**
     * Generate CSV report.
     */
    private fun generateCsvReport(
        startDate: String,
        endDate: String,
        data: Map<String, Any>
    ): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val writer = OutputStreamWriter(outputStream)
            val csvWriter = CSVWriter(writer)

            // Write header
            csvWriter.writeNext(arrayOf(
                "Metric", "Value"
            ))

            // Write data
            val stats = data["stats"] as? Map<*, *>
            if (stats != null) {
                stats.forEach { (key, value) ->
                    csvWriter.writeNext(arrayOf(
                        key.toString(),
                        value.toString()
                    ))
                }
            }

            csvWriter.close()
            outputStream.toString("UTF-8")
        } catch (e: Exception) {
            Logger.e(TAG, "Error generating CSV report", e)
            "Error generating CSV report: ${e.message}"
        }
    }

    /**
     * Generate PDF report.
     */
    private fun generatePdfReport(
        startDate: String,
        endDate: String,
        data: Map<String, Any>
    ): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val writer = PdfWriter(outputStream)
            val pdf = PdfDocument(writer)
            val document = Document(pdf)

            // Title
            document.add(Paragraph("Sphere Schedule - Productivity Report")
                .setFontSize(18f)
                .setBold())

            // Date range
            document.add(Paragraph("Report Period: $startDate to $endDate")
                .setFontSize(12f))

            document.add(Paragraph("Generated: ${LocalDateTime.now().format(DATE_FORMATTER)}")
                .setFontSize(12f))

            document.add(Paragraph("\n"))

            // Stats table
            val stats = data["stats"] as? Map<*, *>
            if (stats != null) {
                document.add(Paragraph("Key Metrics").setBold().setFontSize(14f))

                val table = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
                table.useAllAvailableWidth()

                stats.forEach { (key, value) ->
                    table.addCell(Cell().add(Paragraph(key.toString())))
                    table.addCell(Cell().add(Paragraph(value.toString())))
                }

                document.add(table)
            }

            document.close()
            outputStream.toString("UTF-8")
        } catch (e: Exception) {
            Logger.e(TAG, "Error generating PDF report", e)
            "Error generating PDF report: ${e.message}"
        }
    }

    /**
     * Generate JSON report.
     */
    private fun generateJsonReport(
        startDate: String,
        endDate: String,
        data: Map<String, Any>
    ): String {
        return try {
            val reportData = ReportData(
                generatedAt = LocalDateTime.now().format(DATE_FORMATTER),
                startDate = startDate,
                endDate = endDate,
                data = data.mapValues { it.value.toString() }
            )
            json.encodeToString(ReportData.serializer(), reportData)
        } catch (e: Exception) {
            Logger.e(TAG, "Error generating JSON report", e)
            "Error generating JSON report: ${e.message}"
        }
    }

    @Serializable
    private data class ReportData(
        val generatedAt: String,
        val startDate: String,
        val endDate: String,
        val data: Map<String, String>
    )
}