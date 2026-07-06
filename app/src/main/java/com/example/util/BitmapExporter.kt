package com.example.util

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.example.data.InvitationEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object BitmapExporter {
    private const val TAG = "BitmapExporter"

    enum class ExportQuality(val width: Int, val height: Int) {
        LOW(600, 900),
        MEDIUM(1080, 1620),
        HIGH(2000, 3000)
    }

    /**
     * Programmatically draws the invitation card content onto a Bitmap.
     */
    fun renderInvitationToBitmap(
        context: Context,
        entity: InvitationEntity,
        quality: ExportQuality = ExportQuality.MEDIUM
    ): Bitmap {
        val width = quality.width
        val height = quality.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background
        val bgImagePreset = entity.bgImagePreset
        val bgGradStart = entity.bgGradientStart
        val bgGradEnd = entity.bgGradientEnd

        if (bgImagePreset != null && bgImagePreset != "None") {
            // Preset backgrounds
            drawPresetBackground(canvas, width, height, bgImagePreset)
        } else if (bgGradStart != null && bgGradEnd != null) {
            // Gradient background
            val paint = Paint().apply { isAntiAlias = true }
            val shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                bgGradStart.toInt() or 0xFF000000.toInt(),
                bgGradEnd.toInt() or 0xFF000000.toInt(),
                Shader.TileMode.CLAMP
            )
            paint.shader = shader
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        } else {
            // Solid color (default elegant dark blue slate)
            canvas.drawColor(Color.parseColor("#121824"))
        }

        // Draw elegant default frame borders
        drawElegantBorder(canvas, width, height)

        // 2. Draw Texts from Json
        try {
            val textsArray = JSONArray(entity.textsJson)
            for (i in 0 until textsArray.length()) {
                val textObj = textsArray.getJSONObject(i)
                val text = textObj.optString("text", "")
                if (text.isEmpty()) continue

                // Normalize positions (usually in percentage 0f..1f of editing canvas)
                val relX = textObj.optDouble("x", 0.5).toFloat()
                val relY = textObj.optDouble("y", 0.5).toFloat()
                val x = relX * width
                val y = relY * height

                val sizePercent = textObj.optDouble("size", 24.0).toFloat()
                // base editor has width ~400, scale text size relative to exported width
                val finalSize = (sizePercent / 400f) * width

                val colorLong = textObj.optLong("color", 0xFFFFFFFF)
                val isBold = textObj.optBoolean("isBold", false)
                val isItalic = textObj.optBoolean("isItalic", false)
                val isUnderline = textObj.optBoolean("isUnderline", false)
                val opacity = textObj.optDouble("opacity", 1.0).toFloat()
                val align = textObj.optString("textAlignment", "Center")
                val fontName = textObj.optString("fontFamily", "serif")

                val paint = Paint().apply {
                    isAntiAlias = true
                    textSize = finalSize
                    color = colorLong.toInt() or (0xFF000000.toInt())
                    alpha = (opacity * 255).toInt().coerceIn(0, 255)
                    textAlign = when (align) {
                        "Left" -> Paint.Align.LEFT
                        "Right" -> Paint.Align.RIGHT
                        else -> Paint.Align.CENTER
                    }
                    
                    // Font Family Mapping
                    val style = when {
                        isBold && isItalic -> Typeface.BOLD_ITALIC
                        isBold -> Typeface.BOLD
                        isItalic -> Typeface.ITALIC
                        else -> Typeface.NORMAL
                    }
                    
                    typeface = when (fontName) {
                        "SansSerif" -> Typeface.create(Typeface.SANS_SERIF, style)
                        "Monospace" -> Typeface.create(Typeface.MONOSPACE, style)
                        "Cursive" -> Typeface.create("serif", style) // cursive fallback
                        else -> Typeface.create(Typeface.SERIF, style)
                    }

                    if (isUnderline) {
                        isUnderlineText = true
                    }
                    
                    // Shadow layer
                    setShadowLayer(5f, 2f, 2f, Color.BLACK)
                }

                canvas.drawText(text, x, y, paint)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error drawing text layers: ${e.message}")
            // Draw baseline information if custom layer parsing failed
            val paint = Paint().apply {
                color = Color.WHITE
                textSize = width * 0.05f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(entity.title, width * 0.5f, height * 0.2f, paint)
            canvas.drawText(entity.eventName, width * 0.5f, height * 0.35f, paint)
            canvas.drawText("Host: ${entity.hostName}", width * 0.5f, height * 0.45f, paint)
            canvas.drawText("${entity.date} | ${entity.time}", width * 0.5f, height * 0.55f, paint)
            canvas.drawText("At: ${entity.venue}", width * 0.5f, height * 0.65f, paint)
            if (entity.rsvp.isNotEmpty()) {
                canvas.drawText("RSVP: ${entity.rsvp}", width * 0.5f, height * 0.8f, paint)
            }
        }

        // 3. Draw Stickers
        try {
            val stickersArray = JSONArray(entity.stickersJson)
            for (i in 0 until stickersArray.length()) {
                val stickerObj = stickersArray.getJSONObject(i)
                val type = stickerObj.optString("type", "Heart")
                val relX = stickerObj.optDouble("x", 0.5).toFloat()
                val relY = stickerObj.optDouble("y", 0.5).toFloat()
                val scale = stickerObj.optDouble("scale", 1.0).toFloat()
                val rotation = stickerObj.optDouble("rotation", 0.0).toFloat()
                
                val x = relX * width
                val y = relY * height

                // Size of sticker relative to width
                val stickerSize = width * 0.15f * scale

                canvas.save()
                canvas.translate(x, y)
                canvas.rotate(rotation)

                val paint = Paint().apply {
                    isAntiAlias = true
                    color = Color.parseColor("#E0A96D") // Gold sticker accent
                    style = Paint.Style.FILL
                }

                drawStickerVector(canvas, type, stickerSize, paint)
                canvas.restore()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error drawing stickers: ${e.message}")
        }

        // 4. Draw QR Code if enabled
        val qrData = entity.qrCodeData
        if (!qrData.isNullOrEmpty()) {
            val qrSize = width * 0.18f
            val qrX = width * 0.5f - qrSize * 0.5f
            val qrY = height * 0.85f - qrSize * 0.5f
            drawQrOnCanvas(canvas, qrData, qrX, qrY, qrSize)
        }

        return bitmap
    }

    private fun drawPresetBackground(canvas: Canvas, width: Int, height: Int, preset: String) {
        val paint = Paint().apply { isAntiAlias = true }
        when (preset) {
            "Luxury" -> {
                // Royal deep crimson with gold gradient
                canvas.drawColor(Color.parseColor("#3B0918"))
                paint.color = Color.parseColor("#26040F")
                canvas.drawRect(width * 0.08f, height * 0.08f, width * 0.92f, height * 0.92f, paint)
            }
            "Floral" -> {
                // Soft pastel blush
                canvas.drawColor(Color.parseColor("#FFF5F5"))
                paint.color = Color.parseColor("#FFE4E6")
                canvas.drawCircle(0f, 0f, width * 0.4f, paint)
                canvas.drawCircle(width.toFloat(), height.toFloat(), width * 0.4f, paint)
            }
            "Golden" -> {
                // Warm deep charcoal with dark gold accents
                canvas.drawColor(Color.parseColor("#1B1E23"))
                val gradient = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
                    Color.parseColor("#2A1E08"), Color.parseColor("#121417"), Shader.TileMode.CLAMP)
                paint.shader = gradient
                canvas.drawPaint(paint)
            }
            "Watercolor" -> {
                // Soft gradient splash
                val gradient = RadialGradient(
                    width * 0.5f, height * 0.5f, height * 0.6f,
                    Color.parseColor("#F5EFFF"), Color.parseColor("#E6F4F8"), Shader.TileMode.CLAMP
                )
                paint.shader = gradient
                canvas.drawPaint(paint)
            }
            "Minimal" -> {
                // Off-white minimal slate
                canvas.drawColor(Color.parseColor("#FAFAFA"))
            }
            "Nature" -> {
                // Forest sage green
                canvas.drawColor(Color.parseColor("#F0F4F1"))
                paint.color = Color.parseColor("#DDE7E1")
                canvas.drawCircle(width * 0.5f, height * 1.1f, height * 0.3f, paint)
            }
            "Marble" -> {
                // Elegant grey marble simulation
                canvas.drawColor(Color.parseColor("#ECEFF1"))
                paint.color = Color.parseColor("#CFD8DC")
                paint.strokeWidth = 3f
                paint.style = Paint.Style.STROKE
                // Draw some marble veins
                canvas.drawLine(0f, height * 0.2f, width * 0.4f, height * 0.4f, paint)
                canvas.drawLine(width * 0.4f, height * 0.4f, width * 0.6f, height * 0.9f, paint)
                canvas.drawLine(width.toFloat(), height * 0.5f, width * 0.5f, height * 0.8f, paint)
            }
            else -> {
                canvas.drawColor(Color.parseColor("#1E293B"))
            }
        }
    }

    private fun drawElegantBorder(canvas: Canvas, width: Int, height: Int) {
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#D4AF37") // Metallic gold
            style = Paint.Style.STROKE
            strokeWidth = width * 0.006f
        }
        
        // Outer border margin
        val margin = width * 0.04f
        canvas.drawRect(margin, margin, width - margin, height - margin, paint)

        // Inner decorative corner lines
        val cSize = width * 0.08f
        paint.strokeWidth = width * 0.003f
        
        // Top-left
        canvas.drawLine(margin + cSize, margin + cSize, margin + cSize, margin, paint)
        canvas.drawLine(margin + cSize, margin + cSize, margin, margin + cSize, paint)

        // Top-right
        canvas.drawLine(width - margin - cSize, margin + cSize, width - margin - cSize, margin, paint)
        canvas.drawLine(width - margin - cSize, margin + cSize, width - margin, margin + cSize, paint)

        // Bottom-left
        canvas.drawLine(margin + cSize, height - margin - cSize, margin + cSize, height - margin, paint)
        canvas.drawLine(margin + cSize, height - margin - cSize, margin, height - margin - cSize, paint)

        // Bottom-right
        canvas.drawLine(width - margin - cSize, height - margin - cSize, width - margin - cSize, height - margin, paint)
        canvas.drawLine(width - margin - cSize, height - margin - cSize, width - margin, height - margin - cSize, paint)
    }

    private fun drawStickerVector(canvas: Canvas, type: String, size: Float, paint: Paint) {
        val half = size / 2f
        when (type) {
            "Heart" -> {
                val path = Path()
                path.moveTo(0f, -half * 0.4f)
                // bezier left hump
                path.cubicTo(-half, -half, -half, half * 0.5f, 0f, half)
                // bezier right hump
                path.cubicTo(half, half * 0.5f, half, -half, 0f, -half * 0.4f)
                canvas.drawPath(path, paint)
            }
            "Star" -> {
                val path = Path()
                val points = 5
                val outerRadius = half
                val innerRadius = half * 0.4f
                var angle = -Math.PI / 2
                val inc = Math.PI / points
                
                path.moveTo(
                    (Math.cos(angle) * outerRadius).toFloat(),
                    (Math.sin(angle) * outerRadius).toFloat()
                )
                for (i in 0 until points * 2) {
                    angle += inc
                    val r = if (i % 2 == 0) innerRadius else outerRadius
                    path.lineTo(
                        (Math.cos(angle) * r).toFloat(),
                        (Math.sin(angle) * r).toFloat()
                    )
                }
                path.close()
                canvas.drawPath(path, paint)
            }
            "Balloons" -> {
                // Two circles and a small knot
                paint.color = Color.parseColor("#FF6B6B")
                canvas.drawCircle(-size * 0.15f, -size * 0.1f, size * 0.25f, paint)
                paint.color = Color.parseColor("#4DABF7")
                canvas.drawCircle(size * 0.15f, -size * 0.15f, size * 0.25f, paint)
                
                // Balloon strings
                val linePaint = Paint().apply {
                    color = Color.LTGRAY
                    strokeWidth = 2f
                    style = Paint.Style.STROKE
                    isAntiAlias = true
                }
                canvas.drawLine(-size * 0.15f, size * 0.15f, 0f, size * 0.5f, linePaint)
                canvas.drawLine(size * 0.15f, size * 0.1f, 0f, size * 0.5f, linePaint)
            }
            "Cake" -> {
                // Simple birthday cake
                val r = RectF(-half * 0.8f, -half * 0.2f, half * 0.8f, half * 0.7f)
                canvas.drawRect(r, paint) // Base
                
                // Icing layer
                val paintIcing = Paint(paint).apply { color = Color.parseColor("#FFF0F6") }
                canvas.drawRect(-half * 0.82f, -half * 0.2f, half * 0.82f, -half * 0.1f, paintIcing)

                // Candle
                val candlePaint = Paint(paint).apply { color = Color.parseColor("#FAB005") }
                canvas.drawRect(-size * 0.04f, -half * 0.6f, size * 0.04f, -half * 0.2f, candlePaint)
                // Flame
                val flamePaint = Paint(paint).apply { color = Color.parseColor("#FF922B") }
                canvas.drawCircle(0f, -half * 0.75f, size * 0.06f, flamePaint)
            }
            "Rings" -> {
                // Intersecting Wedding Rings
                val ringPaint = Paint().apply {
                    isAntiAlias = true
                    color = Color.parseColor("#FCC419")
                    style = Paint.Style.STROKE
                    strokeWidth = size * 0.12f
                }
                canvas.drawCircle(-size * 0.2f, 0f, size * 0.3f, ringPaint)
                canvas.drawCircle(size * 0.2f, 0f, size * 0.3f, ringPaint)
            }
            "Flowers" -> {
                // 5 petal blossom
                canvas.drawCircle(0f, 0f, size * 0.15f, paint) // center
                val petalPaint = Paint(paint).apply { color = Color.parseColor("#F06595") }
                for (a in 0..4) {
                    val angle = a * (360f / 5) * (Math.PI / 180)
                    val px = (Math.cos(angle) * size * 0.3f).toFloat()
                    val py = (Math.sin(angle) * size * 0.3f).toFloat()
                    canvas.drawCircle(px, py, size * 0.16f, petalPaint)
                }
            }
            "Lantern" -> {
                // Islamic Lantern shape
                val p = Path()
                p.moveTo(0f, -half)
                p.lineTo(half * 0.4f, -half * 0.6f)
                p.lineTo(half * 0.4f, half * 0.4f)
                p.lineTo(0f, half)
                p.lineTo(-half * 0.4f, half * 0.4f)
                p.lineTo(-half * 0.4f, -half * 0.6f)
                p.close()
                canvas.drawPath(p, paint)
                
                // hanging hanger
                val p2 = Paint(paint).apply { style = Paint.Style.STROKE; strokeWidth = 3f }
                canvas.drawCircle(0f, -half * 1.1f, size * 0.1f, p2)
            }
            else -> {
                // Fallback elegant star circle
                canvas.drawCircle(0f, 0f, size * 0.2f, paint)
            }
        }
    }

    private fun drawQrOnCanvas(canvas: Canvas, text: String, x: Float, y: Float, size: Float) {
        val matrix = QrCodeGenerator.generateQrMatrix(text)
        val matrixSize = matrix.size
        val cellSize = size / matrixSize

        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(x, y, x + size, y + size, bgPaint)

        val qrPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                if (matrix[r][c]) {
                    canvas.drawRect(
                        x + c * cellSize,
                        y + r * cellSize,
                        x + (c + 1) * cellSize,
                        y + (r + 1) * cellSize,
                        qrPaint
                    )
                }
            }
        }
    }

    /**
     * Saves a bitmap to the cache directory and returns a secure FileProvider Uri.
     */
    fun saveBitmapToShareUri(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "infinity_invitation_${UUID.randomUUID()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bitmap for share: ${e.message}")
            null
        }
    }

    /**
     * Simulation of PDF Export. Creates a simulated physical card template print layout in the cache and returns its URI.
     */
    fun exportToPdfSimulation(context: Context, entity: InvitationEntity): Uri? {
        return try {
            val bitmap = renderInvitationToBitmap(context, entity, ExportQuality.HIGH)
            val cachePath = File(context.cacheDir, "documents")
            cachePath.mkdirs()
            val file = File(cachePath, "infinity_invitation_card_${UUID.randomUUID().toString().take(6)}.pdf")
            
            // For a highly-polished offline experience, we save a high-res JPG layout 
            // that represents the printable card page.
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
            stream.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting simulated PDF: ${e.message}")
            null
        }
    }
}
