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
import com.stafo.app.screens.expense.dataClass.ExpenseFormCreateRequest
import com.stafo.app.screens.expense.dataClass.ExpenseFormCreateResponse
import com.stafo.app.screens.expense.dataClass.GetAllExpenseFormList
import com.stafo.app.screens.expense.dataClass.ViewApplyExpenseResponse
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
                // Convert primitive fields to RequestBody
                val companyIdBody = companyId.toRequestBody("text/plain".toMediaTypeOrNull())
                val employeeIdBody = employeeId.toRequestBody("text/plain".toMediaTypeOrNull())
                val expenseTypeIdBody = expenseTypeId.toRequestBody("text/plain".toMediaTypeOrNull())
                val amountBody = amount.toRequestBody("text/plain".toMediaTypeOrNull())

                // Convert expenseDetails list to JSON and pass as application/json
                val expenseDetailsJson = Gson().toJson(expenseDetails)
                Log.e("JSON_PAYLOAD", expenseDetailsJson)

                val expenseDetailsBody = expenseDetailsJson.toRequestBody("application/json".toMediaTypeOrNull())

                // Prepare optional attachment
                val attachmentPart: MultipartBody.Part? = if (isDocumentRequired && attachmentFile != null) {
                    val mimeType = when {
                        attachmentFile.name.endsWith(".pdf") -> "application/pdf"
                        attachmentFile.name.endsWith(".doc") || attachmentFile.name.endsWith(".docx") -> "application/msword"
                        else -> "image/*"
                    }

                    val requestFile = attachmentFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("attachments[0]", attachmentFile.name, requestFile)
                } else {
                    null
                }

                // API call
                val response = ASLEmpMng.instance.apiStores()?.callApplyExpense(
                    companyIdBody,
                    employeeIdBody,
                    amountBody,
                    expenseTypeIdBody,
                    expenseDetailsBody,
                    attachmentPart
                )

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
                                CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
                            }
                        }
                    } ?: run {
                        CustomToast(mContext, mContext.getString(R.string.error_something_went_wrong))
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







    fun deleteExpenseForm(mContext: Context,id:Int) {
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






    fun getAllExpenseFormList(mContext: Context, companyId:String) {
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



    fun updateExpenseForm(mContext: Context,id:Int, request: ExpenseFormCreateRequest) {
        if (!isNetworkAvailable(mContext)) {
            return
        }
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callExpenseFormUpdate(id,request)
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