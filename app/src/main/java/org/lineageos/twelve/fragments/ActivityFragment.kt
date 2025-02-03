/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.twelve.fragments

import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.lineageos.twelve.R
import org.lineageos.twelve.ext.getViewProperty
import org.lineageos.twelve.ext.navigateSafe
import org.lineageos.twelve.ext.setProgressCompat
import org.lineageos.twelve.models.ActivityTab
import org.lineageos.twelve.models.Album
import org.lineageos.twelve.models.Artist
import org.lineageos.twelve.models.Audio
import org.lineageos.twelve.models.Genre
import org.lineageos.twelve.models.Playlist
import org.lineageos.twelve.models.RequestStatus
import org.lineageos.twelve.ui.recyclerview.SimpleListAdapter
import org.lineageos.twelve.ui.recyclerview.UniqueItemDiffCallback
import org.lineageos.twelve.ui.views.ActivityTabView
import org.lineageos.twelve.utils.PermissionsChecker
import org.lineageos.twelve.utils.PermissionsUtils
import org.lineageos.twelve.viewmodels.ActivityViewModel

/**
 * User activity, notifications and recommendations.
 */
class ActivityFragment : Fragment(R.layout.fragment_activity) {
    // View models
    private val viewModel by viewModels<ActivityViewModel>()

    // Views
    private val linearProgressIndicator by getViewProperty<LinearProgressIndicator>(R.id.linearProgressIndicator)
    private val noElementsLinearLayout by getViewProperty<LinearLayout>(R.id.noElementsLinearLayout)
    private val recyclerView by getViewProperty<RecyclerView>(R.id.recyclerView)
    private val composeView by getViewProperty<ComposeView>(R.id.compose_view)

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_activity, container, false)
//
//        return view
//    }

    // RecyclerView
    private val adapter by lazy {
        object : SimpleListAdapter<ActivityTab, ActivityTabView>(
            UniqueItemDiffCallback(),
            ::ActivityTabView,
        ) {
            override fun ViewHolder.onPrepareView() {
                view.setOnItemClickListener { items, position ->
                    when (val item = items[position]) {
                        is Album -> findNavController().navigateSafe(
                            R.id.action_mainFragment_to_fragment_album,
                            AlbumFragment.createBundle(item.uri)
                        )

                        is Artist -> findNavController().navigateSafe(
                            R.id.action_mainFragment_to_fragment_artist,
                            ArtistFragment.createBundle(item.uri)
                        )

                        is Audio -> viewModel.playAudio(listOf(item), 0)

                        is Genre -> findNavController().navigateSafe(
                            R.id.action_mainFragment_to_fragment_genre,
                            GenreFragment.createBundle(item.uri)
                        )

                        is Playlist -> findNavController().navigateSafe(
                            R.id.action_mainFragment_to_fragment_playlist,
                            PlaylistFragment.createBundle(item.uri)
                        )
                    }
                }

                view.setOnItemLongClickListener { items, position ->
                    items[position].let {
                        findNavController().navigateSafe(
                            R.id.action_mainFragment_to_fragment_media_item_bottom_sheet_dialog,
                            MediaItemBottomSheetDialogFragment.createBundle(
                                it.uri, it.mediaType
                            )
                        )
                    }
                    true
                }
            }

            override fun ViewHolder.onBindView(item: ActivityTab) {
                view.setActivityTab(item)
            }
        }
    }

    // Permissions
    private val permissionsChecker = PermissionsChecker(
        this, PermissionsUtils.mainPermissions
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    val activityComposeState by viewModel.uiState.collectAsState()
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = activityComposeState.textFieldValue,
                            onValueChange = {
                                viewModel.setTextFieldText(it)
                                viewModel.setVideoId(it)
                            },
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Search,
                                    "Search icon"
                                )
                            },
                            shape = RoundedCornerShape(50),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {

                                viewModel.descrambleUrl(activityComposeState.videoId)
                            })
                        )
                        Text(activityComposeState.videoId)
                        Button(
                            onClick = { viewModel.getBaseJs() }
                        ) {
                            Text("basejs")
                        }
                        Button(
                            onClick = { viewModel.getSigSc() }
                        ) {
                            Text("get sig")
                        }
                        Button(
                            onClick = { viewModel.getNsigSc() }
                        ) {
                            Text("get nsig")
                        }
                    }
                }
            }

        }

        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                permissionsChecker.withPermissionsGranted {
                    loadData()
                }
            }
        }
    }

    override fun onDestroyView() {
        recyclerView.adapter = null

        super.onDestroyView()
    }

    private suspend fun loadData() {
        viewModel.activity.collectLatest {
            linearProgressIndicator.setProgressCompat(it, true)

            when (it) {
                is RequestStatus.Loading -> {
                    // Do nothing
                }

                is RequestStatus.Success -> {
                    val data = it.data

                    adapter.submitList(data)

                    val isEmpty = it.data.isEmpty()
                    recyclerView.isVisible = !isEmpty
                    noElementsLinearLayout.isVisible = isEmpty
                }

                is RequestStatus.Error -> {
                    Log.e(LOG_TAG, "Failed to load activity, error: ${it.error}", it.throwable)

                    recyclerView.isVisible = false
                    noElementsLinearLayout.isVisible = true
                }
            }
        }
    }

    companion object {
        private val LOG_TAG = ActivityFragment::class.simpleName!!
    }
}
