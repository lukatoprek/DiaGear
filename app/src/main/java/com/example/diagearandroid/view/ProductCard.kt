package com.example.diagearandroid.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.ImageResult
import coil3.request.crossfade
import com.example.diagearandroid.R
import com.example.diagearandroid.model.Product

@Composable
fun ProductCard(product: Product, clicked: ()-> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{ clicked()}
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = ImageRequest
                        .Builder(LocalContext.current)
                        .data("https://t4.ftcdn.net/jpg/00/53/45/31/360_F_53453175_hVgYVz0WmvOXPd9CNzaUcwcibiGao3CL.jpg")
                        .build(),
                    contentDescription = product.name,
                )
                /*
                Text(
                    text = product.productId,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                */
            }
            /*
            Spacer(modifier = Modifier.padding(horizontal = 5.dp))
            Column(
                modifier = Modifier.fillMaxSize()
            ){
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = product.manufacturer,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = product.name.replaceAfter(',',"").replaceAfter(";",""),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            )
            }
            */
        }

    }
}