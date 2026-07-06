package com.example.util

import kotlin.math.abs

object QrCodeGenerator {
    /**
     * Generates a 21x21 grid representing a QR code.
     * Finder patterns (the three standard positioning squares) are explicitly drawn,
     * and the remaining data bits are filled procedurally based on the input text hash.
     */
    fun generateQrMatrix(text: String): Array<BooleanArray> {
        val size = 21
        val matrix = Array(size) { BooleanArray(size) { false } }

        // 1. Draw Finder Patterns (Corners: Top-Left, Top-Right, Bottom-Left)
        drawFinderPattern(matrix, 0, 0)         // Top-Left
        drawFinderPattern(matrix, 0, size - 7)    // Top-Right
        drawFinderPattern(matrix, size - 7, 0)    // Bottom-Left

        // 2. Add some fixed QR-like elements (timing patterns, quiet zone, alignment indicators)
        // Horizontal timing pattern between finder patterns
        for (col in 8 until size - 8) {
            matrix[6][col] = col % 2 == 0
        }
        // Vertical timing pattern
        for (row in 8 until size - 8) {
            matrix[row][6] = row % 2 == 0
        }

        // 3. Fill the remaining space procedurally based on input text hash
        val textHash = text.hashCode()
        for (r in 0 until size) {
            for (c in 0 until size) {
                // Skip finder pattern zones
                if (isInsideFinderPattern(r, c, size)) {
                    continue
                }
                // Skip timing lines
                if (r == 6 && c >= 8 && c < size - 8) continue
                if (c == 6 && r >= 8 && r < size - 8) continue

                // Procedural generation using a simple deterministic generator based on the hash
                val cellSeed = (r * 313 + c * 73 + abs(textHash))
                val isFilled = (cellSeed % 5 == 0) || (cellSeed % 3 == 0 && textHash % 2 == 0)
                matrix[r][c] = isFilled
            }
        }

        // Add a small alignment block near bottom-right corner for realism
        val alignX = size - 7
        val alignY = size - 7
        for (dr in 0..4) {
            for (dc in 0..4) {
                val r = alignX + dr
                val c = alignY + dc
                if (r in 0 until size && c in 0 until size) {
                    val isEdge = dr == 0 || dr == 4 || dc == 0 || dc == 4
                    val isCenter = dr == 2 && dc == 2
                    matrix[r][c] = isEdge || isCenter
                }
            }
        }

        return matrix
    }

    private fun drawFinderPattern(matrix: Array<BooleanArray>, startRow: Int, startCol: Int) {
        for (r in 0..6) {
            for (c in 0..6) {
                // Finder pattern consists of:
                // 7x7 solid outer square
                // 5x5 hollow inner
                // 3x3 solid center
                val isOuterBorder = r == 0 || r == 6 || c == 0 || c == 6
                val isInnerCenter = (r in 2..4) && (c in 2..4)
                matrix[startRow + r][startCol + c] = isOuterBorder || isInnerCenter
            }
        }
    }

    private fun isInsideFinderPattern(row: Int, col: Int, size: Int): Boolean {
        // Finder pattern sizes are 7x7 at (0,0), (0, size-7), (size-7, 0)
        if (row < 8 && col < 8) return true // Top-Left
        if (row < 8 && col >= size - 8) return true // Top-Right
        if (row >= size - 8 && col < 8) return true // Bottom-Left
        return false
    }
}
