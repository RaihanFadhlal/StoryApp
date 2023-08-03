package com.example.submissionappstory.viewmodeltest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.submissionappstory.data.local.repository.StoryRepository
import com.example.submissionappstory.data.remote.apiresponse.NewStoryResponse
import com.example.submissionappstory.datatest.DummyData
import com.example.submissionappstory.datatest.MainDispatcher
import com.example.submissionappstory.datatest.getOrAwait
import com.example.submissionappstory.ui.viewmodel.StoryViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryVMTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcher()

    private lateinit var viewModel: StoryViewModel

    private val token = "token"
    private val dummy = DummyData.newStory()
    private val image = DummyData.dummyImg()
    private val desc = DummyData.dummyDesc()
    private val lat = 40.7143528
    private val lon = -74.0059731
    @Mock private lateinit var repository: StoryRepository

    @Before
    fun setup() {
        viewModel = StoryViewModel(repository)
    }

    @Test
    fun addStorySuccess() = runTest {
        val expectedData = MutableLiveData<NewStoryResponse>()
        expectedData.postValue(dummy)
        `when`(repository.uploadStory(token, image, desc, lat, lon)).thenReturn(expectedData)

        val actualData = viewModel.uploadStory(token, image, desc, lat, lon).getOrAwait()
        verify(repository).uploadStory(token, image, desc, lat, lon)

        Assert.assertNotNull(actualData)
        assertEquals(actualData, expectedData.value)
    }
}