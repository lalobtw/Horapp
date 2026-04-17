package com.horapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.room.Room
import com.horapp.R
import com.horapp.data.local.AppDatabase
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object PdfReportGenerator {

    private val primaryColor = Color.rgb(0, 91, 191)   // #005BBF
    private val tertiaryColor = Color.rgb(158, 67, 0)  // #9E4300
    private val onSurface = Color.rgb(25, 28, 29)       // #191C1D
    private val onSurfaceVariant = Color.rgb(65, 71, 84) // #414754
    private val surfaceContainerLow = Color.rgb(243, 244, 245)

    fun generate(context: Context, studentName: String): File {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "horapp.db")
            .allowMainThreadQueries()
            .build()

        val profile = db.studentProfileDao().getProfileSync()
        val entries = db.serviceEntryDao().getAllEntriesSync()
        db.close()

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        var yPos = 0f

        // ── Header ───────────────────────────────────────────────────────────
        val headerPaint = Paint().apply { color = primaryColor; isAntiAlias = true }
        canvas.drawRect(0f, 0f, 595f, 120f, headerPaint)

        val titlePaint = Paint().apply {
            color = Color.WHITE; textSize = 24f; isFakeBoldText = true; isAntiAlias = true
        }
        canvas.drawText(context.getString(R.string.pdf_header_title), 32f, 54f, titlePaint)

        val subtitlePaint = Paint().apply { color = Color.argb(200, 255, 255, 255); textSize = 13f; isAntiAlias = true }
        canvas.drawText(profile?.institution ?: "", 32f, 76f, subtitlePaint)
        canvas.drawText(
            context.getString(R.string.pdf_generated_at, LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))),
            32f, 96f, subtitlePaint
        )

        yPos = 136f

        // ── Datos del estudiante ──────────────────────────────────────────────
        val sectionPaint = Paint().apply { color = primaryColor; textSize = 14f; isFakeBoldText = true; isAntiAlias = true }
        canvas.drawText(context.getString(R.string.pdf_student_data_section), 32f, yPos, sectionPaint)
        yPos += 20f

        val bodyPaint = Paint().apply { color = onSurface; textSize = 11f; isAntiAlias = true }
        val labelPaint = Paint().apply { color = onSurfaceVariant; textSize = 10f; isAntiAlias = true }

        fun drawRow(label: String, value: String) {
            canvas.drawText(label, 32f, yPos, labelPaint)
            canvas.drawText(value, 160f, yPos, bodyPaint)
        }
        drawRow(context.getString(R.string.pdf_label_name), profile?.fullName ?: "—"); yPos += 18f
        drawRow(context.getString(R.string.pdf_label_id), profile?.studentId ?: "—"); yPos += 18f
        drawRow(context.getString(R.string.pdf_label_major), profile?.major ?: "—"); yPos += 18f
        drawRow(context.getString(R.string.pdf_label_location), profile?.serviceLocation ?: "—"); yPos += 18f
        drawRow(context.getString(R.string.pdf_label_email), profile?.email ?: "—"); yPos += 18f

        yPos += 16f

        // ── Resumen de horas ──────────────────────────────────────────────────
        val totalHours = entries.sumOf { it.hours.toDouble() }.toFloat()
        val bgPaint = Paint().apply { color = surfaceContainerLow }
        canvas.drawRoundRect(32f, yPos, 563f, yPos + 60f, 12f, 12f, bgPaint)
        val bigNumPaint = Paint().apply { color = primaryColor; textSize = 28f; isFakeBoldText = true; isAntiAlias = true }
        canvas.drawText(context.getString(R.string.hrs_unit_format, totalHours), 52f, yPos + 36f, bigNumPaint)
        val goalPaint = Paint().apply { color = onSurfaceVariant; textSize = 11f; isAntiAlias = true }
        canvas.drawText(
            context.getString(R.string.pdf_summary_format, profile?.totalHoursGoal ?: 480f, entries.size),
            52f, yPos + 52f, goalPaint
        )
        yPos += 80f

        // ── Tabla de registros ────────────────────────────────────────────────
        canvas.drawText(context.getString(R.string.pdf_activities_section), 32f, yPos, sectionPaint)
        yPos += 18f

        // Encabezados de tabla
        val headerBgPaint = Paint().apply { color = Color.rgb(216, 226, 255) }
        canvas.drawRect(32f, yPos, 563f, yPos + 22f, headerBgPaint)
        val tableHeaderPaint = Paint().apply { color = primaryColor; textSize = 10f; isFakeBoldText = true; isAntiAlias = true }
        canvas.drawText(context.getString(R.string.pdf_col_date), 38f, yPos + 15f, tableHeaderPaint)
        canvas.drawText(context.getString(R.string.pdf_col_org), 120f, yPos + 15f, tableHeaderPaint)
        canvas.drawText(context.getString(R.string.pdf_col_category), 320f, yPos + 15f, tableHeaderPaint)
        canvas.drawText(context.getString(R.string.pdf_col_hours), 490f, yPos + 15f, tableHeaderPaint)
        yPos += 26f

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val rowPaint = Paint().apply { color = onSurface; textSize = 10f; isAntiAlias = true }
        val altRowPaint = Paint().apply { color = surfaceContainerLow }

        entries.forEachIndexed { idx, entry ->
            if (yPos > 800f) {
                document.finishPage(page)
                val nextPage = document.startPage(PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create())
                page = nextPage
                canvas = page.canvas
                yPos = 40f
            }

            if (idx % 2 == 1) canvas.drawRect(32f, yPos - 12f, 563f, yPos + 8f, altRowPaint)

            val date = LocalDate.ofEpochDay(entry.dateEpochDay)
            canvas.drawText(date.format(dateFormatter), 38f, yPos, rowPaint)
            canvas.drawText(entry.organization.take(28), 120f, yPos, rowPaint)
            canvas.drawText(entry.category, 320f, yPos, rowPaint)
            val hoursPaint = Paint().apply { color = primaryColor; textSize = 10f; isFakeBoldText = true; isAntiAlias = true }
            canvas.drawText("%.1f h".format(entry.hours), 490f, yPos, hoursPaint)
            yPos += 20f
        }

        // ── Firma / pie ───────────────────────────────────────────────────────
        yPos += 20f
        val dividerPaint = Paint().apply { color = Color.rgb(193, 198, 214); strokeWidth = 0.5f }
        canvas.drawLine(32f, yPos, 563f, yPos, dividerPaint)
        yPos += 16f
        val footerPaint = Paint().apply { color = onSurfaceVariant; textSize = 9f; isAntiAlias = true }
        canvas.drawText(context.getString(R.string.pdf_footer_text, LocalDate.now().year), 32f, yPos, footerPaint)

        document.finishPage(page)

        // ── Guardar archivo ───────────────────────────────────────────────────
        val reportsDir = File(context.filesDir, "reports")
        reportsDir.mkdirs()
        val file = File(reportsDir, "reporte_servicio_social.pdf")
        document.writeTo(FileOutputStream(file))
        document.close()

        return file
    }
}
