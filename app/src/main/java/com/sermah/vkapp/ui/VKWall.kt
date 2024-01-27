package com.sermah.vkapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sermah.vkapp.ui.data.Post
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@Composable
fun VKWall(
    posts: SnapshotStateList<Post>,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        state = rememberLazyListState().also { state ->
            LaunchedEffect(key1 = state) {
                snapshotFlow { state.layoutInfo }
                    .map {
                        it.totalItemsCount == 0 ||
                            it.totalItemsCount ==
                            (it.visibleItemsInfo.lastOrNull()?.index ?: -1).inc()
                    }
                    .distinctUntilChanged()
                    .filter { it }
                    .onEach { onLoadMore() }
                    .collect {}
            }
        }
    ) {
        items(posts) { post ->
            VKPost(
                post = post,
                onLike = {},
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}