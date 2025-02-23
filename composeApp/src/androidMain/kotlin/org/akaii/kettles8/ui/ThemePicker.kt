package org.akaii.kettles8.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import org.akaii.kettles8.Config

@Composable
fun ThemePicker(
    config: Config,
    onSelect: (ColorSet) -> Unit,
    onDismiss: () -> Unit
) {
    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .background(Color(config.colorState.value.background))
                .padding(16.dp)
                .widthIn(max = 300.dp)
                .heightIn(max = 300.dp)
                .border(2.dp, Color(config.colorState.value.pixel), RoundedCornerShape(8.dp))
        ) {
            Text(
                "Theme",
                Modifier.padding(8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(config.colorState.value.pixel)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ColorSet.values.size) { colorSetIndex ->
                    val colorSet = ColorSet.values[colorSetIndex]
                    TextButton(
                        onClick = { onSelect(colorSet); onDismiss() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(colorSet.name, color = Color(config.colorState.value.pixel))
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(colorSet.background))
                                .border(2.dp, Color(colorSet.pixel))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(colorSet.pixel))
                                .border(2.dp, Color(colorSet.background))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}

