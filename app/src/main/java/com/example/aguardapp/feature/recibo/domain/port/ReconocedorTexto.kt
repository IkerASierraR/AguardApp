package com.example.aguardapp.feature.recibo.domain.port

import com.example.aguardapp.feature.recibo.domain.model.TextoReconocido

// Puerto de OCR: implementado en infrastructure/ocr con Google ML Kit Text Recognition.
interface ReconocedorTexto {
    suspend fun reconocer(bytesImagen: ByteArray): Result<TextoReconocido>
}
