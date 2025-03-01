package com.asl_emp_mng.app.screens.settings.dataClass

data class EmployeeViewDocumentResponse(
    val status: String,
    val message: String,
    val data: List<Document>
)

data class Document(
    val id: Int,
    val document_type_id: Int,
    val employee_id: Int,
    val document_name: String,
    val file_path: String,
    val created_at: String,
    val updated_at: String,
    val document_type: DocumentType
)

data class DocumentType(
    val id: Int,
    val document_name: String,
    val status: String,
    val created_at: String,
    val updated_at: String
)
