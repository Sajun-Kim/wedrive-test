package com.wedrive.test.vo

import kotlinx.coroutines.Deferred

typealias DlResponse<T> = Deferred<DlBase<T>>
typealias DlEmptyResponse = Deferred<DlBaseEmpty>
typealias DlNullableResponse<T> = Deferred<DlNullableBase<T>>

typealias DlLoginResponse = Deferred<DlLoginBase>
typealias DlPostResponse<T> = Deferred<DlPostBase<T>>
typealias DlPostDetailResponse = Deferred<DlPostDetailBase>