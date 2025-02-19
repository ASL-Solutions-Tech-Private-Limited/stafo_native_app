package com.asl_emp_mng.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class CompanyUpdateDocumentResponse(
    val status: Boolean,
    val message: String,
    val data: DocumentData
)

data class DocumentData(
    @SerializedName("company_document") val companyDocument: CompanyDocument,
    @SerializedName("document_url") val documentUrl: String
)

data class CompanyDocument(
    val id: Int,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("document_type_id") val documentTypeId: String,
    val document: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

