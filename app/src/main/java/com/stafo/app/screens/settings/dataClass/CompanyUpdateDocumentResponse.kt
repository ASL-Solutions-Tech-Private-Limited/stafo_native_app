package com.stafo.app.screens.settings.dataClass

import com.google.gson.annotations.SerializedName

data class CompanyUpdateDocumentResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: DocumentData
)

data class DocumentData(
    @SerializedName("company_document") val companyDocument: CompanyDocument,
    @SerializedName("document_url") val documentUrl: String
)

data class CompanyDocument(
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("document_type_id") val documentTypeId: String,
    @SerializedName("document") val document: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("id") val id: Int
)
