package com.stafo.app.screens.settings.dataClass

data class EmployeeDocumentUploadResponse(
    val status: Boolean,
    val message: String,
    val documents: List<DocumentItem>
)

data class DocumentItem(
    val document: DocumentDetails,
    val file_url: String
)

data class DocumentDetails(
    val employee_id: String,
    val document_type_id: String,
    val document_name: String,
    val file_path: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)
