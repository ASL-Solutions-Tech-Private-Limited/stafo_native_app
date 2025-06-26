package com.stafo.app.screens.expense

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.stafo.app.ASLEmpMng
import com.stafo.app.R
import com.stafo.app.base.BaseViewModel
import com.stafo.app.base.model.ErrorResponse
import com.stafo.app.screens.auth.LoginActivity
import com.stafo.app.screens.crm.dataClass.LeadCreateRequest
import com.stafo.app.screens.crm.dataClass.LeadCreateResponse
import com.stafo.app.screens.expense.dataClass.ApplyExpenseDetailRequest
import com.stafo.app.screens.expense.dataClass.DeleteExpenseFormResponse
import com.stafo.app.screens.expense.dataClass.EmpApplyExpenseResponse
import com.stafo.app.screens.expense.dataClass.EmployeeDeleteExpenseResponse
import com.stafo.app.screens.expense.dataClass.ExpenseApplyRequest
import com.stafo.app.screens.expense.dataClass.ExpenseChangeStatusRequest
import com.stafo.app.screens.expense.dataClass.ExpenseChangeStatusResponse
import com.stafo.app.screens.expense.dataClass.ExpenseFormCreateRequest
import com.stafo.app.screens.expense.dataClass.ExpenseFormCreateResponse
import com.stafo.app.screens.expense.dataClass.GetAllExpenseFormList
import com.stafo.app.screens.expense.dataClass.UpdateExpenseEmployeeRequest
import com.stafo.app.screens.expense.dataClass.UpdateExpenseResponse
import com.stafo.app.screens.expense.dataClass.ViewApplyExpenseResponse
import com.stafo.app.screens.expense.dataClass.ViewExpenseDetailsResponse
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class ExpenseViewModel : BaseViewModel() {

    private var mExpenseFormCreate: MutableLiveData<ExpenseFormCreateResponse> = MutableLiveData()

    val mExpenseFormCreateResponse: LiveData<ExpenseFormCreateResponse> get() = mExpenseFormCreate

    private var mGetAllExpenseFormList: MutableLiveData<GetAllExpenseFormList> = MutableLiveData()

    val mGetAllExpenseFormListResponse: LiveData<GetAllExpenseFormList> get() = mGetAllExpenseFormList
    private var mDeleteExpenseForm: MutableLiveData<DeleteExpenseFormResponse> = MutableLiveData()
    val mDeleteExpenseFormResponse: LiveData<DeleteExpenseFormResponse> get() = mDeleteExpenseForm


    private var mViewApplyExpense: MutableLiveData<ViewApplyExpenseResponse> = MutableLiveData()
    val mViewApplyExpenseResponse: LiveData<ViewApplyExpenseResponse> get() = mViewApplyExpense

    private var mEmpApplyExpense: MutableLiveData<EmpApplyExpenseResponse> = MutableLiveData()
    val mEmpApplyExpenseResponse: LiveData<EmpApplyExpenseResponse> get() = mEmpApplyExpense


    private var mExpenseChangeStatus: MutableLiveData<ExpenseChangeStatusResponse> =
        MutableLiveData()
    val mExpenseChangeStatusResponse: LiveData<ExpenseChangeStatusResponse> get() = mExpenseChangeStatus


    private var mViewExpenseDetails: MutableLiveData<ViewExpenseDetailsResponse> = MutableLiveData()
    val mViewExpenseDetailsResponse: LiveData<ViewExpenseDetailsResponse> get() = mViewExpenseDetails

    private var mEmployeeDeleteExpense: MutableLiveData<EmployeeDeleteExpenseResponse> =
        MutableLiveData()
    val mEmployeeDeleteExpenseResponse: LiveData<EmployeeDeleteExpenseResponse> get() = mEmployeeDeleteExpense


    private var mUpdateExpense: MutableLiveData<UpdateExpenseResponse> = MutableLiveData()
    val mUpdateExpenseResponse: LiveData<UpdateExpenseResponse> get() = mUpdateExpense


    fun employeeUpdateExpense(
        mContext: Context,
        id: Int,
        request: UpdateExpenseEmployeeRequest
    ) {
        if (!isNetworkAvailable(mContext)) return

        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callUpdateExpenseEmployee(id,request)

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mUpdateExpense.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }







    fun deleteApplyExpense(
        mContext: Context,
        id: Int
    ) {
        if (!isNetworkAvailable(mContext)) return

        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callDeleteApplyExpense(id)

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mEmployeeDeleteExpense.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun viewExpenseDetails(
        mContext: Context,
        id: Int
    ) {
        if (!isNetworkAvailable(mContext)) return

        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callExpenseDetails(id)

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mViewExpenseDetails.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }

    fun approveRejectExpense(
        mContext: Context,
        request: ExpenseChangeStatusRequest
    ) {
        if (!isNetworkAvailable(mContext)) return

        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    ASLEmpMng.instance.apiStores()?.callApproveRejectApplyExpense(request)

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mExpenseChangeStatus.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun empApplyExpense(
        mContext: Context,
        companyId: String,
        employeeId: String,
        expenseTypeId: String,
        amount: String,
        expenseDetails: List<ApplyExpenseDetailRequest>,
        attachmentFile: File?,
        isDocumentRequired: Boolean
    ) {
        if (!isNetworkAvailable(mContext)) return

        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val formDataMap = mutableMapOf<String, RequestBody>()

                formDataMap["company_id"] =
                    companyId.toRequestBody("text/plain".toMediaTypeOrNull())
                formDataMap["employee_id"] =
                    employeeId.toRequestBody("text/plain".toMediaTypeOrNull())
                formDataMap["expensetype_id"] =
                    expenseTypeId.toRequestBody("text/plain".toMediaTypeOrNull())
                formDataMap["amount"] = amount.toRequestBody("text/plain".toMediaTypeOrNull())


                expenseDetails.forEachIndexed { index, detail ->
                    val expenseFormId = detail.expenseform_id.toString()
                    val expenseValue = detail.expense_value.toString()

                    if (expenseValue.isBlank()) {
                        withContext(Dispatchers.Main) {
                            getLoaderLiveData().value = "stop"
                            CustomToast(mContext, "Expense value for item $index is empty")
                        }
                        return@launch
                    }

                    formDataMap["expense_details[$index][expenseform_id]"] =
                        expenseFormId.toRequestBody("text/plain".toMediaTypeOrNull())
                    formDataMap["expense_details[$index][expense_value]"] =
                        expenseValue.toRequestBody("text/plain".toMediaTypeOrNull())
                }

                val attachmentParts = mutableListOf<MultipartBody.Part>()
                if (isDocumentRequired && attachmentFile != null) {
                    val mimeType = when {
                        attachmentFile.name.endsWith(".pdf") -> "application/pdf"
                        attachmentFile.name.endsWith(".doc") || attachmentFile.name.endsWith(".docx") -> "application/msword"
                        else -> "image/jpeg"
                    }
                    val fileRequestBody = attachmentFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData(
                        "attachments[]",
                        attachmentFile.name,
                        fileRequestBody
                    )
                    attachmentParts.add(filePart)
                }

                val response =
                    ASLEmpMng.instance.apiStores()?.callApplyExpense(formDataMap, attachmentParts)

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mEmpApplyExpense.postValue(it.body())
                        } else {
                            val errorBody = it.errorBody()?.string()
                            try {
                                val json = JSONObject(errorBody ?: "")
                                val message = json.optString("message")
                                val errors = json.optJSONObject("errors")
                                val firstError =
                                    errors?.keys()?.asSequence()?.firstOrNull()?.let { key ->
                                        errors.optJSONArray(key)?.optString(0)
                                    }
                                val errorMessage =
                                    if (!firstError.isNullOrEmpty()) "$message\n$firstError" else message
                                CustomToast(mContext, errorMessage)
                            } catch (e: Exception) {
                                CustomToast(
                                    mContext,
                                    errorBody
                                        ?: mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        Log.d("REQ", "error 1: ")
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    Log.e("REQ", "Exception: ${e.localizedMessage}", e)
                    CustomToast(
                        mContext,
                        "Something went wrong: ${e.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
    }


    fun empApplyExpenseWithoutAttach(
        mContext: Context,
        request: ExpenseApplyRequest
    ) {
        if (!isNetworkAvailable(mContext)) return

        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    ASLEmpMng.instance.apiStores()?.callApplyExpenseWithoutAttach(request)

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mEmpApplyExpense.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getApplyExpenseList(
        mContext: Context,
        companyId: String? = null,
        employeeId: String? = null
    ) {
        if (!isNetworkAvailable(mContext)) return

        getLoaderLiveData().value = "load"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()
                    ?.callGetApplyExpenseList(companyId = companyId, employeeId = employeeId)

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mViewApplyExpense.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(
                        mContext,
                        mContext.getString(R.string.error_something_went_wrong)
                    )
                }
            }
        }
    }


    fun deleteExpenseForm(mContext: Context, id: Int) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callExpenseFormDelete(id)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mDeleteExpenseForm.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun getAllExpenseFormList(mContext: Context, companyId: String) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callGetAllExpenseFormList(companyId)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mGetAllExpenseFormList.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun updateExpenseForm(mContext: Context, id: Int, request: ExpenseFormCreateRequest) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callExpenseFormUpdate(id, request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mExpenseFormCreate.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


    fun expenseFormCreate(mContext: Context, request: ExpenseFormCreateRequest) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callExpenseFormCreate(request)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mExpenseFormCreate.postValue(it.body())
                        } else {
                            it.errorBody()?.charStream()?.let { errorStream ->
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                CustomToast(mContext as LoginActivity, error?.message ?: "")
                            } ?: run {
                                CustomToast(
                                    mContext,
                                    mContext.getString(R.string.error_something_went_wrong)
                                )
                            }
                        }
                    } ?: run {
                        CustomToast(
                            mContext,
                            mContext.getString(R.string.error_something_went_wrong)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                }
            }
        }
    }


}