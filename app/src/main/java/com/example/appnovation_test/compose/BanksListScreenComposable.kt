package com.example.appnovation_test.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appnovation_test.R
import com.example.appnovation_test.extension.clickableNoRipple
import com.example.appnovation_test.model.Action
import com.example.appnovation_test.model.ActionDetail
import com.example.appnovation_test.model.ActionType
import com.example.appnovation_test.model.BankInfo
import com.example.appnovation_test.viewmodels.BanksViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BanksListScreenComposable(banksViewModel: BanksViewModel) {
    val uiState by banksViewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.banks_list_title),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding),
            isRefreshing = uiState.isLoading,
            onRefresh = {
                banksViewModel.dispatch(BanksViewModel.Action.Refresh)
            },
        ) {
            if (uiState.banks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                uiState.Composable(
                    onClickDetail = { actionType, url ->
                        banksViewModel.dispatch(BanksViewModel.Action.OnClickActionDetail(actionType,url))
                    }
                )
            }
        }

    }
}

@Composable
private fun BanksViewModel.BanksUiState.Composable(
    modifier: Modifier = Modifier,
    onClickDetail: (ActionType, String) -> Unit = {_, _ -> },
) {
    val state = this
    var maxHeight by remember { mutableFloatStateOf(0f) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(state.banks) { bank ->
            var isExpanded by rememberSaveable { mutableStateOf(false) }
            Column (
                modifier = Modifier.clickableNoRipple { isExpanded = !isExpanded },
            ){
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            val itemHeight = coordinates.size.height.toFloat()
                            if (itemHeight > maxHeight) {
                                maxHeight = itemHeight
                            }
                        }
                        .heightIn(min = with(LocalDensity.current) { maxHeight.toDp() })
                ) {
                    BankInfoItem(
                        bank = bank
                    )
                }
                AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxWidth(),
                    visible = isExpanded
                ) {
                    // Show actions when expanded, only show first 3 recommended and more actions
                    Column {
                        BankActionItem(
                            title = stringResource(R.string.banks_action_recommended_title),
                            actionDetailList = bank.actions.recommended.take(3),
                            onClickDetail = { actionType, url ->
                                onClickDetail(actionType, url)
                            }
                        )
                        BankActionItem(
                            title = stringResource(R.string.banks_action_More_title),
                            actionDetailList = bank.actions.more.take(3),
                            onClickDetail = { actionType, url ->
                                onClickDetail(actionType, url)
                            }
                        )
                    }
                }
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun BankInfoItem(
    bank: BankInfo,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 6.dp)
    ) {
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = bank.nameEn,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
        bank.descEn.takeIf { it.isNotEmpty() }?.let {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = bank.descEn,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun BankActionItem(
    title: String,
    actionDetailList: List<ActionDetail> = emptyList(),
    onClickDetail: (ActionType, String) -> Unit = {_, _ -> },
) {
    if (actionDetailList.isEmpty()) return
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Gray.copy(alpha = 0.3F))
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(8.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        actionDetailList.map {
            BankActionDetailItem(
                actionDetail = it,
                onClick = { actionType, url -> onClickDetail(actionType, url) }
            )
        }
    }
}

@Composable
fun BankActionDetailItem(
    actionDetail: ActionDetail,
    onClick: (ActionType, String) -> Unit = {_, _ -> },
) {
    Column(
        modifier = Modifier.clickableNoRipple {
            onClick.invoke(actionDetail.type, actionDetail.urlEn)
        }
    ) {
        if (actionDetail.type == ActionType.PHONE) {
            Text(
                text = AnnotatedString.fromHtml(actionDetail.titleEn),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = actionDetail.titleEn,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        HorizontalDivider(thickness = 0.5.dp)
    }
}

@Preview
@Composable
private fun BanksListScreenComposablePreview() {
    val sampleBank = BanksViewModel.BanksUiState(
        banks = listOf(
            BankInfo(
                nameEn = "Sample Bank 1",
                descEn = "This is a sample bank description.",
                actions = Action(
                    recommended = listOf(
                        ActionDetail("Action 1", ActionType.OTHERS, "url1"),
                        ActionDetail("Action 2", ActionType.OTHERS, "url2")
                    ),
                    more = listOf(
                        ActionDetail("More Action 1", ActionType.OTHERS, "url3"),
                        ActionDetail("More Action 2", ActionType.OTHERS, "url4")
                    )
                )
            ),
            BankInfo(
                nameEn = "Sample Bank 2",
                descEn = "This is another sample bank description.",
                actions = Action(
                    recommended = listOf(
                        ActionDetail("Action A", ActionType.OTHERS, "urlA"),
                        ActionDetail("Action B", ActionType.OTHERS, "urlB")
                    ),
                    more = listOf(
                        ActionDetail("More Action A", ActionType.OTHERS, "urlC"),
                        ActionDetail("More Action B", ActionType.OTHERS, "urlD")
                    )
                )
            ),
            BankInfo(
                nameEn = "Sample Bank 2",
                descEn = "",
                actions = Action(
                    recommended = listOf(
                        ActionDetail("Action A", ActionType.OTHERS, "urlA"),
                        ActionDetail("Action B", ActionType.OTHERS, "urlB")
                    ),
                    more = listOf(
                        ActionDetail("More Action A", ActionType.OTHERS, "urlC"),
                        ActionDetail("More Action B", ActionType.OTHERS, "urlD")
                    )
                )
            )
        )
    )

    sampleBank.Composable(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
    )
}