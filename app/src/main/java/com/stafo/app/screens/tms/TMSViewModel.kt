package com.stafo.app.screens.tms

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
import com.stafo.app.screens.tms.dataClass.AddCommentRequest
import com.stafo.app.screens.tms.dataClass.AddCommentResponse
import com.stafo.app.screens.tms.dataClass.CreateTaskRequest
import com.stafo.app.screens.tms.dataClass.CreateTaskResponse
import com.stafo.app.screens.tms.dataClass.DeleteTaskResponse
import com.stafo.app.screens.tms.dataClass.TaskCommentListResponse
import com.stafo.app.screens.tms.dataClass.TaskListRequest
import com.stafo.app.screens.tms.dataClass.TaskListResponse
import com.stafo.app.screens.tms.dataClass.TaskStatusRequest
import com.stafo.app.screens.tms.dataClass.UpdateTaskResponse
import com.stafo.app.screens.tms.dataClass.UpdateTaskStatusResponse
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.doLogout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.osmdroid.tileprovider.cachemanager.CacheManager.getFileName


class TMSViewModel : BaseViewModel() {

    private var mTaskList: MutableLiveData<TaskListResponse> = MutableLiveData()
    val mTaskListResponse: LiveData<TaskListResponse> get() = mTaskList

    private var mCreateTask: MutableLiveData<CreateTaskResponse> = MutableLiveData()
    val mCreateTaskResponse: LiveData<CreateTaskResponse> get() = mCreateTask


    private var mUpdateTask: MutableLiveData<UpdateTaskResponse> = MutableLiveData()
    val mUpdateTaskResponse: LiveData<UpdateTaskResponse> get() = mUpdateTask

    private var mDeleteTask: MutableLiveData<DeleteTaskResponse> = MutableLiveData()
    val mDeleteTaskResponse: LiveData<DeleteTaskResponse> get() = mDeleteTask

    private var mTaskCommentList: MutableLiveData<TaskCommentListResponse> = MutableLiveData()
    val mTaskCommentListResponse: LiveData<TaskCommentListResponse> get() = mTaskCommentList

    private var mAddComment: MutableLiveData<AddCommentResponse> = MutableLiveData()
    val mAddCommentResponse: LiveData<AddCommentResponse> get() = mAddComment


    private var mUpdateTaskStatus: MutableLiveData<UpdateTaskStatusResponse> = MutableLiveData()
    val mUpdateTaskStatusResponse: LiveData<UpdateTaskStatusResponse> get() = mUpdateTaskStatus



    fun attachFile(
        context: Context,
        taskId: Int,
        fileUris: List<Uri>
    ) {
        Log.e("tms", "Preparing request parts...")
        val taskIdBody = taskId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val contentResolver = context.contentResolver
        val fileParts = mutableListOf<MultipartBody.Part>()

        fileUris.forEachIndexed { index, uri ->
            Log.d("updateTask", "Processing file #$index: URI = $uri")

            val fileName = getFileNameFromUri(contentResolver, uri)
            Log.d("updateTask", "File name resolved: $fileName")

            try {
                val inputStream = contentResolver.openInputStream(uri)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                if (fileBytes != null && fileName != null) {
                    Log.d("updateTask", "File size: ${fileBytes.size} bytes")

                    val requestFile = fileBytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("files[$index]", fileName, requestFile)
                    fileParts.add(part)
                } else {
                    Log.w("updateTask", "Skipping file #$index because fileBytes or fileName is null")
                }
            } catch (e: Exception) {
                Log.e("updateTask", "Failed to read file #$index", e)
            }
        }

        getLoaderLiveData().postValue("load")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("tms", "Calling API with ${fileParts.size} files...")

                val response = ASLEmpMng.instance.apiStores()?.callAttachFile(
                    taskIdBody,
                    fileParts
                )

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"

                    if (response?.isSuccessful == true) {
                        Log.d("tms", "API success! Response: ${response.body()}")
                        mUpdateTaskStatus.postValue(response.body())
                    } else {
                        val errorMsg = response?.errorBody()?.string()
                        Log.e("tms", "API failed. Code: ${response?.code()}, Error: $errorMsg")
                        CustomToast(context, errorMsg ?: "Task update failed")
                    }
                }
            } catch (e: Exception) {
                Log.e("tms", "Exception during API call", e)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                }
            }
        }
    }















    fun changeTaskStatus(mContext: Context, id: Int, request: TaskStatusRequest) {
        getLoaderLiveData().postValue("load")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callTaskStatus(id, request)
                Log.d("TMS", "API Response changeTaskStatus: ${response?.body()}")

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"

                    if (response != null) {
                        if (response.isSuccessful) {
                            mUpdateTaskStatus.postValue(response.body())
                        } else {
                            val errorMsg = try {
                                val errorStream = response.errorBody()?.charStream()
                                val error = Gson().fromJson(errorStream, ErrorResponse::class.java)
                                error?.message ?: mContext.getString(R.string.error_something_went_wrong)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                mContext.getString(R.string.error_something_went_wrong)
                            }

                            Log.e("TMS", "API Error: $errorMsg")
                            CustomToast(mContext, errorMsg)
                        }
                    } else {
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





    fun deleteComment(mContext: Context,id:Int) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callDeleteComment(id)
                Log.d("tms", "deleteComment: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mDeleteTask.postValue(it.body())
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





    fun addComment(mContext: Context,request: AddCommentRequest) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callAddComment(request)
                Log.d("tms", "addComment: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mAddComment.postValue(it.body())
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


    fun getCommentList(mContext: Context,taskId:Int) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callCommentList(taskId)
                Log.d("tms", "getCommentList: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mTaskCommentList.postValue(it.body())
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

    fun deleteAttachFile(mContext: Context,id:Int) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callDeleteAttachFile(id)
                Log.d("tms", "deleteAttachFile: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mDeleteTask.postValue(it.body())
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

    fun deleteTask(mContext: Context,id:Int) {
        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.callDeleteTask(id)
                Log.d("tms", "deleteTask: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mDeleteTask.postValue(it.body())
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

    fun updateTask(
        context: Context,
        taskId: Int,
        request: CreateTaskRequest,
        fileUris: List<Uri>
    ) {
        Log.d("updateTask", "Preparing request parts...")

        val title = request.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val description = request.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val startDate = request.start_date.toRequestBody("text/plain".toMediaTypeOrNull())
        val endDate = request.end_date.toRequestBody("text/plain".toMediaTypeOrNull())
        val status = request.status.toRequestBody("text/plain".toMediaTypeOrNull())
        val priority = request.priority.toRequestBody("text/plain".toMediaTypeOrNull())

        val taskAssignParts = request.task_assign.mapIndexed { index, id ->
            MultipartBody.Part.createFormData("task_assign[$index]", id.toString())
        }

        val contentResolver = context.contentResolver
        val fileParts = mutableListOf<MultipartBody.Part>()

        fileUris.forEachIndexed { index, uri ->
            Log.d("updateTask", "Processing file #$index: URI = $uri")

            val fileName = getFileNameFromUri(contentResolver, uri)
            Log.d("updateTask", "File name resolved: $fileName")

            try {
                val inputStream = contentResolver.openInputStream(uri)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                if (fileBytes != null && fileName != null) {
                    Log.d("updateTask", "File size: ${fileBytes.size} bytes")

                    val requestFile = fileBytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("files[$index]", fileName, requestFile)
                    fileParts.add(part)
                } else {
                    Log.w("updateTask", "Skipping file #$index because fileBytes or fileName is null")
                }
            } catch (e: Exception) {
                Log.e("updateTask", "Failed to read file #$index", e)
            }
        }

        getLoaderLiveData().postValue("load")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("updateTask", "Calling API with ${fileParts.size} files...")

                val response = ASLEmpMng.instance.apiStores()?.callUpdateTask(
                    taskId,
                    title,
                    description,
                    startDate,
                    endDate,
                    status,
                    priority,
                    taskAssignParts,
                    fileParts
                )

                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"

                    if (response?.isSuccessful == true) {
                        Log.d("updateTask", "API success! Response: ${response.body()}")
                        mUpdateTask.postValue(response.body())
                    } else {
                        val errorMsg = response?.errorBody()?.string()
                        Log.e("updateTask", "API failed. Code: ${response?.code()}, Error: $errorMsg")
                        CustomToast(context, errorMsg ?: "Task update failed")
                    }
                }
            } catch (e: Exception) {
                Log.e("updateTask", "Exception during API call", e)
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                }
            }
        }
    }



    /* fun updateTask(mContext: Context,id:Int, request: CreateTaskRequest) {
         getLoaderLiveData().value = "load"
         viewModelScope.launch(Dispatchers.IO) {
             try {
                 val response = ASLEmpMng.instance.apiStores()?.callUpdateTask(id,request)
                 Log.d("tms", "updateTask: ${response?.body().toString()}")
                 withContext(Dispatchers.Main) {
                     getLoaderLiveData().value = "stop"
                     response?.let {
                         if (it.isSuccessful) {
                             mUpdateTask.postValue(it.body())
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
     }*/

    fun createTask(context: Context, request: CreateTaskRequest, fileUris: List<Uri>) {
        // Prepare text parts as RequestBody
        val companyId = request.company_id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val title = request.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val description = request.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val startDate = request.start_date.toRequestBody("text/plain".toMediaTypeOrNull())
        val endDate = request.end_date.toRequestBody("text/plain".toMediaTypeOrNull())
        val status = request.status.toRequestBody("text/plain".toMediaTypeOrNull())
        val priority = request.priority.toRequestBody("text/plain".toMediaTypeOrNull())

        // Prepare task assign parts
        val taskAssignParts = request.task_assign.mapIndexed { index, id ->
            MultipartBody.Part.createFormData("task_assign[$index]", id.toString())
        }

        // Prepare file parts from URIs
        val fileParts = mutableListOf<MultipartBody.Part>()
        val contentResolver = context.contentResolver

        fileUris.forEachIndexed { index, uri ->
            val fileName = getFileNameFromUri(contentResolver, uri) ?: "file_$index"
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                val requestFile = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("files[$index]", fileName, requestFile)
                fileParts.add(part)
            }
        }

        // Launch Coroutine for network call
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ASLEmpMng.instance.apiStores()?.createTask(
                    companyId,
                    title,
                    description,
                    startDate,
                    endDate,
                    status,
                    priority,
                    taskAssignParts,
                    fileParts
                )

                withContext(Dispatchers.Main) {
                    if (response?.isSuccessful == true) {
                        // Success
                        mCreateTask.postValue(response.body())
                    } else {
                        // Handle error
                        val err = response?.errorBody()?.string()
                        CustomToast(context, err ?: "Failed to create task")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    CustomToast(context, "Exception: ${e.localizedMessage}")
                }
            }
        }
    }




    fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri): String? {
        var name: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    name = it.getString(index)
                }
            }
        }
        return name
    }




    fun getTaskList(mContext: Context, comId:String) {

        getLoaderLiveData().value = "load"
        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = ASLEmpMng.instance.apiStores()?.callTaskList(comId)
                Log.d("tms", "getTaskList: ${response?.body().toString()}")
                withContext(Dispatchers.Main) {
                    getLoaderLiveData().value = "stop"
                    response?.let {
                        if (it.isSuccessful) {
                            mTaskList.postValue(it.body())
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