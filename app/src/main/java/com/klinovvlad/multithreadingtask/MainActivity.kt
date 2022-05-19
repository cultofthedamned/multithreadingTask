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
    private var thread: Thread? = null
    private var observable: Disposable? = null
    private var job: Job? = null

    private val mutableLiveData = MutableLiveData<Int>()
    private val liveData: LiveData<Int>
        get() = mutableLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainActivityRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = mainAdapter
        }

        // liveDataMethod()
        // rxJavaMethod()
        // coroutineMethod()
    }

    override fun onStop() {
        super.onStop()
        thread?.interrupt()
        Log.i("onStop", thread?.isInterrupted.toString())
        observable?.dispose()
        Log.i("onStop", observable?.isDisposed.toString())
        job?.cancel()
        Log.i("onStop", job?.isCancelled.toString())
    }

    private fun liveDataMethod() {
        thread = Thread {
            runOnUiThread {
                liveData.observe(this) {
                    mainAdapter.addNumber(it)
                }
            }
            for (n in 0..15) {
                val i = Random.nextInt()
                mutableLiveData.postValue(i)
                Thread.sleep(1000)
                continue
            }
        }
        thread?.start()
    }

    private fun rxJavaMethod() {
        observable = Observable.interval(1, TimeUnit.SECONDS)
            .map { Random.nextInt() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({next: Int ->
                mainAdapter.addNumber(next)},
                { error: Throwable -> Log.i("myRxJava", error.message.toString()) })
    }

    private fun coroutineMethod() {
        job = lifecycleScope.launch(Dispatchers.Main) {
            myFlow().collect { value -> mainAdapter.addNumber(value) }
        }
    }

    private fun myFlow(): Flow<Int> = flow {
        for (n in 0..15) {
            delay(1000)
            emit(Random.nextInt())
        }
    }

}