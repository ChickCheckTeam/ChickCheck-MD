package com.example.chickcheckapp.data

import com.example.chickcheckapp.data.remote.RemoteDataSource
import javax.inject.Inject

class ChickCheckRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
}