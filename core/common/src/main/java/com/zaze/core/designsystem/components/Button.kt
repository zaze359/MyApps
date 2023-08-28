package com.zaze.apps.compose.components

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BackIconButton(onBackPress: () -> Unit) {
    IconButton(onClick = onBackPress) {
        Icon(
            imageVector = mirroringBackIcon(),
            contentDescription = "back",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
@Preview
fun BackIconButtonPreview() {
    BackIconButton() {
        Log.i("Button", "BackIconButton onClick")
    }
}