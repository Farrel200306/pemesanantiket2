package com.example.pemesanantiket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class StatusPesanan {
    BELUM_PESAN,
    NAMA_KOSONG,
    MEMPROSES,
    BERHASIL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketBookingScreen() {

    var hargaTiket by remember { mutableStateOf(75_000) }
    var jumlahTiket by remember { mutableStateOf(1) }
    var namaPembeli by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(StatusPesanan.BELUM_PESAN) }

    var prosesTrigger by remember { mutableStateOf(0) }

    val totalHarga = hargaTiket * jumlahTiket
    val isMemproses = status == StatusPesanan.MEMPROSES

    LaunchedEffect(prosesTrigger) {
        if (prosesTrigger > 0) {
            status = StatusPesanan.MEMPROSES
            delay(5000)
            status = StatusPesanan.BERHASIL
        }
    }

    val onPesanTiket: () -> Unit = {
        if (namaPembeli.isBlank()) {
            status = StatusPesanan.NAMA_KOSONG
        } else {
            prosesTrigger += 1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pemesanan Tiket", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1D4ED8),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    HargaTiketInfo(harga = hargaTiket, total = totalHarga)

                    Spacer(modifier = Modifier.height(16.dp))

                    NamaPembeliInput(
                        nama = namaPembeli,
                        onNamaChange = { baru ->
                            namaPembeli = baru
                            if (status == StatusPesanan.NAMA_KOSONG) {
                                status = StatusPesanan.BELUM_PESAN
                            }
                        },
                        enabled = !isMemproses
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    JumlahTiketCounter(
                        jumlah = jumlahTiket,
                        enabled = !isMemproses,
                        onTambah = { jumlahTiket++ },
                        onKurang = { if (jumlahTiket > 1) jumlahTiket-- }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PesanTiketButton(
                        isMemproses = isMemproses,
                        onClick = onPesanTiket
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    StatusPesananText(status = status)
                }
            }
        }
    }
}

@Composable
fun HargaTiketInfo(harga: Int, total: Int) {
    Column {
        Text("Harga per tiket", fontSize = 13.sp, color = Color.Gray)
        Text(
            "Rp ${"%,d".format(harga).replace(',', '.')}",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text("Total: Rp ${"%,d".format(total).replace(',', '.')}", fontSize = 14.sp, color = Color(0xFF1D4ED8))
    }
}

@Composable
fun NamaPembeliInput(nama: String, onNamaChange: (String) -> Unit, enabled: Boolean) {
    Column {
        Text("Nama", fontWeight = FontWeight.Medium, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = nama,
            onValueChange = onNamaChange,
            enabled = enabled,
            placeholder = { Text("Masukkan nama Anda") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun JumlahTiketCounter(
    jumlah: Int,
    enabled: Boolean,
    onTambah: () -> Unit,
    onKurang: () -> Unit
) {
    Column {
        Text("Jumlah Tiket", fontWeight = FontWeight.Medium, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onKurang, enabled = enabled && jumlah > 1) { Text("-") }
            Text(
                "$jumlah",
                modifier = Modifier.padding(horizontal = 20.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(onClick = onTambah, enabled = enabled) { Text("+") }
        }
    }
}

@Composable
fun PesanTiketButton(isMemproses: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isMemproses,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
    ) {
        if (isMemproses) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(if (isMemproses) "Memproses..." else "Pesan Tiket")
    }
}

@Composable
fun StatusPesananText(status: StatusPesanan) {
    val (bg, fg, teks, icon) = when (status) {
        StatusPesanan.BELUM_PESAN -> QuadStatus(Color(0xFFF3F4F6), Color.Gray, "Silakan pesan tiket", null)
        StatusPesanan.NAMA_KOSONG -> QuadStatus(Color(0xFFFEE2E2), Color(0xFFDC2626), "Nama harus diisi", Icons.Default.Error)
        StatusPesanan.MEMPROSES -> QuadStatus(Color(0xFFDBEAFE), Color(0xFF2563EB), "Memproses pesanan.........", null)
        StatusPesanan.BERHASIL -> QuadStatus(Color(0xFFDCFCE7), Color(0xFF16A34A), "Tiket telah dipesan!", Icons.Default.CheckCircle)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (status == StatusPesanan.MEMPROSES) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = fg, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
        } else if (icon != null) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text("Status: $teks", color = fg, fontSize = 13.sp)
    }
}

private data class QuadStatus(
    val bg: Color,
    val fg: Color,
    val teks: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector?
)