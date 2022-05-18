package com.klinovvlad.multithreadingtask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.klinovvlad.multithreadingtask.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.Observer
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

    private val mutableLiveData = MutableLiveData<Int>()
    private val liveData: LiveData<Int>
        get() = mutableLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // liveDataMethod()
        // rxJavaMethod()
        // coroutineMethod()
    }

    private fun liveDataMethod() {
        Thread {
            for (n in 0..15) {
                val i = Random.nextInt()
                mutableLiveData.postValue(i)
                runOnUiThread {
                    liveData.observe(this) {
                        binding.number.text = it.toString()
                    }
                }
                Thread.sleep(1000)
            }
        }.start()
    }

    private fun rxJavaMethod() {
        Observable.interval(1, TimeUnit.SECONDS)
            .map { Random.nextInt() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {
                    Log.i("myRxJava", "onSubscribe")
                }

                override fun onNext(t: Int) {
                    binding.number.text = t.toString()
                }

                override fun onError(e: Throwable) {
                    Log.i("myRxJava", e.message.toString())
                }

                override fun onComplete() {
                    Log.i("myRxJava", "completed")
                }

            })
    }

    private fun coroutineMethod() {
        GlobalScope.launch(Dispatchers.Main) {
            myFlow().collect { value -> binding.number.text = value.toString() }
        }
    }

    private fun myFlow(): Flow<Int> = flow {
        for (n in 0..15) {
            delay(1000)
            emit(Random.nextInt())
        }
    }

}