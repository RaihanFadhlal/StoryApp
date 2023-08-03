package com.example.submissionappstory.viewmodeltest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.submissionappstory.PagingSource
import com.example.submissionappstory.data.local.repository.MainRepository
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import com.example.submissionappstory.datatest.DummyData
import com.example.submissionappstory.datatest.MainDispatcher
import com.example.submissionappstory.datatest.getOrAwait
import com.example.submissionappstory.ui.adapter.StoryAdapter.Companion.DIFF_CALLBACK
import com.example.submissionappstory.ui.viewmodel.MainViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
class MainVMTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcher()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var mainviewModel : MainViewModel
    @Mock private lateinit var mainRepo: MainRepository

    @Before
    fun setup(){
        mainviewModel = MainViewModel(mainRepo)
    }

    @Test
    fun getStoryReturnSuccess() = runTest {
        val dummy = DummyData.dummyStory()
        val data = PagingSource.snapShot(dummy)

        val expectedData = MutableLiveData<PagingData<ListStory>>()
        expectedData.value = data

        `when`(mainviewModel.getStory()).thenReturn(expectedData)
        mainviewModel.getStory()

        val actualData = mainviewModel.getStory().value

        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualData!!)

        assertNotNull(differ.snapshot())
        assertEquals(dummy, differ.snapshot().items)
        assertEquals(dummy.size, differ.snapshot().size)
        assertEquals(dummy[0], differ.snapshot().items[0])
    }

    @Test
    fun getStoryWithNoDataReturnSuccess() = runTest {
        val dummy = DummyData.dummyStoryNull
        val data = PagingSource.snapShot(dummy)
        val expectedData = MutableLiveData<PagingData<ListStory>>()
        expectedData.value = data

        `when`(mainviewModel.getStory()).thenReturn(expectedData)
        mainviewModel.getStory()

        val actualData = mainviewModel.getStory().getOrAwait()

        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualData)

        Assert.assertEquals(0, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
        }

        override fun onRemoved(position: Int, count: Int) {
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
        }
    }
}