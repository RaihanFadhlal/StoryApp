package com.example.submissionappstory.data.local.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.submissionappstory.data.remote.apiresponse.StoryResponse
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import com.example.submissionappstory.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsRepository {

    private val _mapStory = MutableLiveData<List<ListStory>>()

    fun getStoryLocation(token : String): LiveData<List<ListStory>> {
        ApiConfig.getApiService().getStoriesLocation(token, 1)
            .enqueue(object : Callback<StoryResponse> {
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    if (response.isSuccessful) {
                        _mapStory.postValue(response.body()?.listStory)
                    }
                }
                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                }
            })
        return _mapStory
    }

    fun getStory(): LiveData<List<ListStory>> {
        return _mapStory
    }
}