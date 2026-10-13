package com.example.data.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.Customer
import com.example.data.model.CustomerTransaction
import com.example.data.model.UserProfile
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun generateCustomerStatement(
        context: Context,
        profile: UserProfile,
        customer: Customer,
        transactions: List<CustomerTransaction>
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 portrait: 595 x 842 pt
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.rgb(0, 121, 107) // Emerald green
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val headerPaint = Paint().apply {
            color = Color.rgb(33, 33, 33)
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 11f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(200, 200, 200)
            strokeWidth = 1f
        }

        val duePaint = Paint().apply {
            color = Color.rgb(211, 47, 47) // Red
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val paymentPaint = Paint().apply {
            color = Color.rgb(46, 125, 50) // Green
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        // Header Background Banner
        val bannerPaint = Paint().apply {
            color = Color.rgb(224, 242, 241) // Light teal
        }
        canvas.drawRect(20f, 20f, 575f, 110f, bannerPaint)

        // Shop Name & Details
        canvas.drawText(profile.shopName, 40f, 55f, titlePaint)
        canvas.drawText("স্বত্বাধিকারী: ${profile.ownerName} | মোবাইল: ${profile.phone}", 40f, 75f, subPaint)
        canvas.drawText("ইমেইল: ${profile.email}", 40f, 95f, subPaint)

        // Customer Info Card
        var y = 140f
        canvas.drawText("গ্রাহকের হিসাব বিবরণী (Customer Statement)", 40f, y, headerPaint)
        y += 20f
        val sdfDate = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        canvas.drawText("নাম: ${customer.name}", 40f, y, textPaint)
        canvas.drawText("মোবাইল: ${customer.phone}", 320f, y, textPaint)
        y += 18f
        if (customer.address.isNotEmpty()) {
            canvas.drawText("ঠিকানা: ${customer.address}", 40f, y, textPaint)
        }
        canvas.drawText("স্টেটমেন্ট তৈরির তারিখ: ${sdfDate.format(Date())}", 320f, y, subPaint)
        y += 25f

        // Table Header
        val tableHeaderPaint = Paint().apply {
            color = Color.rgb(0, 105, 92)
        }
        canvas.drawRect(30f, y, 565f, y + 26f, tableHeaderPaint)

        val tableHeadTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        canvas.drawText("তারিখ", 40f, y + 17f, tableHeadTextPaint)
        canvas.drawText("বিবরণ", 140f, y + 17f, tableHeadTextPaint)
        canvas.drawText("বাকি (টাকা)", 340f, y + 17f, tableHeadTextPaint)
        canvas.drawText("জমা (টাকা)", 450f, y + 17f, tableHeadTextPaint)
        y += 35f

        val itemSdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        var totalGiven = 0.0
        var totalReceived = 0.0

        for (tx in transactions) {
            if (y > 740f) break // Protect page height
            val dateStr = itemSdf.format(Date(tx.timestamp))
            canvas.drawText(dateStr, 40f, y, textPaint)
            val noteTruncated = if (tx.note.length > 28) tx.note.take(25) + "..." else tx.note.ifEmpty { "লেনদেন" }
            canvas.drawText(noteTruncated, 140f, y, textPaint)

            if (tx.type == "DUE") {
                totalGiven += tx.amount
                canvas.drawText("৳ %.2f".format(tx.amount), 340f, y, duePaint)
                canvas.drawText("-", 460f, y, textPaint)
            } else {
                totalReceived += tx.amount
                canvas.drawText("-", 350f, y, textPaint)
                canvas.drawText("৳ %.2f".format(tx.amount), 450f, y, paymentPaint)
            }

            canvas.drawLine(30f, y + 6f, 565f, y + 6f, linePaint)
            y += 24f
        }

        // Summary Total Box
        y += 15f
        val summaryBg = Paint().apply {
            color = Color.rgb(245, 245, 245)
        }
        canvas.drawRect(30f, y, 565f, y + 55f, summaryBg)
        canvas.drawText("মোট বাকি: ৳ %.2f".format(totalGiven), 45f, y + 22f, duePaint)
        canvas.drawText("মোট জমা: ৳ %.2f".format(totalReceived), 220f, y + 22f, paymentPaint)

        val netStatus = if (customer.totalDue >= 0) "মোট বাকি পাওনা: ৳ %.2f".format(customer.totalDue)
        else "অগ্রিম জমা: ৳ %.2f".format(-customer.totalDue)
        val netPaint = if (customer.totalDue >= 0) duePaint else paymentPaint
        canvas.drawText(netStatus, 400f, y + 22f, netPaint)

        // Footer / Branding requested by user
        val footerY = 810f
        canvas.drawLine(30f, footerY - 15f, 565f, footerY - 15f, linePaint)
        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 9.5f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        canvas.drawText("টালিখাতা অ্যাপস | ক্রিয়েট বাই: ${profile.ownerName} (${profile.email})", 40f, footerY, footerPaint)
        canvas.drawText("ডিজিটাল হিসাব খাতা", 460f, footerY, footerPaint)

        pdfDocument.finishPage(page)

        // Save file
        return try {
            val docsDir = File(context.cacheDir, "shared_docs")
            if (!docsDir.exists()) docsDir.mkdirs()
            val fileName = "Tally_${customer.name.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
            val file = File(docsDir, fileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun sharePdf(context: Context, file: File, title: String = "হিসাব বিবরণী শেয়ার") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "টালিখাতা থেকে হিসাব বিবরণী পাঠানো হলো।")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
