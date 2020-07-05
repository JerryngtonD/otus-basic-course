package ru.otus.cineman.data.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ImageService {
    @Streaming
    @GET
    fun downloadImage(@Url imageUrl: String): Observable<Response<ResponseBody>>
}