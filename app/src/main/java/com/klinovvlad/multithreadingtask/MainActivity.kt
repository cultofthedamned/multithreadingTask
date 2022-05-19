package com.klinovvlad.multithreadingtask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.klinovvlad.multithreadingtask.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainAdapter: MainAdapter by lazy {
        MainAdapter()
    }

    private val mutableLiveData = MutableLiveData<Int>()
    private val liveData: LiveData<Int>
        get() = mutableLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // liveDataMethod().start()
        // liveDataMethodUpdateUi().start()
        // rxJavaMethod()
        // coroutineMethod()
    }

    override fun onStop() {
        super.onStop()
        liveDataMethod().stop()
        liveDataMethodUpdateUi().stop()
        rxJavaMethod()?.dispose()
        coroutineMethod().cancel()
    }

    private fun liveDataMethod(): Thread {
        val thread = Thread {
            for (n in 0..15) {
                val i = Random.nextInt()
                mutableLiveData.postValue(i)
                Thread.sleep(1000)
            }
        }
        return thread
    }

    private fun liveDataMethodUpdateUi(): Thread {
        val thread = Thread {
            runOnUiThread {
                binding.mainActivityRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    setHasFixedSize(true)
                    adapter = mainAdapter
                }
                liveData.observe(this) {
                    mainAdapter.addNumber(it)
                }
            }
        }
        return thread
    }

    private fun rxJavaMethod(): Disposable? {
        val observable = Observable.interval(1, TimeUnit.SECONDS)
            .map { Random.nextInt() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({next: Int ->
                binding.mainActivityRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                setHasFixedSize(true)
                adapter = mainAdapter
            }
                mainAdapter.addNumber(next)},
                { error: Throwable -> Log.i("myRxJava", error.message.toString()) })
        return observable
    }

    private fun coroutineMethod(): Job {
        val job: Job = lifecycleScope.launch(Dispatchers.Main) {
            binding.mainActivityRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                setHasFixedSize(true)
                adapter = mainAdapter
            }
            myFlow().collect { value -> mainAdapter.addNumber(value) }
        }
        return job
    }

    private fun myFlow(): Flow<Int> = flow {
        for (n in 0..15) {
            delay(1000)
            emit(Random.nextInt())
        }
    }

}